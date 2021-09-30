import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import junit.framework.Assert;

class Tests_StrategyGeneration {

//	@Test
	void testBasicGenerationDeclaration() {
		FitnessLandscape landscape = new FitnessLandscape(15, 3);
		StrategyGeneration gen = new StrategyGeneration(landscape, 1000, 10);
		Assertions.assertEquals(1000, gen.strategies.size());
		Assertions.assertEquals(gen.landscape, landscape);
		Assertions.assertEquals(10, gen.strategies.get(0).strategyArray.length);
	}
	
//	@Test
	void testDeterminedGenerationDeclaration() {
		FitnessLandscape landscape = new FitnessLandscape(15, 3);
		ArrayList<LearningStrategy> strats = new ArrayList<LearningStrategy>();
		for(int i = 0; i < 1000; i++)
		{
			strats.add(new LearningStrategy(landscape, 10));
		}
		StrategyGeneration gen = new StrategyGeneration(strats);
		Assertions.assertEquals(1000, gen.strategies.size());
		Assertions.assertEquals(gen.landscape, landscape);
		Assertions.assertEquals(10, gen.strategies.get(0).strategyArray.length);
	}
	
	@Test
	void runGenerationAnalysis() {
		FitnessLandscape landscape = new FitnessLandscape(20, 1);
		int steps = 15;
		int strategiesPerGeneration = 100000;
		
		//Choose the strategies to make
		ArrayList<int[]> strategies = new ArrayList<int[]>();
		//Set up the strategies list to do exclusive seperated strategies (like [0, 0, 1, 1] or [1, 0, 0, 0]
		int[] constructorArray = new int[steps];
		for(int i = 0; i < steps; i++)
		{
			constructorArray[i] = 0;
		}
		strategies.add(CommonMethods.copyArray(constructorArray));
		for(int i = 0; i < steps; i++)
		{
			constructorArray[i] = 1;
			strategies.add(CommonMethods.copyArray(constructorArray));
		}
		for(int i = 0; i < steps; i++)
		{
			constructorArray[i] = 0;
			strategies.add(CommonMethods.copyArray(constructorArray));
		}
		
		//Run all the strategies
		System.out.println("Running analysis on " + strategies.size() + " strategies with sample size " + strategiesPerGeneration);
		for(int[] strategy : strategies)
		{
			ArrayList<LearningStrategy> strats = new ArrayList<LearningStrategy>();
			for(int i = 0; i < strategiesPerGeneration; i++)
			{
				strats.add(new LearningStrategy(landscape, strategy));
			}
			StrategyGeneration gen = new StrategyGeneration(strats);
			gen.runAllStrategies();
			System.out.println("Average final fitness for " + CommonMethods.arrayAsString(strategy) + " was " + gen.averageFitness());
		}
	}
}
