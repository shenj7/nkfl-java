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
		StrategyGeneration gen = new StrategyGeneration(landscape, 1000, 10, true);
		Assertions.assertEquals(1000, gen.strategies.size());
		Assertions.assertEquals(gen.landscape, landscape);
		Assertions.assertEquals(10, gen.strategies.get(0).strategyArray.length);
	}
	
	@Test
	void testDeterminedGenerationDeclaration() {
		FitnessLandscape landscape = new FitnessLandscape(15, 3);
		ArrayList<LearningStrategy> strats = new ArrayList<LearningStrategy>();
		for(int i = 0; i < 1000; i++)
		{
			strats.add(new LearningStrategy(landscape, 10, true));
		}
		StrategyGeneration gen = new StrategyGeneration(strats, true);
		Assertions.assertEquals(1000, gen.strategies.size());
		Assertions.assertEquals(gen.landscape, landscape);
		Assertions.assertEquals(10, gen.strategies.get(0).strategyArray.length);
	}
	
	@Test
	void testSorting() {
		FitnessLandscape landscape = new FitnessLandscape(20, 3);
		ArrayList<LearningStrategy> strategyList = new ArrayList<LearningStrategy>();
		for(int i = 0; i < 10; i++)
		{
			strategyList.add(new LearningStrategy(landscape, 10, true));
		}
		
		StrategyGeneration gen = new StrategyGeneration(strategyList, true);
		
		gen.runAllStrategies();
		gen.sortStrategies();
		
		System.out.print("Sorted random fitnesses: ");
		double prevStrat = 1;
		for(int i = 0; i < 10; i++)
		{
			Assert.assertTrue(gen.getStrategyAtIndex(i).currentFitness <= prevStrat);
			prevStrat = strategyList.get(i).currentFitness;
			System.out.print(prevStrat + " ");
		}
		System.out.println("");
	}
}
