package musicgen;

/**
 * Contains a list of genes which can mutate or cross with others to create
 * new genomes. A genome cannot have more than 128 genes.
 * @author Ches Burks
 *
 */
public class Genome {
	private Gene[] genes;
	/**
	 * The chance PER GENE that a gene will mutate.
	 */
	private static final float mutationChance = 0.01f;

	/**
	 * Constructs a new genome with random genes.
	 */
	public Genome(){
		genes = new Gene[Gene.VALUE_MAP.length];
		int i;
		for (i = 0; i < genes.length; ++i){
			if (i > Byte.MAX_VALUE || i < 0){
				//TODO throw an error
			}
			genes[i] = new Gene((byte)i);
		}
	}

	/**
	 * Returns the gene at the given position, if one exists. If the ID
	 * is outside the range of valid id's, null is returned.
	 *
	 * @param id the id of the gene
	 * @return the gene at that index
	 */
	public Gene getGene(int id){
		if (id >= 0 && id < genes.length){
			return genes[id];
		}
		return null;
	}

	@Override
	public String toString(){
		String out = "[";
		int i;
		for (i = 0; i < genes.length - 1; ++i){
			out += genes[i].getValue() + ",";
		}
		out += genes[genes.length - 1].getValue();
		out += "]";
		return out;
	}

}
