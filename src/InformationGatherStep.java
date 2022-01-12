/*
 * Information gathering step
 * 
 * @author Jackson Shen
 */
public class InformationGatherStep extends Step {
	public int numChecks; // change this to change the number of info-gathering steps
	
	public InformationGatherStep(int[] genotype, int numChecks) {
		super(genotype);
		this.numChecks = numChecks;
	}
	
	public InformationGatherStep(int[] genotype) {
		super(genotype);
	}

	@Override
	public int[] execute() {
		
		return this.genotype;
	}

}
