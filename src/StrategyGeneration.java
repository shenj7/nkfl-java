import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The StrategyGeneration class represents a group of
 * learning strategies, and is intended to be used in an evolution simulation
 * 
 * @author Jacob Ashworth
 *
 */
public class StrategyGeneration {
	
	public ArrayList<LearningStrategy> strategies = new ArrayList<LearningStrategy>();
	public FitnessLandscape landscape;
	public int[] startingLocation;
	public int strategyLength;
	
	/**
	 * Declares a strategy generation with a given landscape, number of strategies,
	 * and strategy length.  The LearningStrategies are then randomly generated.
	 * 
	 * @param landscape FitnessLandscape
	 * @param numStrategies Number of strategies
	 * @param strategyLength Length of the strategies
	 */
	public StrategyGeneration(FitnessLandscape landscape, int numStrategies, int strategyLength, int[] startingLocation)
	{
		this.landscape = landscape;
		this.strategyLength = strategyLength;
		this.startingLocation = startingLocation;
		
		for(int i = 0; i < numStrategies; i++)
		{
			strategies.add(new LearningStrategy(landscape, strategyLength, startingLocation));
		}
	}
	
	/**
	 * Declares a strategy generation from an arraylist of strategies
	 * 
	 * @param strategies
	 */
	public StrategyGeneration(ArrayList<LearningStrategy> strategies)
	{
		this.strategies = strategies;
		if(strategies.size() == 0)
		{
			System.err.println("Cannot create an empty generation");
			return;
		}
		landscape = strategies.get(0).landscape;
		strategyLength = strategies.get(0).strategy.size();
	}

	public LearningStrategy getBestStrategyOfGeneration()
	{
		return strategies.get(strategies.size() - 1);
	}
	
	public void runAllStrategies() {
		for(LearningStrategy strategy : strategies)
		{
			strategy.executeStrategy();
		}
	}
	
	public double averageFitness() {
		double sumOfFitnesses = 0;
		for(LearningStrategy strategy : strategies)
		{
			sumOfFitnesses += strategy.phenotypeFitness;
		}
		return sumOfFitnesses / strategies.size();
	}
	
	public double averageFitnessAtStep(int step) {
		double sumOfFitnesses = 0;
		for(LearningStrategy strategy : strategies)
		{
			sumOfFitnesses += strategy.getFitnessAtStep(step);
		}
		return sumOfFitnesses / strategies.size();
	}
	
	public int getNumStrategies() {
		return strategies.size();
	}
	
	public int getStrategyLength() {
		return strategyLength;
	}
	
	public LearningStrategy getDirectChild(int index)
	{
		return strategies.get(index).getDirectChild();
	}
	
	public LearningStrategy getStrategyAtIndex(int index)
	{
		return strategies.get(index);
	}
	
	public void mutateGeneration(double mutationPercentage)
	{
		for(LearningStrategy strat : strategies)
		{
			strat.mutate(mutationPercentage);
		}
	}
	
	public void sortStrategies() {
		Collections.sort(strategies);
	}
}
