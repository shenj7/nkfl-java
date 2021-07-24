/*
 * Add documentation comments just in case
 */
public class FitnessLandscape {
	public int[] visitedTable; //not sure if should keep this, or make private and add accessor methods
	public double[][] interactions = this.interactions();
	public double[] fitTable = this.fitTable();
	int n = 0; // set this manually
	int k = 0; // set this manually
	
	public FitnessLandscape(int n, int k) {
		this.n = n;
		this.k = k;
	}

	public int[] ind2gen(int index, int n) {
		int[] genotype = new int[n]; // not correct. need to find how to use np
		if (index >= Math.pow(2, n)) {
			System.out.println("ind2gen error");
			return genotype;
		}
		while (n > 0) {
			n = n - 1;
			if (index % 2 == 0){
				genotype[n] = 0;
			}else{
				genotype[n] = 1;
			}
			index = index / 2; // this is floor division right?
		}
		return genotype;
	}
	public int gen2ind(int[] genotype) {
		int i = 0;
		int index = 0;
		int amount = genotype.length;
		while(i < amount){
			index += genotype[i]*Math.pow(2, (amount-i-1));
			i++;
		}
		return (int)(index);
	}

	/*
	 * generating landscape like discussion
	 */
	public double[][] interactions() { // Interaction matrix
		double[][] interactionList = new double[n][(int)Math.pow(2, k+1)];
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < Math.pow(2, k+1); y++) {
				interactionList[x][y] = SeededRandom.rnd.nextDouble();
			}
		}
		return interactionList;
	}
	
	public double[] fitTable() {
		double[] fitTable = new double[(int) Math.pow(2, n)];
		for (int x = 0; x < Math.pow(2, n); x++) {
			double currentFit = 0.0;
			int[] genotype = ind2gen(x, n);
			for (int y = 0; y < n; y++) {
				int[] subgen = new int[k+1]; // maybe change to double
				for (int z = 0; z < k+1; z++){ // maybe change to double
					subgen[z] = genotype[(y+z)%n];
				}
				int ind = gen2ind(subgen); // maybe change to double
				currentFit = currentFit+interactions[y][ind];
			}
			fitTable[x] = currentFit;
		}
		return fitTable;
	}

	public double maxFit(double[] fitTable) {
		double maxFit = 0.0;
		for (int x = 0; x < fitTable.length; x++) {
			if (fitTable[x] > maxFit) {
				maxFit = fitTable[x];
			}
		}
		return maxFit;
	}

	public double minFit(double[] fitTable) {
		double minFit = 0.0;
		for (int x = 0; x < fitTable.length; x++) {
			if (fitTable[x] > minFit) {
				minFit = fitTable[x];
			}
		}
		return minFit;
	}

	public double fitness(int[] genotype)
	{
		int index = this.gen2ind(genotype);
		return fitTable[index];
	}
	
	public void visited(int[] genotype)
	{
		int index = this.gen2ind(genotype);
		visitedTable[index] = 1;
	}
	
	public void init_visited()
	{
		for(int i = 0; i < visitedTable.length; i++)
		{
			visitedTable[i] = 0;
		}
	}
}