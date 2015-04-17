package markov;

/**
 * Stores a word and a count for occurrences of that word following another.
 *
 * @author Ches Burks
 *
 */
public class Child implements Comparable<Child> {
	/**
	 * The word this child stores
	 */
	private final String contents;
	/**
	 * How many times the word has been observed directly following its parent.
	 * These numbers are used to calculate the probability of a specific word
	 * occurring after its parent in sequence.
	 */
	private int occurenceCount;

	/**
	 * Creates a child for recording occurrences of the given string. Occurrence
	 * count is set to 1 by default.
	 *
	 * @param word the string to track
	 */
	public Child(String word) {
		this.contents = word;
		this.occurenceCount = 1;
	}

	/**
	 * Returns the word this child stores counts for.
	 *
	 * @return the string to track
	 */
	public String getWord() {
		return this.contents;
	}

	/**
	 * How many times the word has been observed directly following its parent.
	 * These numbers are used to calculate the probability of a specific word
	 * occurring after its parent in sequence.
	 *
	 * @return the occurrence count
	 */
	public int getOccuranceCount() {
		return this.occurenceCount;
	}

	/**
	 * Increase the occurrence count by one.
	 */
	public void incrementCount() {
		++this.occurenceCount;
	}

	/**
	 * Sets the number times the word has been observed directly following its
	 * parent. These numbers are used to calculate the probability of a specific
	 * word occurring after its parent in sequence.
	 *
	 * @param count the new occurrence count
	 */
	public void setOccuranceCount(int count) {
		this.occurenceCount = count;
	}

	@Override
	public int compareTo(Child o) {
		return this.contents.compareTo(o.getWord());
	}

}
