import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the LearningStrategy class
 * @author Jacob Ashworth
 *
 */
class Tests_LearningStrategy {
	
	/**
	 * Tests initialization of a strategy by passing in a strategy length
	 */
	@Test
	void testBasicRandomInitalization() {
		FitnessLandscape landscape = new FitnessLandscape(5, 0);
		int[] startingLocation = {0, 0, 0, 0, 0};
		LearningStrategy testStrategy = new LearningStrategy(landscape, 10, startingLocation);
		
		System.out.println("Randomly generated strategy: ");
		System.out.println(testStrategy.strategy);
		System.out.println("Starting location: ");
		System.out.println(NDArrayManager.array1dAsString(testStrategy.genotype));
		
		
		Assertions.assertEquals(10, testStrategy.strategy.size());
		Assertions.assertEquals(5, testStrategy.genotype.length);
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
		
		ArrayList<Step> strategy = new ArrayList<Step>();
		
		for(int i = 0; i < 15; i++)
		{
			strategy.add(new WalkStep());
		}
		int[] startingLocation = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		LearningStrategy testStrategy = new LearningStrategy(landscape, strategy, startingLocation);
		double previousFitness = testStrategy.genotypeFitness;
		
		double newFitness = testStrategy.executeStrategy();
		Assert.assertNotEquals(previousFitness, newFitness);
	}
	
	/**
	 * Tests a learning strategy that strictly climbs steeply
	 */
	@Test
	void testLook() {
		FitnessLandscape landscape = new FitnessLandscape(15, 0);
		
		ArrayList<Step> strategy = new ArrayList<Step>();
		
		for(int i = 0; i < 15; i++)
		{
			strategy.add(new LookStep());
		}
		int[] startingLocation = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		LearningStrategy testStrategy = new LearningStrategy(landscape, strategy, startingLocation);
		double previousFitness = testStrategy.genotypeFitness;
		
		double newFitness = testStrategy.executeStrategy();
		Assert.assertTrue(previousFitness == newFitness);
	}
	
	/**
	 * Tests a learning strategy that strictly climbs steeply
	 */
	@Test
	void testLookAndClimb() {
		FitnessLandscape landscape = new FitnessLandscape(15, 0);
		
		ArrayList<Step> strategy = new ArrayList<Step>();
		
		for(int i = 0; i < 15; i++)
		{
			strategy.add(new LookStep());
		}
		strategy.add(new WalkStep());
		
		int[] startingLocation = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		LearningStrategy testStrategy = new LearningStrategy(landscape, strategy, startingLocation);
		double previousFitness = testStrategy.genotypeFitness;
		
		double newFitness = testStrategy.executeStrategy();
		Assert.assertTrue(previousFitness < newFitness);
		
		System.out.println(NDArrayManager.array1dDoubleAsString(testStrategy.fitnessArray));
	}
	
	/**
	 * Tests a learning strategy that strictly climbs steeply
	 */
	@Test
	void testManyLookAndClimb() {
		FitnessLandscape landscape = new FitnessLandscape(15, 0);
		
		ArrayList<Step> strategy = new ArrayList<Step>();
		
		for(int i = 0; i < 5; i++)
		{
			strategy.add(new LookStep());
			strategy.add(new WalkStep());
		}
		
		int[] startingLocation = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		LearningStrategy testStrategy = new LearningStrategy(landscape, strategy, startingLocation);
		double previousFitness = testStrategy.genotypeFitness;
		
		double newFitness = testStrategy.executeStrategy();
		Assert.assertTrue(previousFitness < newFitness);
		
		System.out.println(NDArrayManager.array1dDoubleAsString(testStrategy.fitnessArray));
	}
	

//	
//	@Test
//	void testSorting() {
//		FitnessLandscape landscape = new FitnessLandscape(20, 3);
//		ArrayList<LearningStrategy> strategyList = new ArrayList<LearningStrategy>();
//		for(int i = 0; i < 10; i++)
//		{
//			strategyList.add(new LearningStrategy(landscape, 10, true));
//		}
//		for(LearningStrategy strat : strategyList)
//		{
//			strat.executeStrategy();
//		}
//		Collections.sort(strategyList);
//		
//		System.out.print("Sorted random fitnesses: ");
//		double prevStrat = 0;
//		for(int i = 0; i < 10; i++)
//		{
//			Assert.assertTrue(strategyList.get(i).currentFitness >= prevStrat);
//			prevStrat = strategyList.get(i).currentFitness;
//			System.out.print(prevStrat + " ");
//		}
//		System.out.println("");
//	}
}
