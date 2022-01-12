/*
 * Abstract class of step to make it so we can have various kinds of steps in a learning strategy.
 * 
 * @author Jackson Shen
 */
public abstract class Step {
	public int[] genotype;
	public FitnessLandscape landscape;
	public abstract int[] execute(); // return the location after, should be the same after a ig step
	public Step(int[] genotype, FitnessLandscape landscape) {
		this.landscape = landscape;
		this.genotype = genotype;
	}
}