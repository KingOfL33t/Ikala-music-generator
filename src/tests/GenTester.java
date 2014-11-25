package tests;

import musicgen.Generator;

import org.junit.Test;

public class GenTester {

	@Test
	public void distribution() {

		Generator generator;
		// get the current time of the system
		long time1 = System.nanoTime();
		/*
		 * construct a random number of integers based on the last 3 digits of
		 * the system time this will take a different amount of time based on
		 * what value the hash was.
		 */
		for (int i = 0; i <= time1 % 1000; ++i) {
			@SuppressWarnings("unused")
			// not supposed to be used, int x is supposed to be inside the loop
			int x = i + 2;// its value is trashed
		}
		// get the current time again
		long time2 = System.nanoTime();

		long deltaTime = time2 - time1;

		int timeAsInt;
		// make sure the long time can fit into an integer
		while (deltaTime > Integer.MAX_VALUE) {
			deltaTime = deltaTime - Integer.MAX_VALUE;
		}
		timeAsInt = (int) deltaTime;
		generator = new Generator(timeAsInt);

		int i;
		int j;
		int min = 0;
		int max = 100;
		int interations = 1000;
		int tmp;
		int[] numbers = new int[max + 1];
		for (i = min; i <= interations; ++i){
			tmp = generator.getWeightedIntBetween(min, max, (max+min)/2);
			++numbers[tmp];
		}
		for (i = min; i < numbers.length; ++i){
			System.out.print(String.format("%04d", i));
			System.out.print(":");
			for (j = 0; j < numbers[i]; ++j){
				System.out.print("*");
			}
			System.out.println();
		}
	}

}
