import java.util.ArrayList;

/*
 * Add documentation comments just in case
 */
public class FitnessLandscape {
	public int[] visitedTable; // not sure if should keep this, or make private and add accessor methods
	// public double[][] interactions;
	public double[] fitTable;
	int n = 0; // set this manually
	int k = 0; // set this manually

	public FitnessLandscape(int n, int k) {
		this.n = n;
		this.k = k;
		setTables();
	}

	public void setTables() {
		NKLandscape(n, k);
	}

	public static int[] ind2gen(int index, int n) {
		int[] genotype = new int[n]; // not correct. need to find how to use np
		if (index >= Math.pow(2, n)) {
			System.out.println("ind2gen error");
			return genotype;
		}
		while (n > 0) {
			n = n - 1;
			if (index % 2 == 0) {
				genotype[n] = 0;
			} else {
				genotype[n] = 1;
			}
			index = index / 2; // this is floor division right?
		}
		return genotype;
	}

	public static int gen2ind(int[] genotype) {
		int i = 0;
		int index = 0;
		int amount = genotype.length;
		while (i < amount) {
			index += genotype[i] * Math.pow(2, (amount - i - 1));
			i++;
		}
		return (int) (index);
	}

	public void NKLandscape(int n, int k) {
		double maxFit = 0;
		double minFit = 1000000;
		double[][] interactions = new double[n][(int) Math.pow(2, (k + 1))];
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < (int) Math.pow(2, (k + 1)); y++) {
				interactions[x][y] = SeededRandom.rnd.nextDouble();
			}
		}
		fitTable = new double[(int) Math.pow(2, n)];
		int[] visitedTable = new int[(int) Math.pow(2, n)];
		for (int x = 0; x < ((int) Math.pow(2, n)); x++) {
			double fit = 0;
			int[] genotype = ind2gen(x, n);
			for (int y = 0; y < n; y++) {
				ArrayList<Integer> subgen = new ArrayList<Integer>();
				for (int z = 0; z < k + 1; z++) {
					int zInd = (y + z) % n;
					subgen.add(genotype[zInd]);
				}
				int[] subgenArray = new int[subgen.size()];
				for (int i = 0; i < subgen.size(); i++) {
					subgenArray[i] = subgen.get(i);
				}
				int ind = gen2ind(subgenArray);
				fit = fit + interactions[y][ind];
			}
			fitTable[x] = fit;
			if (fit > maxFit) {
				maxFit = fit;
				int[] best = genotype;
			}
			if (fit < minFit) {
				minFit = fit;
			}

		}
		for (int x = 0; x < fitTable.length; x++) {
			fitTable[x] = (fitTable[x] - minFit) / (maxFit - minFit);
			fitTable[x] = fitTable[x] * 8;
		}
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

	public double fitness(int[] genotype) {
		int index = this.gen2ind(genotype);
		return fitTable[index];
	}

	public void visited(int[] genotype) {
		int index = this.gen2ind(genotype);
		visitedTable[index] = 1;
	}

	public void init_visited() {
		for (int i = 0; i < visitedTable.length; i++) {
			visitedTable[i] = 0;
		}
	}
}
