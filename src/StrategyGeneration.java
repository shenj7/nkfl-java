import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class StrategyGeneration {
	
	public ArrayList<LearningStrategy> strategies = new ArrayList<LearningStrategy>();
	public FitnessLandscape landscape;
	public int strategyLength;
	
	public StrategyGeneration(FitnessLandscape landscape, int numStrategies, int strategyLength)
	{
		this.landscape = landscape;
		this.strategyLength = strategyLength;
		
		for(int i = 0; i < numStrategies; i++)
		{
			strategies.add(new LearningStrategy(landscape, strategyLength));
		}
	}
	
	public StrategyGeneration(ArrayList<LearningStrategy> strategies)
	{
		this.strategies = strategies;
		if(strategies.size() == 0)
		{
			System.err.println("Cannot create an empty generation");
			return;
		}
		landscape = strategies.get(0).landscape;
		strategyLength = strategies.get(0).strategyArray.length;
	}
	
	public void resetStrategies() {
		for(LearningStrategy strategy : strategies)
		{
			strategy.resetStrategy();
		}
	}
	
	public void setOriginalGenotypes(int[] genotype) {
		for(LearningStrategy strategy : strategies)
		{
			strategy.setOriginalGenotype(genotype);
		}
	}
	
	public void randomizeOriginalGenotypes() {
		for(LearningStrategy strategy : strategies)
		{
			strategy.setOriginalGenotype(NDArrayManager.array1dRandInt(landscape.n, 1));
		}
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
			sumOfFitnesses += strategy.currentFitness;
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
		for(LearningStrategy strategy : strategies)
		{
			for(int i = 0; i < strategy.getStrategyLength(); i++)
			{
				double roll = SeededRandom.rnd.nextDouble() * 100;
				if(roll < mutationPercentage)
				{
					strategy.mutateStep(i);
				}
			}
		}
	}
	
	public void sortStrategies() {
		Collections.sort(strategies);
		Collections.reverse(strategies);//Greatest first
	}
	
	public double getPercentWithStepAtIndex(int step, int index)
	{
		int numMatches = 0;
		for(LearningStrategy strategy : strategies)
		{
			if(strategy.getStepAtIndex(index) == step)
			{
				numMatches++;
			}
		}
		return (double)numMatches / (double)strategies.size();
	}
}
