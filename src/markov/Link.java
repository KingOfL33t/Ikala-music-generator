package markov;

import java.util.Collections;
import java.util.LinkedList;

/**
 * A link in the Markov chain. This holds a word and a list of {@link Child
 * children} that have followed it in sequence.
 *
 * @author Ches Burks
 *
 */
public class Link implements Comparable<Link> {
	/**
	 * A list of strings that have followed the current word and how many times
	 * they have occurred
	 */
	private LinkedList<Child> children = new LinkedList<>();
	private final String current;

	/**
	 * Creates a link for this word with no children.
	 *
	 * @param word the base word for this probability list
	 */
	public Link(String word) {
		this.current = word;
	}

	/**
	 * Returns the word this link tracks.
	 *
	 * @return the word this link stores subsequent word probabilities for
	 */
	public String getWord() {
		return this.current;
	}

	/**
	 * Stores the given word in the list of next words. If the word does not
	 * exist, it is added with a count of 1, otherwise the occurrence count for
	 * the child tracking that word is incremented. Sorts the list if a new
	 * element is added.
	 *
	 * @param word the word to store
	 */
	public void learnWord(String word) {
		boolean childFound = false;// if it found an entry
		for (Child child : this.children) {
			if (child.getWord().equals(word)) {
				child.incrementCount();
				childFound = true;
			}
			if (childFound) {
				break;
			}
		}
		if (!childFound) {
			Child newChild = new Child(word);
			this.children.add(newChild);
			Collections.sort(this.children);
		}
	}

	/**
	 * Adds the child to the list or updates the existing occurrence count to
	 * the new one if it already is in the list. Sorts if a new item is added.
	 *
	 * @param toAdd the child to add
	 */
	public void add(Child toAdd) {
		boolean childFound = false;// if it found an entry
		for (Child child : this.children) {
			if (child.getWord().equals(toAdd.getWord())) {
				child.setOccuranceCount(toAdd.getOccuranceCount());
				childFound = true;
			}
			if (childFound) {
				break;
			}
		}
		if (!childFound) {
			this.children.add(toAdd);
			Collections.sort(this.children);
		}
	}

	/**
	 * Returns the chain probability list for the string this link tracks. This
	 * list is stored as a sorted linked list.
	 *
	 * @return the child list for this word
	 */
	public LinkedList<Child> getList() {
		return this.children;
	}

	@Override
	public int compareTo(Link o) {
		return this.current.compareTo(o.getWord());
	}
}
