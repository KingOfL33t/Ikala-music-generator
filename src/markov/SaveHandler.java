package markov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import midi.AsciiParser;
import midi.Song;

/**
 * Handles saving chain data to and loading chain data from file.
 *
 * @author Ches Burks
 *
 */
public class SaveHandler {

	/**
	 * Reads the stored input from file
	 *
	 * @param toLoad the chain to load to
	 * @param location the file to load from
	 * @throws Exception if the file is not valid
	 */
	public static void loadChainStorageToChain(Chain toLoad, File location)
			throws Exception {
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(location));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		try {
			String inputLine = "";
			String[] parts;
			int count = 0;
			String currentString = "";
			Link newLink = null;
			char added = 0;
			try {
				if (in.ready()) {
					++count;
					inputLine = in.readLine();
					parts = inputLine.split(" ");
					currentString = parts[0];
					newLink = new Link(currentString);
				}
				while (in.ready()) {
					added = 0;
					++count;
					inputLine = in.readLine();
					parts = inputLine.split(" ");
					if (!parts[0].equals(currentString)) {
						toLoad.add(newLink);
						currentString = parts[0];
						newLink = new Link(currentString);
						added = 1;
					}
					Child newChild = new Child(parts[1]);
					newChild.setOccuranceCount(Integer.parseInt(parts[2]));
					if (newLink != null) {
						newLink.add(newChild);
					}
				}
				if (added == 0) {
					toLoad.add(newLink);
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("at input file line number " + count);
				toLoad.clear();
				in.close();
				throw new Exception("Invalid input");
			}

			in.close();
			toLoad.sortList();
		}
		catch (IOException e) {
			e.printStackTrace();
			toLoad.clear();
			throw new Exception("Invalid input");
		}
	}

	/**
	 * Reads a MIDI file, parses it, and creates markov chain data from it. Adds
	 * the data to the chain.
	 *
	 * @param location the file to load from
	 * @return the song that was loaded from the midi text file
	 */
	public static Song loadSong(File location) {
		Song song;
		song = AsciiParser.parse(location);
		return song;
	}

	/**
	 * Stores the chain in a file.
	 *
	 * @param toSave the chain to save
	 * @param location the file to store the data in
	 */
	public static void saveChain(Chain toSave, File location) {
		PrintWriter out;
		try {
			out = new PrintWriter(location);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		for (Link link : toSave.getKnownWords()) {
			for (Child child : link.getList()) {
				out.println(link.getWord() + " " + child.getWord() + " "
						+ child.getOccuranceCount());
			}
		}
		out.close();
	}
}
