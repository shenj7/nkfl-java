import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class FitnessLandscapeTests {
	/**
	 * testNKLandscape_Basic just makes sure the fitTable is the right size, and
	 * makes sure it initializes with varying values in the table
	 */
	@Test
	public void testNKLandscape_Basic() {
		FitnessLandscape landscape = new FitnessLandscape(10, 2);
		// landscape.n = 10;
		// landscape.k = 2;

		double[] table = landscape.fitTable;
		// Make sure the table is the right size
		assertEquals(Math.pow(2, landscape.n), table.length);

		// Make sure the table has multiple different values
		ArrayList<Double> seenValues = new ArrayList<Double>();
		int numDifferentValuesExpected = 10; // We should be able to get at least 10 values out of the landscape
		for (int i = 0; i < table.length; i++) {
			if (!seenValues.contains(table[i])) {
				seenValues.add(table[i]);

				if (seenValues.size() >= numDifferentValuesExpected) {
					break; // Don't need to keep looking if we've seen enough different values
				}
			}
		}

		Assertions.assertTrue(seenValues.size() >= numDifferentValuesExpected);
	}

}