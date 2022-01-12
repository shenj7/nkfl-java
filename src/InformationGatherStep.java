/*
 * Information gathering step
 * 
 * @author Jackson Shen
 */
public class InformationGatherStep extends Step {
	public int numChecks; // change this to change the number of info-gathering steps
	public int bestChecked; // the bit to be flipped to get the best fitness after n checks
	public double bestFit = -1;
	
	public InformationGatherStep(int[] genotype, FitnessLandscape landscape, int numChecks) {
		super(genotype, landscape);
		this.numChecks = numChecks;
	}
	
	public InformationGatherStep(int[] genotype, FitnessLandscape landscape) {
		super(genotype, landscape);
	}

	@Override
	public int[] execute() {
		for (int x = 0; x < numChecks; x++) {
			int[] copygeno = NDArrayManager.copyArray1d(genotype);
			int changeIndex = SeededRandom.rnd.nextInt(copygeno.length);
			copygeno[changeIndex] = (copygeno[changeIndex] + 1) % 2;
			if(landscape.fitness(copygeno) > bestFit) {
				bestFit = landscape.fitness(copygeno);
				bestChecked = changeIndex;
			}

		}
		return this.genotype;
	}
	
	public int getBestChecked() {
		return this.bestChecked;
	}

}
