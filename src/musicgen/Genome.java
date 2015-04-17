package musicgen;

/**
 * Contains a list of genes which can mutate or cross with others to create new
 * genomes. A genome cannot have more than 128 genes.
 * 
 * @author Ches Burks
 *
 */
class Genome {
	private Gene[] genes;
	/**
	 * The chance PER GENE that a gene will mutate.
	 */
	private static final float mutationChance = 0.01f;

	/**
	 * Constructs a new genome with random genes.
	 */
	public Genome() {
		this.genes = new Gene[Gene.VALUE_MAP.length];
		int i;
		for (i = 0; i < this.genes.length; ++i) {
			this.genes[i] = new Gene((byte) i);
		}
	}

	/**
	 * Returns the gene at the given position, if one exists. If the ID is
	 * outside the range of valid id's, null is returned.
	 *
	 * @param id the id of the gene
	 * @return the gene at that index
	 */
	public Gene getGene(int id) {
		if (id >= 0 && id < this.genes.length) {
			return this.genes[id];
		}
		return null;
	}

	/**
	 * Sets the gene at the given position, assuming it is a valid position.
	 * Warning! Currently does not check for gene valdidity, only index
	 * validity.
	 * 
	 * @param id the position of the gene
	 * @param gene the gene to set
	 */
	public void setGene(int id, Gene gene) {
		if (id >= 0 && id < this.genes.length) {
			this.genes[id] = gene;
		}// TODO gene value checking
	}

	/**
	 * Searches through the entire genome and randomly applies mutations. This
	 * does not guarantee any values changed.
	 */
	public void mutate() {
		int i;
		for (i = 0; i < this.genes.length; ++i) {
			if (RNG.getBoolean(Genome.mutationChance)) {
				this.genes[i].mutate();
			}
		}
	}

	@Override
	public String toString() {
		String out = "[";
		int i;
		for (i = 0; i < this.genes.length - 1; ++i) {
			out += this.genes[i].getValue() + ",";
		}
		out += this.genes[this.genes.length - 1].getValue();
		out += "]";
		return out;
	}

}
