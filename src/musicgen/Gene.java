
package musicgen;


/**
 * An interface for classes that make up the genome. Genes have a value
 * associated with them and can mutate or mix with other genes.
 *
 * @author Ches Burks
 *
 */
public class Gene {

	/**
	 * Constructs a new Gene with the supplied ID. A random valid value
	 * is assigned to the gene.
	 * @param id the id of the gene
	 * @see #Gene(byte, byte)
	 */
	public Gene(byte id){
		this(id, getRandomValidValue(id));
	}

	/**
	 * Constructs a new Gene with the supplied ID and value.
	 * @param id the id of the gene
	 * @param value the value of the gene
	 */
	public Gene(byte id, byte value){
		if (id >= VALUE_MAP.length - 1 || id < 0){
			//TODO throw an error
		}
		this.id = id;
		if (contains(id, value)) {
			this.currentValue = value;
		}
		else {
			//TODO throw an error
		}

	}

	/**
	 * The different voices are:
	 *
	 *
	 * <ol start="0">
	 * <li>0 (PIANO or ACOUSTIC_GRAND)</li>
	 * <li>9 (GLOCKENSPIEL)</li>
	 * <li>16 (DRAWBAR_ORGAN)</li>
	 * <li>34 (ELECTRIC_BASS_PICK)</li>
	 * <li>38 (SYNTH_BASS_1)</li>
	 * <li>40 (VIOLIN)</li>
	 * <li>44 (TREMOLO_STRINGS)</li>
	 * <li>47 (TIMPANI)</li>
	 * <li>48 (STRING_ENSEMBLE_1)</li>
	 * <li>74 (RECORDER)</li>
	 * <li>81 (LEAD_SAWTOOTH or SAWTOOTH)</li>
	 * <li>94 (PAD_HALO or HALO)</li>
	 * <li>95 (PAD_SWEEP or SWEEP)</li>
	 * <li>106 (SHAMISEN)</li>
	 * <li>110 (FIDDLE)</li>
	 * </ol>
	 *
	 */
	public static final byte[] VAL_VOICES = {0, 9, 16, 34, 38, 40, 44, 47, 48,
			74, 81, 94, 95, 106, 110};
	/**
	 * An array of id's and their values. The IDs are:
	 * <ol start="0">
	 * <li>{@link #VAL_VOICES Voices}</li>
	 * </ol>
	 *
	 */
	public static final byte[][] VALUE_MAP = {VAL_VOICES};

	private byte currentValue = 0;
	/**
	 * The ID identifies what gene this is. Each ID identifies a specific trait.
	 *
	 * @see #VALUE_MAP
	 */
	private byte id = 0;

	private boolean contains(byte id, byte value){
		byte[] bytes = VALUE_MAP[id];
		for (int i = 0; i < bytes.length; ++i){
			if (bytes[i] == value){
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns the current value of this gene.
	 *
	 * @return the value of this gene
	 */
	public byte getValue() {
		return currentValue;
	}

	/**
	 * Returns the ID of this gene. The different ID values are: <blockquote>
	 * <ol start="0">
	 * <li>{@link #VAL_VOICES Voices}</li>
	 * </ol>
	 * </blockquote>
	 *
	 * @return the ID of the gene
	 */
	public byte getID() {
		return id;
	}

	/**
	 * Sets the current value of this gene, if the given value is valid. Invalid
	 * values will not change the value.
	 *
	 * @param val the new value to use
	 */
	public void setValue(byte val) {
		if (contains(id, val)) {
			currentValue = val;
		}
	}

	/**
	 * Randomly changes the value of this gene to one of the valid values.
	 */
	public void mutate() {
		// picks a random value from the array
		currentValue = getRandomValidValue(id);
	}

	/**
	 * Returns a list of valid values for the given gene ID.
	 *
	 * @param id The ID to get values for
	 * @return values that gene can have
	 */
	public static byte[] getValidValues(int id) {
		return VALUE_MAP[id];
	}

	/**
	 * Returns a random valid values from the list of values the given gene ID
	 * can have.
	 *
	 * @param id The ID to get a value for
	 * @return a random value that gene can have
	 */
	public static byte getRandomValidValue(int id) {
		return VALUE_MAP[id][RNG.getIntBetween(0, VALUE_MAP[id].length - 1)];
	}

	/**
	 * Returns the result if this gene is crossed with another gene. The end
	 * result is typically going to be one of the two values. This does not
	 * modify the value of either gene.
	 *
	 * @param other the gene to cross with
	 * @return the new gene that would be created
	 */
	public Gene crossWith(Gene other) {
		if (other.getID() != this.getID()) {
			// TODO they cannot be crossed
		}

		return null;
	}
}
