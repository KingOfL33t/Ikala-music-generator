package midi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Parses the data from mid2asc.exe output files into a form the program can
 * use.
 *
 * @author Ches Burks
 *
 */
public class AsciiParser {

	/**
	 * Takes an input file and creates a song from the contents and returns it.
	 * If the file is not valid or it cannot be read for some reason, null is
	 * returned.
	 * 
	 * @param input the file to read input from to create a Song.
	 * @return the newly created song, or null if there was a problem creating
	 *         one
	 */
	public static Song parse(File input) {
		FileReader fReader;
		try {
			fReader = new FileReader(input);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		BufferedReader reader = new BufferedReader(fReader);
		String line;

		int format;
		int tracks;
		int division;
		ArrayDeque<Line> songLines = new ArrayDeque<>();
		ArrayDeque<Line> parsedLines = new ArrayDeque<>();

		try {
			line = reader.readLine();
			line = line.replaceFirst("format=", "");
			format = Integer.parseInt(line.substring(0, line.indexOf(" ")));
			line = line.substring(line.indexOf("tracks=") + 7);
			tracks = Integer.parseInt(line.substring(0, line.indexOf(" ")));
			line = line.substring(line.indexOf("division=") + 9);
			division = Integer.parseInt(line);

			// Scan in the rest of the lines
			line = reader.readLine();
			while (line != null) {
				if (line.isEmpty()) {
					line = reader.readLine();
					continue;
				}
				else if (line.startsWith("#")) {
					// its a comment
					// TODO handle these for track separated
				}
				else {
					Line l1 = new Line(line);
					songLines.add(l1);
				}
				line = reader.readLine();
			}
			reader.close();

		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		HashMap<Integer, HashSet<Integer>> ignoredChannels = new HashMap<>();
		// parse the lines
		while (!songLines.isEmpty()) {
			Line l1 = songLines.remove();
			l1.process();
			if (l1.getBar() == -1 || l1.getChannel() == -1
					|| l1.getCrotchet() == -1 || l1.getTrack() == -1
					|| l1.getInstruction() == "") {
				l1.printData();
				continue;// invalid line
			}
			if (l1.getInstruction().matches(Line.regexInstrInstrument)) {
				String inst = l1.getInstruction();
				inst = inst.replaceFirst("Instrument +", "");
				int instrumentID = Integer.parseInt(inst);
				if (instrumentID < 1 || instrumentID > 8) {// Not a piano
					if (!ignoredChannels.containsKey(l1.getTrack())) {
						ignoredChannels.put(l1.getTrack(), new HashSet<>());
					}
					// will not add twice
					ignoredChannels.get(l1.getTrack()).add(l1.getChannel());
					continue;// stop reading this line
				}
			}
			if (ignoredChannels.containsKey(l1.getTrack())) {
				if (ignoredChannels.get(l1.getTrack())
						.contains(l1.getChannel())) {
					continue;// Don't bother storing non-piano instruments
				}
			}
			parsedLines.add(l1);
		}

		// Create the song
		Song song = new Song(format, tracks, division);

		while (!parsedLines.isEmpty()) {
			song.addLine(parsedLines.remove());
		}

		return song;
	}
}
