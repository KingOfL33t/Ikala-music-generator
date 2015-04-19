package midi;

import java.util.ArrayList;

/**
 * Represents a midi song loaded from file.
 * 
 * @author Ches Burks
 *
 */
public class Song {
	private int numTracks;
	private int midiFormat;
	private int numDivisions;
	private ArrayList<Line> lines;

	/**
	 * Creates a song with no lines and the given information.
	 * 
	 * @param format the format of the midi file
	 * @param tracks how many tracks this song originally had
	 * @param divisions overall division of crotchets (fractional times must
	 *            divide into this)
	 */
	public Song(int format, int tracks, int divisions) {
		this.midiFormat = format;
		this.numTracks = tracks;
		this.numDivisions = divisions;
		this.lines = new ArrayList<>();
	}

	/**
	 * Adds the given line to the end of the song.
	 * 
	 * @param toAdd the line to add
	 */
	public void addLine(Line toAdd) {
		this.lines.add(toAdd);
	}

	/**
	 * Outputs information about this song to System.out
	 */
	public void printData() {
		System.out.print("Tracks: " + this.numTracks + " ");
		System.out.print("Format: " + this.midiFormat + " ");
		System.out.print("Divisions: " + this.numDivisions + " ");
		System.out.println();
		System.out.println("Num Lines:" + this.lines.size());
	}
}
