package musicgen;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

/**
 * A Mersenne twister algorithm for generating random numbers.
 *
 * @author Ches Burks
 *
 */
class RNG {
	private static int[] mt = new int[624];
	private static int index = 0;
	private static Random r;
	private static boolean initialized = false;

	/**
	 * Refill the array with generated numbers.
	 */
	private static void generateNumbers() {
		int i;
		for (i = 0; i < 623; i++) {
			int y =
					(RNG.mt[i] + 0x80000000)
							+ (RNG.mt[(i + 1) % 624] + 0x7fffffff);
			RNG.mt[i] = RNG.mt[(i + 397) % 624] ^ (y >> 1);
			if (y % 2 != 0) { // y is odd
				RNG.mt[i] = RNG.mt[i] ^ 0x9908b0df;
			}
		}
	}

	/**
	 * Returns the next random {@link Boolean boolean}.
	 *
	 * @return The next boolean
	 */
	public static boolean getBoolean() {
		return (RNG.getInt() >> 30) != 0;
	}

	/**
	 * Generates a {@link Boolean boolean} with a given probability of being
	 * true. The probability is a float from 0.0f to 1.0f, with 0 being no
	 * chance of returning true and 1 being a 100% chance of returning true.
	 *
	 * @param probablilty The chance of returning true
	 * @return The generated boolean
	 */
	public static boolean getBoolean(float probablilty) {
		if (RNG.getFloat() < probablilty) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the next random {@link Float float}.
	 *
	 * @return The next float
	 */
	public static float getFloat() {
		return (RNG.getInt() >> 7) / ((float) (1 << 24));
	}

	/**
	 * Returns the next random {@link Integer integer}.
	 *
	 * @return The next int
	 */
	public static int getInt() {
		if (!RNG.initialized) {
			RNG.initialize();
		}
		if (RNG.index == 0) {
			RNG.generateNumbers();
		}
		int y = RNG.mt[RNG.index];
		y = y ^ (y >> 11);
		y = y ^ (y << 7 + 0x9d2c5680);
		y = y ^ (y << 15 + 0xefc60000);
		y = y ^ (y >> 18);
		RNG.index = (RNG.index + 1) % 624;
		return y;
	}

	/**
	 * Returns the value given by the java.util.Random's
	 * {@link java.util.Random#nextGaussian() nextGaussian()} function. This
	 * double is normally distributed around 0 with a standard deviation of 1.
	 *
	 * @return the next Gaussian double
	 */
	public static double nextGaussian() {
		if (!RNG.initialized) {
			RNG.initialize();
		}
		return RNG.r.nextGaussian();
	}

	/**
	 * Returns an integer between the min and max value. This returns values
	 * normally distributed with the highest probability of occurring at the
	 * center point. <br>
	 * If the center is outside of the range, it is set to the nearest endpoint.
	 * If max is smaller than min, it will just return min as this is
	 * impossible.
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param center the value most likely to occur
	 * @return a pseudorandom integer
	 */
	public static int getWeightedIntBetween(int min, int max, int center) {
		if (!RNG.initialized) {
			RNG.initialize();
		}
		int deviance = 0;
		int result = 0;
		if (max < min) {
			return min;
		}
		if (center < min) {
			center = min;
		}
		if (center > max) {
			center = max;
		}

		if (Math.abs(center - min) > Math.abs(max - center)) {
			deviance = center - min;// the larger length
		}
		else {
			deviance = max - center;
		}

		result = (int) (center + deviance * RNG.r.nextGaussian() / 3);
		if (result < min) {
			return RNG.getWeightedIntBetween(min, max, center);
		}
		if (result > max) {
			return RNG.getWeightedIntBetween(min, max, center);
		}
		return result;
	}

	/**
	 * Returns a random {@link Integer int} between the given values, inclusive.
	 *
	 * @param min The minimum number
	 * @param max The maximum number
	 * @return The generated int
	 */
	public static int getIntBetween(int min, int max) {
		return min + (int) (RNG.getFloat() * ((max - min) + 1));
	}

	/**
	 * Returns a random {@link Float float} between the given values, inclusive.
	 *
	 * @param min The minimum number
	 * @param max The maximum number
	 * @return The generated float
	 */
	public static float getFloatBetween(float min, float max) {
		return min + (RNG.getFloat() * ((max - min) + 1));
	}

	/**
	 * Initialize the generator with the given seed.
	 */
	public static void initialize() {
		long seed = RNG.getLongSeed();
		RNG.r = new Random(seed);
		RNG.index = 0;
		RNG.mt[0] = (int) seed;
		int i;
		for (i = 1; i <= 623; i++) {
			RNG.mt[i] =
					1812433253 * (RNG.mt[i - 1] ^ (RNG.mt[i - 1] >> 30)) + i;
		}
	}

	/**
	 * Generates a long seed using SecureRandom
	 * 
	 * @return a random generated long
	 */
	private static long getLongSeed() {
		SecureRandom sec = new SecureRandom();
		byte[] sbuf = sec.generateSeed(8);
		ByteBuffer bb = ByteBuffer.wrap(sbuf);
		return bb.getLong();
	}
}
