import static org.junit.jupiter.api.Assertions.*;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the LearningStrategy class
 * @author Jacob Ashworth
 *
 */
class LearningStrategyTests {

	/**
	 * Tests initialization of a strategy by passing in an array
	 */
	@Test
	void testBasicInitalization() {
		FitnessLandscape landscape = new FitnessLandscape(15, 0);
		int[] strategy = {1, 0, 1, 0, 1, 1, 1, 0, 0, 0};
		LearningStrategy testStrategy = new LearningStrategy(landscape, strategy);
		Assertions.assertEquals(strategy, testStrategy.strategyArray);
		Assertions.assertEquals(15, testStrategy.genotype.length);
	}
	
	/**
	 * Tests initialization of a strategy by passing in a strategy length
	 */
	@Test
	void testBasicRandomInitalization() {
		FitnessLandscape landscape = new FitnessLandscape(15, 0);
		LearningStrategy testStrategy = new LearningStrategy(landscape, 10);
		Assertions.assertEquals(10, testStrategy.strategyArray.length);
		Assertions.assertEquals(15, testStrategy.genotype.length);
	}

	/**
	 * Tests a learning strategy that randomly walks across a landscape
	 * 
	 * Edge Case Failure: if two neighboring genotypes have identical fitnesses (extremely unlikely),
	 * this test could throw a false negative
	 */
	@Test
	void testRandomWalk() {
		FitnessLandscape landscape = new FitnessLandscape(15, 0);
		int[] strategy = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //Ten random walks
		LearningStrategy testStrategy = new LearningStrategy(landscape, strategy);
		double previousFitness = testStrategy.currentFitness;
		for(int i = 0; i < 10; i++)
		{
			double newFitness = testStrategy.executeStep();
			Assert.assertNotEquals(previousFitness, newFitness);
			previousFitness = newFitness;
		}
	}
	
	/**
	 * Tests a learning strategy that strictly climbs steeply
	 */
	@Test
	void testSteepestClimb() {
		FitnessLandscape landscape = new FitnessLandscape(15, 0);
		int[] strategy = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}; //Ten steepest climbs
		LearningStrategy testStrategy = new LearningStrategy(landscape, strategy);
		double previousFitness = testStrategy.currentFitness;
		for(int i = 0; i < 10; i++)
		{
			double newFitness = testStrategy.executeStep();
			Assert.assertTrue(previousFitness <= newFitness); //We can only go up or stay the same
			previousFitness = newFitness;
		}
	}
	
	/**
	 * Tests a strategy's ability to execute the entire strategy array at once
	 */
	@Test
	void testExecuteStrategy() {
		FitnessLandscape landscape = new FitnessLandscape(15, 0);
		int[] strategy = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}; //Ten steepest climbs
		LearningStrategy testStrategy = new LearningStrategy(landscape, strategy);
		double previousFitness = testStrategy.currentFitness;
		double newFitness = testStrategy.executeStrategy();
		Assert.assertTrue(previousFitness <= newFitness); //We can only go up or stay the same
		Assert.assertEquals(9, testStrategy.currentStep);
	}
}
