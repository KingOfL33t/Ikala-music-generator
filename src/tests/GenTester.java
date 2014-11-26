
package tests;

import musicgen.RNG;

import org.junit.Test;

/**
 * Used for testing the generation of music
 * @author Ches Burks
 *
 */
public class GenTester {

	/**
	 * Tests the distribution curves of the rng
	 */
	@Test
	public void distribution() {

		int i;
		int j;
		int min = 0;
		int max = 100;
		int interations = 1000;
		int tmp;
		int[] numbers = new int[max + 1];
		for (i = min; i <= interations; ++i) {
			tmp = RNG.getWeightedIntBetween(min, max, (max + min) / 2);
			++numbers[tmp];
		}
		for (i = min; i < numbers.length; ++i) {
			System.out.print(String.format("%04d", i));
			System.out.print(":");
			for (j = 0; j < numbers[i]; ++j) {
				System.out.print("*");
			}
			System.out.println();
		}
	}

}
