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
	public int strategyLength;
	public boolean hillClimbSteepest;
	
	/**
	 * Declares a strategy generation with a given landscape, number of strategies,
	 * and strategy length.  The LearningStrategies are then randomly generated.
	 * 
	 * @param landscape FitnessLandscape
	 * @param numStrategies Number of strategies
	 * @param strategyLength Length of the strategies
	 */
	public StrategyGeneration(FitnessLandscape landscape, int numStrategies, int strategyLength, boolean hillClimbSteepest)
	{
		this.landscape = landscape;
		this.strategyLength = strategyLength;
		this.hillClimbSteepest = hillClimbSteepest;
		
		for(int i = 0; i < numStrategies; i++)
		{
			strategies.add(new LearningStrategy(landscape, strategyLength, hillClimbSteepest));
		}
	}
	
	/**
	 * Declares a strategy generation from an arraylist of strategies
	 * 
	 * @param strategies
	 */
	public StrategyGeneration(ArrayList<LearningStrategy> strategies, boolean hillClimbSteepest)
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
	
	public StrategyGeneration getGenerationOfTopPercent(double percent)
	{
		this.sortStrategies();
		int numInTopPercent = (int) Math.floor(strategies.size() * (percent / 100.0));
		ArrayList<LearningStrategy> topStrategies = new ArrayList<LearningStrategy>();
		for(int i = 0; i < numInTopPercent; i++)
		{
			topStrategies.add(strategies.get(i));
		}
		return new StrategyGeneration(topStrategies, hillClimbSteepest);
	}
	
	/**
	 * 
	 */
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
			strategy.setOriginalGenotype(NDArrayManager.array1dRandInt(landscape.n, 2));
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
	
	public double averageFitnessRELIABILITY(int reliabilitySampleSize)
	{
		double sumOfFitnesses = 0;
		for(LearningStrategy strategy : strategies)
		{
			LearningStrategy stratTester = strategy.getDirectChild();
			for(int i = 0; i < reliabilitySampleSize; i++)
			{
				stratTester.executeStrategy();
				sumOfFitnesses += stratTester.currentFitness;
				stratTester.resetStrategy();
			}
		}
		return sumOfFitnesses / ((double)strategies.size() * (double)reliabilitySampleSize);
	}
	
	public double averageFitnessAtStep(int step) {
		double sumOfFitnesses = 0;
		for(LearningStrategy strategy : strategies)
		{
			sumOfFitnesses += strategy.getFitnessAtStep(step);
		}
		return sumOfFitnesses / strategies.size();
	}
	
	public double getPercentStuckAtLocalOptima(int step) {
		double numStuckAtArray = 0;
		for(LearningStrategy strategy : strategies)
		{
			if(strategy.stuckAtOptimaArray[step] == true)
			{
				numStuckAtArray++;
			}
		}
		return numStuckAtArray / strategies.size();
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
	
	public boolean getHillClimbSteepest() {
		return hillClimbSteepest;
	}
	
	public void mutateGeneration(double mutationPercentage, int childrenPerGeneration)
	{
		for(int child = strategies.size()-1; child >= strategies.size() - childrenPerGeneration; child--)
		{
			LearningStrategy strategy = strategies.get(child);
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
	
	public Map<Integer, Integer> frequencyOfStrategyMap() {
		Map<Integer, Integer> freqMap = new HashMap<Integer, Integer>();
		for(LearningStrategy strategy : strategies)
		{
			Integer strat = FitnessLandscape.gen2ind(strategy.genotype);
			if(!freqMap.containsKey(strat))
			{
				freqMap.put(strat, 1);
			}
			else 
			{
				freqMap.put(strat, freqMap.get(strat) + 1);
			}
		}
		return freqMap;
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
	
	public double getAverageNumberConsecutiveRWs()
	{
		int numConsRws = 0;
		for(LearningStrategy strategy : strategies)
		{
			numConsRws += strategy.getNumConsRws();
		}
		return (double)numConsRws / (double)strategies.size();
	}
	
	public int[] numConsecutiveRWsAcrossGeneration()
	{
		int[] doubleRWoccurances = new int[strategyLength];
		for(LearningStrategy strategy : strategies)
		{
			for(Integer occurance : strategy.getDoubleStepLocations())
			{
				doubleRWoccurances[occurance] += 1;
			}
		}
		return doubleRWoccurances;
	}
	
	public static double averageFitnessOfStrategy(int[] strategy, FitnessLandscape landscape, int sampleSize, boolean hillClimbSteepest)
	{
		ArrayList<LearningStrategy> strats = new ArrayList<LearningStrategy>();
		for(int i = 0; i < sampleSize; i++)
		{
			strats.add(new LearningStrategy(landscape, NDArrayManager.copyArray1d(strategy), hillClimbSteepest));
		}
		StrategyGeneration test = new StrategyGeneration(strats, true);
		test.runAllStrategies();
		return test.averageFitness();
	}
}
