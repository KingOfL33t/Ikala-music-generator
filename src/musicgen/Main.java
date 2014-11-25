package musicgen;

import java.util.HashMap;

import org.jfugue.Pattern;
import org.jfugue.Player;

public class Main {
	public static void main(String[] args) {
		Main main = new Main();
		main.start();
	}

	public Main() {
		durationValues.put(1.0f, 0);
		durationValues.put(1.0f / 2, 1);
		durationValues.put(1.0f / 4, 2);
		durationValues.put(1.0f / 8, 3);
		durationValues.put(1.0f / 16, 4);
		durationValues.put(1.0f / 32, 5);
	}

	private Generator generator;
	private final String[] notes = { "C", "D", "E", "F", "G", "A", "B" };
	private final String rest = "R";
	private final String flat = "b";
	private final String sharp = "#";
	private final int minOctave = 0;
	private final int maxOctave = 9;
	private final String[] durations = { "w", "h", "q", "i", "s", "t" };
	private final HashMap<Float, Integer> durationValues = new HashMap<Float, Integer>();

	// settings
	/**
	 * the chance it will be a rest instead of a note
	 */
	private float restChance = .07f;
	/**
	 * chance of a sharp or a flat
	 */
	private float sharpModChance = .1f;
	/**
	 * if a note is modified, the chance that modification is a sharp
	 */
	private float sharpChance = .5f;
	/**
	 * how much the octave leans towards the center when deciding shifts
	 */
	private float octaveShiftVariance = 0.2f;
	/**
	 * the chance a note will not change
	 */
	private float noteChangeChanceSame = 0.06f;
	/**
	 * how many beats per measure
	 */
	private float timeTop = 1;
	/**
	 * how long a beat is
	 */
	private float timeBottom = 1.0f / 4;
	/**
	 * The chance that a beat will be split into sub-beats
	 */
	private float smallerBeatChance = 0.2f;
	/**
	 * Each step down creating smaller beats the chance to split again is
	 * reduced by this amount.
	 */
	private int smallBeatReductionFactor = 16;

	// for calculating
	private int currentOctave = 5;
	private int currentNote = 1;

	/**
	 * Initializes the random number generator.
	 */
	private void init() {
		// get the current time of the system
		long time1 = System.nanoTime();
		/*
		 * construct a random number of integers based on the last 3 digits of
		 * the system time this will take a different amount of time based on
		 * what value the hash was.
		 */
		for (int i = 0; i <= time1 % 1000; ++i) {
			@SuppressWarnings("unused")
			// not supposed to be used, int x is supposed to be inside the loop
			int x = i + 2;// its value is trashed
		}
		// get the current time again
		long time2 = System.nanoTime();

		long deltaTime = time2 - time1;

		int timeAsInt;
		// make sure the long time can fit into an integer
		while (deltaTime > Integer.MAX_VALUE) {
			deltaTime = deltaTime - Integer.MAX_VALUE;
		}
		timeAsInt = (int) deltaTime;
		generator = new Generator(timeAsInt);
	}

	/**
	 * Increases the current note if possible by a random amount. This will not
	 * increase the current note above the maximum note for the octave.
	 */
	private void increaseNote() {
		int delta = generator.getIntBetween(1, 3);
		if (currentNote + delta < notes.length - 1) {
			currentNote += delta;
		} else if (currentNote < notes.length - 1) {
			currentNote += 1;
		}
	}

	/**
	 * Decreases teh current note if possible by a random amount. This will not
	 * decrease the current note below the minimum note for the octave.
	 */
	private void decreaseNote() {
		int delta = generator.getIntBetween(1, 3);
		if (currentNote - delta > 0) {
			currentNote -= delta;
		} else if (currentNote > 0) {
			currentNote -= 1;
		}
	}

	/**
	 * Returns a string representing notes that, summed up, have a duration
	 * equal to the supplied duration.
	 *
	 * @param duration the duration to generate beats for
	 * @param splitChance the chance of splitting into sub-beats
	 * @return the generated string
	 */
	private String getBeat(float duration, float splitChance) {
		if (generator.getBoolean(splitChance)) {
			if (duration / 2 >= 1.0f / 32) {
				String toReturn = "";
				toReturn += getBeat(duration / 2, splitChance
						/ smallBeatReductionFactor);
				toReturn += " ";
				toReturn += getBeat(duration / 2, splitChance
						/ smallBeatReductionFactor);
				return toReturn;
			}
		}
		String note = "";
		if (generator.getBoolean(restChance)) {
			// Rest
			note += rest;
		} else {
			// Note
			if (!generator.getBoolean(noteChangeChanceSame)) {
				if (generator.getBoolean()) {
					increaseNote();
				} else {
					decreaseNote();
				}
			}
			note += notes[currentNote];
			// make it sharp or flat
			if (generator.getBoolean(sharpModChance)) {
				if (generator.getBoolean(sharpChance)) {
					note += sharp;
				} else {
					note += flat;
				}
			}
			// add the octive number
			note += currentOctave;
		}

		// Length
		note += durations[durationValues.get(duration)];
		return note;
	}

	/**
	 * Changes the octave randomly, but always leans towards the middle, so
	 * the octave will tend to stay around the center octave depending on
	 * how strongly it is pulled to the middle.
	 */
	private void shiftOctave() {
		int halfWayOctive = (minOctave + maxOctave) / 2;
		if (currentOctave < halfWayOctive) {
			// want to shift up more
			if (generator.getBoolean(0.5f + octaveShiftVariance)) {
				++currentOctave;
			} else {
				--currentOctave;
			}
		} else {
			// want to shift down more
			if (generator.getBoolean(0.5f + octaveShiftVariance)) {
				--currentOctave;
			} else {
				++currentOctave;
			}
		}

		// bounds check
		if (currentOctave < minOctave) {
			currentOctave = minOctave;
		}
		if (currentOctave > maxOctave) {
			currentOctave = maxOctave;
		}
	}

	/**
	 * Builds a measure of piano music and returns the string that represents
	 * the notes.
	 *
	 * @return a string of notes to play
	 */
	private String buildPianoMeasure() {
		String measure = "";
		int i;

		float baseValue = 1.0f;
		if (timeTop < baseValue) {
			baseValue = timeTop;
		}
		// invalid value
		int numElements = -1;

		while ((numElements & (numElements - 1)) != 0
				|| !durationValues.containsKey(timeBottom / numElements)) {
			// not a power of 2
			numElements = generator.getWeightedIntBetween(1,
					(int) (baseValue / (1.0f / 32)), 1);
		}
		for (i = 0; i < numElements; ++i) {
			measure += getBeat(timeTop / numElements, smallerBeatChance);
			measure += " ";
		}

		if (generator.getBoolean(0.4f)) {
			shiftOctave();
		}
		return measure;
	}

	/**
	 * Builds a song with the given number of measures.
	 *
	 * @param measures how many measures to generate
	 * @return the newly generated song
	 */
	private Pattern buildPianoSong(int measures) {
		String tmp = "";
		Pattern song = new Pattern();
		for (int i = 0; i < measures; ++i) {
			tmp = buildPianoMeasure();
			System.out.println(tmp);
			song.add(new Pattern(tmp));
		}
		return song;
	}

	/**
	 * Initializes and plays a short song.
	 */
	public void start() {
		init();
		Player player = new Player();
		player.play(buildPianoSong(10));
	}
}
