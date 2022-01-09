import java.util.ArrayList;

public class StrategyGenerationFactory {
//	public static void main(String args[])
//	{
//		FitnessLandscape l = new FitnessLandscape(10, 2);
//		int[] p1 = {0, 0, 0, 0, 0};
//		int[] p2 = {1, 1, 1, 1, 1};
//		LearningStrategy par1 = new LearningStrategy(l, p1);
//		LearningStrategy par2 = new LearningStrategy(l, p2);
//		LearningStrategy kid = truncateParents(par1, par2, 3);
//		System.out.println("Result of truncation: " + NDArrayManager.array1dAsString(kid.strategyArray));
//	}
	
	public static StrategyGeneration generateTruncation(StrategyGeneration parents, int numChildren, int[] startingLocation) {
		parents.sortStrategies();
		
		ArrayList<LearningStrategy> childrenStrategies = new ArrayList<LearningStrategy>();
		
		int numSurvivors = parents.getNumStrategies() - numChildren;
		
		for(int i = 0; i < numSurvivors; i++)//Add the survivors to the childrenStrategies
		{
			childrenStrategies.add(parents.getDirectChild(i));
		}
		
		for(int i = numSurvivors; i < parents.getNumStrategies(); i++)
		{
			//Truncate
			//Random truncation index
			int truncationIndex = SeededRandom.rnd.nextInt(parents.strategyLength);
			LearningStrategy parent1 = parents.getStrategyAtIndex(SeededRandom.rnd.nextInt(numSurvivors));
			LearningStrategy parent2 = parents.getStrategyAtIndex(SeededRandom.rnd.nextInt(numSurvivors));//It's possible to get the same parent, but that's okay. Any decent sample size should mitigate that problem.
			LearningStrategy newChild = StrategyGenerationFactory.truncateParents(parent1, parent2, truncationIndex);
			childrenStrategies.add(newChild);
		}
		StrategyGeneration childGeneration = new StrategyGeneration(childrenStrategies, parents.getHillClimbSteepest());
		childGeneration.setOriginalGenotypes(startingLocation);
		return childGeneration;
	}
	
	public static StrategyGeneration generateRankedExponential(StrategyGeneration parents, int numChildren, int[] startingLocation) {
		ArrayList<LearningStrategy> childrenStrategies = new ArrayList<LearningStrategy>();
		parents.sortStrategies();
		
		int totalRanks = 0;
		int[] rankValues = new int[parents.getNumStrategies()];
		for(int i = 2; i < parents.getNumStrategies() + 2; i++)
		{
			rankValues[i - 2] = parents.getNumStrategies()/i;
			totalRanks += rankValues[i - 2];
		}
		
		ArrayList<LearningStrategy> rankAL = new ArrayList<LearningStrategy>();
		
		for(int i = 0; i < rankValues.length; i++)
		{
			for(int j = 0; j < rankValues[i]; j++)
			{
				rankAL.add(parents.getStrategyAtIndex(i));
			}
		}
		
		int numSurvivors = parents.getNumStrategies() - numChildren;
		
		for(int i = 0; i < numSurvivors; i++)//Add the survivors to the childrenStrategies
		{
			childrenStrategies.add(parents.getDirectChild(i));
		}
		
		for(int i = numSurvivors; i < parents.getNumStrategies(); i++)
		{
			int index = (int)(SeededRandom.rnd.nextDouble() * totalRanks);
			childrenStrategies.add(rankAL.get(index).getDirectChild());
		}
		
		if(parents.getNumStrategies() != childrenStrategies.size())
		{
			System.err.println("Generation sizes differ... Parent: " + parents.getNumStrategies() + " Child: " + childrenStrategies.size());
		}
		
		StrategyGeneration childGeneration = new StrategyGeneration(childrenStrategies, parents.getHillClimbSteepest());
		childGeneration.setOriginalGenotypes(startingLocation);
		return childGeneration;
	}
	
	public static StrategyGeneration generateRankedLinear(StrategyGeneration parents, int numChildren, int[] startingLocation) {
		ArrayList<LearningStrategy> childrenStrategies = new ArrayList<LearningStrategy>();
		parents.sortStrategies();
		
		int totalRanks = 0;
		int[] rankValues = new int[parents.getNumStrategies()];
		for(int i = 2; i < parents.getNumStrategies() + 2; i++)
		{
			rankValues[i - 2] = Math.max(parents.getNumStrategies() - i, 0);
			totalRanks += rankValues[i - 2];
		}
		
		ArrayList<LearningStrategy> rankAL = new ArrayList<LearningStrategy>();
		
		for(int i = 0; i < rankValues.length; i++)
		{
			for(int j = 0; j < rankValues[i]; j++)
			{
				rankAL.add(parents.getStrategyAtIndex(i));
			}
		}
		
		int numSurvivors = parents.getNumStrategies() - numChildren;
		
		for(int i = 0; i < numSurvivors; i++)//Add the survivors to the childrenStrategies
		{
			childrenStrategies.add(parents.getDirectChild(i));
		}
		
		for(int i = numSurvivors; i < parents.getNumStrategies(); i++)
		{
			int index = (int)(SeededRandom.rnd.nextDouble() * totalRanks);
			childrenStrategies.add(rankAL.get(index).getDirectChild());
		}
		
		if(parents.getNumStrategies() != childrenStrategies.size())
		{
			System.err.println("Generation sizes differ... Parent: " + parents.getNumStrategies() + " Child: " + childrenStrategies.size());
		}
		
		StrategyGeneration childGeneration = new StrategyGeneration(childrenStrategies, parents.getHillClimbSteepest());
		childGeneration.setOriginalGenotypes(startingLocation);
		return childGeneration;
	}
	
	//TruncateIndex will be where p2 starts (so [0,0,0] and [1,1,1] truncated at 1 = [0,1,1])
	private static LearningStrategy truncateParents(LearningStrategy p1, LearningStrategy p2, int truncateIndex)
	{
		int stratLength = p1.getStrategyLength();
		if(stratLength != p2.getStrategyLength())
		{
			System.err.println("Cannot truncate different length parents");
			return null;
		}
		
		int[] childStrategyArray = new int[stratLength];
		
		for(int i = 0; i < truncateIndex; i++)
		{
			childStrategyArray[i] = p1.getStepAtIndex(i);
		}
		for(int i = truncateIndex; i < stratLength; i++)
		{
			childStrategyArray[i] = p2.getStepAtIndex(i);
		}
		
		return new LearningStrategy(p1.landscape, childStrategyArray, p1.getHillClimbSteepest());
	}
}
