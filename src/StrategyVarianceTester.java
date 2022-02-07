import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class StrategyVarianceTester {
	
	public static void main(String[] args)
	{
		int n=15,k=6;
		int numToTest = 100,strategyRuns=1000;
		
		PrintWriter csvWriter;
		File csvFile = new File("StrategyVariance");
		try {
			csvWriter = new PrintWriter(csvFile + ".csv");
		} catch (FileNotFoundException e) {
			System.err.println("could not create csv writer");
			e.printStackTrace();
			return;
		}
		
		FitnessLandscape testLandscape = new FitnessLandscape(n, k);
		int[] testLocation = NDArrayManager.array1dRandInt(n, 2);
		LearningStrategy testStrat = new LearningStrategy(testLandscape, 50, testLocation);
		
		ArrayList<LearningStrategy> testStrats = new ArrayList<LearningStrategy>();
		for(int i = 0; i < numToTest; i++)
		{
			testStrats.add(testStrat.getDirectChild());
		}
		
		StrategyGeneration testGen = new StrategyGeneration(testStrats);
		testGen.runAllStrategies(strategyRuns);
		testGen.sortStrategies();
		
		for(LearningStrategy strat : testGen.strategies)
		{
			writeStratToCSV(strat, csvWriter);
		}
		
		System.out.println("Finished Strategy Variance Test");
		//cleanup
		csvWriter.flush();
        csvWriter.close();
	}
	
	static final String SimulationHeader = "SIMULATION";
	static final String GenerationHeader = "GENERATION";
	static final String StrategyRowHeader = "STRATEGY_ROW";
	static final String FitnessRowHeader = "FITNESS_ROW";
	static final String ComparisonStrategyHeader = "COMPARISON_STRATEGIES";
	private static void writeStratToCSV(LearningStrategy strat, PrintWriter csvWriter)
	{
		csvWriter.print(StrategyRowHeader);
		for(String step : strat.getStrategyStringArray())
		{
			csvWriter.print("," + step);
		}
		csvWriter.print("\n");
		
		//Write fitnesses to CSV
		csvWriter.print(FitnessRowHeader);
		for(double d: strat.getFitnessArray())
		{
			csvWriter.print("," + d);
		}
		csvWriter.print("\n");
	}
}
