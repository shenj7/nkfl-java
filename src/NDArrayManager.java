import java.util.Random;
import java.util.Arrays;

/**
 * Class: NDArrayManager
 * 
 * @author Edward Kim and Lyra Lee <br>
 *         Purpose: Hold useful NDArray operations to replace NumPy methods <br>
 *         Note: Replacement by open source libraries such as DJL might be
 *         preferable for extended functionality <br>
 *         For example:
 * 
 *         <pre>
 *         NDArrayManager arrMgr = new NDArrayManager();
 *         </pre>
 */

public class NDArrayManager {

	/**
	 * Creates and returns a 2D array of the given dimensions, filled with random
	 * ints under the given bound.
	 * 
	 * @param dim1         The first dimension of the array, i.e. row
	 * @param dim2         The second dimension of the arry, i.e. column
	 * @param randIntBount The bound (exclusive) for the random ints to be generated
	 * @return int[][] The constructed 2D array
	 */
	public int[][] array2dRandInt(int dim1, int dim2, int randIntBound) {

		Random randomizer = new Random();

		int[][] randIntArray2d = new int[dim1][dim2];
		for (int indx1 = 0; indx1 < dim1; indx1++) {
			for (int indx2 = 0; indx2 < dim2; indx2++) {
				randIntArray2d[indx1][indx2] = randomizer.nextInt(randIntBound);
			}
		}

		return randIntArray2d;
	}

	/**
	 * Makes a new copy of the given 2D array and returns.
	 * 
	 * @param array The 2D array to copy
	 * @return int[][] The new copy of the 2D array
	 */
	public int[][] copyArray2d(int[][] array) {

		int[][] newArray = new int[array.length][array[0].length];
		System.arraycopy(array, 0, newArray, 0, array.length);

		return newArray;
	}

	/**
	 * Creates and returns a 1D array of the given dimension, filled with random
	 * ints under the given bound.
	 * 
	 * @param dim1         The length of the array
	 * @param randIntBount The bound (exclusive) for the random ints to be generated
	 * @return int[] The constructed 1D array
	 */
	public int[] array1dRandInt(int dim1, int randIntBound) {

		Random randomizer = new Random();

		int[] randIntArray1d = new int[dim1];
		for (int indx1 = 0; indx1 < dim1; indx1++) {
			randIntArray1d[indx1] = randomizer.nextInt(randIntBound);
		}

		return randIntArray1d;
	}

	/**
	 * Makes a new copy of the given 1D array and returns.
	 * 
	 * @param array The 1D array to copy
	 * @return int[] The new copy of the 1D array
	 */
	public int[] copyArray1d(int[] array) {

		int[] newArray = new int[array.length];
		System.arraycopy(array, 0, newArray, 0, array.length);

		return newArray;
	}

	/**
	 * Creates and returns a 1D array of the given dimension, filled with the given
	 * double.
	 * 
	 * @param dim1 The length of the array
	 * @return Double[] The constructed 1D array
	 */
	public Double[] array1dNums(int dim1, Double number) {

		Double[] numArray = new Double[dim1];
		Arrays.fill(numArray, number);

		return numArray;
	}

	/**
	 * Computes and returns the mean of a 1D array.
	 * 
	 * @param array The array to calculate the mean of
	 * @return Double The calculated mean
	 */
	public Double array1dMean(Double[] array) {

		Double sum = 0.0;
		int count = array.length;

		for (int i = 0; i < count; i++) {
			sum += array[i];
		}

		return sum / count;
	}

	/**
	 * Creates and returns a 1D array containing the absolute difference of each of
	 * the elements in the two given 1D arrays.
	 * 
	 * @param arr1 The first array
	 * @param arr2 The second array
	 * @return Double[] The constructed 1D array
	 */
	public Double[] array1dAbsDiff(int[] arr1, int[] arr2) {

		Double[] absDiffArray = new Double[arr1.length];

		for (int i = 0; i < arr1.length; i++) {
			absDiffArray[i] = (double) Math.abs(arr1[i] - arr2[i]);
		}

		return absDiffArray;
	}

	/**
	 * Returns the maximum value in the given 1D array.
	 * 
	 * @param array The array to search in
	 * @return Double The maximum value
	 */
	public Double array1dMax(Double[] array) {

		Double max = array[0];

		for (int i = 0; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
			}
		}

		return max;
	}

	/**
	 * Returns the index of the maximum value in the given 1D array.
	 * 
	 * @param array The array to search in
	 * @return int The index of the maximum value
	 */
	public int array1dMaxIndex(Double[] array) {

		int maxIndex = 0;
		Double max = array[maxIndex];

		for (int i = 0; i < array.length; i++) {
			if (array[i] > max) {
				maxIndex = i;
				max = array[maxIndex];
			}
		}

		return maxIndex;
	}

	/**
	 * Converts a 1D int array into a 1D double array.
	 * 
	 * @param intArr The int array to convert
	 * @return Double[] The newly created double array
	 */
	public Double[] array1dIntToDouble(int[] intArr) {

		Double[] doubleArr = new Double[intArr.length];

		for (int i = 0; i < intArr.length; i++) {
			doubleArr[i] = (double) intArr[i];
		}

		return doubleArr;
	}

} // end class
