
package markov;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Holds a list of {@link Link links}.
 * 
 * @author Ches Burks
 *
 */
public class Chain {
	/**
	 * A list of words that are known to the program
	 */
	private LinkedList<Link> knownWords = new LinkedList<Link>();
	private HashSet<String> words = new HashSet<String>();

	/**
	 * Stores the link between the first and second word in the list of known
	 * words. A word is any string delimited by one or more whitespace
	 * characters, typically a single space. Sorts the list if a new link is
	 * inserted.
	 * 
	 * @param first the first word
	 * @param second the word right after the first word.
	 */
	public void learnWordLink(String first, String second) {
		boolean learned = false;
		for (Link lnk : knownWords) {
			if (lnk.getWord().equals(first)) {
				/*
				 * case sensitive so that different capitalizations are kept as
				 * different entries.
				 */
				lnk.learnWord(second);
				learned = true;
			}
			if (learned)
				break;
		}
		if (!learned) {
			/*
			 * The first word has not been seen before.
			 */
			Link newLink = new Link(first);
			newLink.learnWord(second);
			knownWords.add(newLink);
			words.add(first);
		}
	}

	/**
	 * Clears the lists
	 */
	public void clear() {
		words.clear();
		knownWords.clear();
	}

	/**
	 * Adds the given link to the chain
	 * 
	 * @param newLink the link to add
	 */
	public void add(Link newLink) {
		if (words.contains(newLink.getWord())) {
			// way faster than looping through thelinked list every add
			for (Link link : knownWords) {
				if (link.getWord().equals(newLink.getWord())) {
					for (Child child : newLink.getList()) {
						link.add(child);
					}
					return;
				}
			}
		}
		knownWords.add(newLink);
		words.add(newLink.getWord());

	}

	/**
	 * Returns true if the word has been encountered before.
	 * 
	 * @param word the word
	 * @return true if the word exists
	 */
	public boolean hasWord(String word) {
		return words.contains(word);
	}

	/**
	 * Sorts the list. called after loading from file.
	 */
	public void sortList() {
		Collections.sort(knownWords);
	}

	/**
	 * Returns the linked list of known words.
	 * 
	 * @return the known words
	 */
	public LinkedList<Link> getKnownWords() {
		return knownWords;
	}
}
