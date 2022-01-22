import java.util.ArrayList;

/*
 * Walking step
 * 
 * @author Jackson Shen, Jacob Ashworth
 */
public class WalkStep extends Step {

	public WalkStep() {
	}

	@Override
	public int[] execute(FitnessLandscape landscape, int[] phenotype, ArrayList<Integer> lookedLocations) {
		if(lookedLocations.size() == 0)
		{
			//RW
			int location = SeededRandom.rnd.nextInt(phenotype.length);
			int[] newPhenotype = NDArrayManager.copyArray1d(phenotype);
			newPhenotype[location] = (newPhenotype[location] + 1) % 2;
			return newPhenotype;
		}
		else
		{
			//HC
		
			int[] bestPhenotype = phenotype;
			double bestPhenotypeFitness = landscape.fitness(phenotype);
			
			for(Integer location : lookedLocations)
			{
				int[] newPhenotype = NDArrayManager.copyArray1d(phenotype);
				newPhenotype[location] = (newPhenotype[location] + 1) % 2;
				double newPhenotypeFitness = landscape.fitness(newPhenotype);
				
				if(newPhenotypeFitness > bestPhenotypeFitness)
				{
					bestPhenotype = newPhenotype;
					bestPhenotypeFitness = newPhenotypeFitness;
				}
			}
			
			lookedLocations.clear(); //Remove all our looked locations for the future
			return bestPhenotype;
		}
	}
	
	public String getStepName() {
		return "Walk";
	}

}
