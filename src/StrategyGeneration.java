import java.util.ArrayList;

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
			strategy.setOriginalGenotype(CommonMethods.randomIntArray(landscape.n, 1));
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
}
