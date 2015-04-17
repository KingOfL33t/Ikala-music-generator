package musicgen;

import java.util.HashMap;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

/**
 * Used to generate music.
 *
 * @author Ches Burks
 *
 */
public class MusicGen {

	private Player player;

	/**
	 * Constructs a new music generator and initializes some variables.
	 */
	public MusicGen() {
		this.durationValues.put(1.0f, 0);
		this.durationValues.put(1.0f / 2, 1);
		this.durationValues.put(1.0f / 4, 2);
		this.durationValues.put(1.0f / 8, 3);
		this.durationValues.put(1.0f / 16, 4);
		this.durationValues.put(1.0f / 32, 5);
		player = new Player();
	}

	private final String[] notes = {"C", "D", "E", "F", "G", "A", "B"};
	private final String rest = "R";
	private final int minOctave = 0;
	private final int maxOctave = 9;
	private final String[] durations = {"w", "h", "q", "i", "s", "t"};
	private final HashMap<Float, Integer> durationValues = new HashMap<>();

	// settings
	/**
	 * the chance it will be a rest instead of a note
	 */
	private float restChance = .142f;
	/**
	 * how much the octave leans towards the center when deciding shifts
	 */
	private float octaveShiftVariance = 0.605f;
	/**
	 * the chance a note will not change
	 */
	private float noteChangeChanceSame = 0.558f;
	/**
	 * how many beats per measure
	 */
	private float timeTop = 4;
	/**
	 * how long a beat is
	 */
	private float timeBottom = 1.0f / 4;
	/**
	 * The chance that a beat will be split into sub-beats
	 */
	private float smallerBeatChance = 0.38f;
	/**
	 * Each step down creating smaller beats the chance to split again is
	 * reduced by this amount.
	 */
	private int smallBeatReductionFactor = 15;

	// for calculating
	private int currentOctave = 5;
	private int currentNote = 1;

	/**
	 * Increases the current note if possible by a random amount. This will not
	 * increase the current note above the maximum note for the octave.
	 */
	private void increaseNote() {
		int delta = RNG.getIntBetween(1, 3);
		if (this.currentNote + delta < this.notes.length - 1) {
			this.currentNote += delta;
		}
		else if (this.currentNote < this.notes.length - 1) {
			this.currentNote += 1;
		}

	}

	/**
	 * Decreases teh current note if possible by a random amount. This will not
	 * decrease the current note below the minimum note for the octave.
	 */
	private void decreaseNote() {
		int delta = RNG.getIntBetween(1, 3);
		if (this.currentNote - delta > 0) {
			this.currentNote -= delta;
		}
		else if (this.currentNote > 0) {
			this.currentNote -= 1;
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
		if (RNG.getBoolean(splitChance)) {
			if (duration / 2 >= 1.0f / 32) {
				String toReturn = "";
				toReturn +=
						this.getBeat(duration / 2, splitChance
								/ this.smallBeatReductionFactor);
				toReturn += " ";
				toReturn +=
						this.getBeat(duration / 2, splitChance
								/ this.smallBeatReductionFactor);
				return toReturn;
			}
		}
		String note = "";
		if (RNG.getBoolean(this.restChance)) {
			// Rest
			note += this.rest;
		}
		else {
			// Note
			if (!RNG.getBoolean(this.noteChangeChanceSame)) {
				if (RNG.getBoolean()) {
					this.increaseNote();
				}
				else {
					this.decreaseNote();
				}
			}// TODO clean up the note generation code
				// TODO perhaps add different keys
			if (this.currentNote == 1) {
				this.currentNote = 0;
			}
			else if (this.currentNote == 3) {
				this.currentNote = 2;// TODO replace with the musical key values
										// not
				// hard coded note values
			}
			else if (this.currentNote >= 6) {
				this.currentNote = 5;
			}
			note += this.notes[this.currentNote];
			// make it sharp or flat
			/*
			 * if (RNG.getBoolean(sharpModChance)) { if
			 * (RNG.getBoolean(sharpChance)) { note += sharp; } else { note +=
			 * flat; } }
			 */
			// add the octive number
			note += this.currentOctave;
		}
		// Length
		note += this.durations[this.durationValues.get(duration)];
		return note;
	}

	/**
	 * Changes the octave randomly, but always leans towards the middle, so the
	 * octave will tend to stay around the center octave depending on how
	 * strongly it is pulled to the middle.
	 */
	private void shiftOctave() {
		int halfWayOctive = (this.minOctave + this.maxOctave) / 2;
		if (this.currentOctave < halfWayOctive) {
			// want to shift up more
			if (RNG.getBoolean(0.5f + this.octaveShiftVariance)) {
				++this.currentOctave;
			}
			else {
				--this.currentOctave;
			}
		}
		else {
			// want to shift down more
			if (RNG.getBoolean(0.5f + this.octaveShiftVariance)) {
				--this.currentOctave;
			}
			else {
				++this.currentOctave;
			}
		}

		// bounds check
		if (this.currentOctave < this.minOctave) {
			this.currentOctave = this.minOctave;
		}
		if (this.currentOctave > this.maxOctave) {
			this.currentOctave = this.maxOctave;
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
		if (this.timeTop < baseValue) {
			baseValue = this.timeTop;
		}
		// invalid value
		int numElements = -1;

		while ((numElements & (numElements - 1)) != 0
				|| !this.durationValues.containsKey(this.timeBottom
						/ numElements)) {
			// not a power of 2
			numElements =
					RNG.getWeightedIntBetween(1,
							(int) (baseValue / (1.0f / 32)), 1);
		}
		for (i = 0; i < numElements; ++i) {
			if (this.timeTop / numElements > 1) {
				measure += this.getBeat(1.0f, this.smallerBeatChance);
			}
			else {
				measure +=
						this.getBeat(this.timeTop / numElements,
								this.smallerBeatChance);
			}

			measure += " ";
		}

		if (RNG.getBoolean(0.4f)) {
			this.shiftOctave();
		}
		return measure;
	}

	// TODO add a drum beat gene
	/**
	 * Builds a song with the given number of measures.
	 *
	 * @param measures how many measures to generate
	 * @return the newly generated song
	 */
	private Pattern buildSong(int measures) {
		String tmp = "";
		Pattern song = new Pattern();
		song.add(new Pattern("V0 I0"));// piano on voice 1
		for (int i = 0; i < measures; ++i) {
			/*
			 * tmp = "V0 " + this.buildPianoMeasure() +
			 * "V9 L0 [TAMBOURINE]i [TAMBOURINE]i [TAMBOURINE]i [TAMBOURINE]s [TAMBOURINE]s [TAMBOURINE]i [TAMBOURINE]i [TAMBOURINE]i [TAMBOURINE]i "
			 * ; tmp +=
			 * "V9 L1 [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i [OPEN_HI_HAT]i "
			 * ; tmp += "V9 L2 Rq Rq [ELECTRIC_SNARE]q Rq "; tmp +=
			 * "V9 L3 [BASS_DRUM]q Ri [BASS_DRUM]i Rq [BASS_DRUM]q ";
			 * System.out.print(tmp);
			 */
			tmp = "V0 " + this.buildPianoMeasure();
			song.add(new Pattern(tmp));
		}
		// System.out.println();
		return song;
	}

	/**
	 * Creates and plays a new song, if it is not already playing one.
	 */
	public void play() {
		if (player.getManagedPlayer().isPlaying()) {
			return;
		}
		Genome ggnome = new Genome();
		ggnome.setGene(1, new Gene((byte) 1, Gene.VAL_NOTE_LENGTH[3]));
		ggnome.setGene(2, new Gene((byte) 2, Gene.VAL_BEAT_SPLIT[4]));
		Pattern p = getSong(ggnome);
		player.play(p);
	}

	// play notes 1 3 5 6 of a major scale

	/**
	 * Creates a new song using information from a genome.
	 *
	 * @param genome the genes to use in generation of the song
	 * @return the newly created song
	 */
	Pattern getSong(Genome genome) {
		int length = genome.getGene(1).getValue();
		for (float f : this.durationValues.keySet()) {
			if (this.durationValues.get(f) == length) {
				this.timeBottom = f;
				this.timeTop = this.timeBottom * 4;
			}
		}
		int split = genome.getGene(2).getValue();
		switch (split) {
		case 0:
			this.smallerBeatChance = 0.0f;
			this.smallBeatReductionFactor = 999999;
			break;
		case 1:
			this.smallerBeatChance = 0.05f;
			this.smallBeatReductionFactor = 6;
			break;
		case 2:
			this.smallerBeatChance = 0.25f;
			this.smallBeatReductionFactor = 64;
			break;
		case 3:
			this.smallerBeatChance = 0.25f;
			this.smallBeatReductionFactor = 64;
			break;
		case 4:
			this.smallerBeatChance = 0.5f;
			this.smallBeatReductionFactor = 2;
			break;
		case 5:
			this.smallerBeatChance = 0.75f;
			this.smallBeatReductionFactor = 64;
			break;

		case 6:
			this.smallerBeatChance = 0.75f;
			this.smallBeatReductionFactor = 64;
			break;
		case 7:
			this.smallerBeatChance = 0.9f;
			this.smallBeatReductionFactor = 6;
			break;
		case 8:
			this.smallerBeatChance = 1.0f;
			this.smallBeatReductionFactor = 1;
			break;
		}

		Pattern p = this.buildSong(10);
		return p;
	}
}
