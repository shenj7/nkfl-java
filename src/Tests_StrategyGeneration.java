import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import junit.framework.Assert;

class Tests_StrategyGeneration {

	@Test
	void testBasicGenerationDeclaration() {
		FitnessLandscape landscape = new FitnessLandscape(15, 3);
		int[] startingLocation = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		StrategyGeneration gen = new StrategyGeneration(landscape, 1000, 10, startingLocation);
		Assertions.assertEquals(1000, gen.strategies.size());
		Assertions.assertEquals(gen.landscape, landscape);
		Assertions.assertEquals(10, gen.strategies.get(0).strategy.size());
	}
	
	@Test
	void testSorting() {
		FitnessLandscape landscape = new FitnessLandscape(20, 3);
		int[] startingLocation = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		ArrayList<LearningStrategy> strategyList = new ArrayList<LearningStrategy>();
		for(int i = 0; i < 10; i++)
		{
			strategyList.add(new LearningStrategy(landscape, 10, startingLocation));
		}
		
		StrategyGeneration gen = new StrategyGeneration(strategyList);
		
		gen.runAllStrategies();
		gen.sortStrategies();
		
		System.out.print("Sorted random fitnesses: ");
		double prevStrat = 0;
		for(int i = 0; i < 10; i++)
		{
			Assert.assertTrue(gen.getStrategyAtIndex(i).phenotypeFitness >= prevStrat);
			prevStrat = strategyList.get(i).phenotypeFitness;
			System.out.print(prevStrat + " ");
		}
		System.out.println("");
	}
}
