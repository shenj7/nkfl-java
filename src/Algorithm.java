import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class: Algorithm
 * 
 * @author Edward Kim and Lyra Lee <br>
 *         Purpose: Contain all functionality for the evolution and development
 *         of abstract agents in NKFL <br>
 *         Note: Highly likely to be separated into multiple classes in the
 *         future <br>
 *         For example:
 * 
 *         <pre>
 *         Algorithm alg1 = new Algorithm();
 *         </pre>
 */

public class Algorithm {

   // Main
   public static void main(String[] args) {

      System.out.println();
      System.out.println("running basic test script: ");
      System.out.println();

      DummyLandscape land1 = new DummyLandscape();
      ArrayList<String> cond1 = new ArrayList<String>();

      // population of 100, genome length 100, 100 generations, crossoverProb 0,
      // mutateProb 0, 100 learning trials
      Algorithm alg1 = new Algorithm(100, 100, land1, 100, 0.0, 0.0, 100, cond1, false, 0.0, 0);

      // testing all method calls, order not considered
      alg1.activate();
      alg1.reset();
      // alg1.plotFitness();
      alg1.diversityStats();
      alg1.fitnessStats();
      alg1.stepRandomWalk(alg1.arrMgr.array1dRandInt(alg1.genomeLength, 2), 1);
      alg1.stepHillClimb(alg1.arrMgr.array1dRandInt(alg1.genomeLength, 2), 1, 1);

   } // end main

   // Fields
   public Integer LOG_LEVEL; // for dev purposes
   public Boolean calculateDiversity; // boolean control to calculate diversity (high computational cost)
   public Double overlapTolerance; // a maximum tolerance for hamming distance, under which two phenotypes are
                                   // considered very similar, "non-unique"

   public Integer populationSize; // number of individuals in a generation/population
   public Integer genomeLength; // number of genes in an individual
   public Integer generations; // number of generations
   public Integer currGeneration; // the current generation
   public Integer learningTrials; // learning occurs in a lifetime, i.e. in between two consecutive generations
   public Double crossoverProb; // probability of exchanging genes
   public Double mutateProb; // probability of mutation

   public DummyLandscape landscape;
   public ArrayList<String> condition; // set of controls and hyperparameters, condition[0] specifies evolutionary
                                       // search
   public Integer[][] initialGenotypes;
   public Integer[][] genotypes; // genotype[i] is an individual, genotype[i][i] is a gene
   public Double[] fitness; // fitness of all individuals in population
   public Double[] averageHistory; // average fitness of all individuals in each generation
   public Double[] bestHistory; // fitness of best individual in each generation
   public Double[] avgHammDistHistory; // measure of diversity in population
   public Double[] uniquenessHistory; // measure of uniqueness in population
   public Integer numUnchangedPhenotype; // phenotype = individual
   public Integer numChangedPhenotype;

   public Integer fitCalledCount;
   public Integer randomWalkCount; // exploration
   public Integer hillClimbCount; // exploitation
   public Integer steepestHillClimbCount;
   public Integer hillClimbSuccessCount;
   public Integer steepestHillClimbSuccessCount;
   public ArrayList<Integer> stuckLocalOptima;

   public NDArrayManager arrMgr = new NDArrayManager(); // helper class for NDArray operations, substituting NumPy
   public Random randomizer = new Random();

   // Constructor
   public Algorithm(Integer populationSize, Integer genomeLength, DummyLandscape landscape, Integer generations,
         Double crossoverProb, Double mutateProb, Integer learningTrials, ArrayList<String> condition,
         Boolean calculateDiversity, Double overlapTolerance, Integer LOG_LEVEL) {

      // initialize fields
      this.LOG_LEVEL = LOG_LEVEL;
      this.calculateDiversity = calculateDiversity;
      this.overlapTolerance = overlapTolerance;
      this.populationSize = populationSize;
      this.genomeLength = genomeLength;
      this.generations = generations;
      this.currGeneration = 0;
      this.learningTrials = learningTrials;
      this.crossoverProb = crossoverProb;
      this.mutateProb = mutateProb;
      this.landscape = landscape;
      this.condition = condition;

      this.initialGenotypes = arrMgr.array2dRandInt(this.populationSize, this.genomeLength, 2); // bound at 2
                                                                                                // (exclusive)
      this.genotypes = arrMgr.copyArray2d(this.initialGenotypes);
      this.fitness = arrMgr.array1dNums(this.populationSize, 0.0);
      this.averageHistory = arrMgr.array1dNums(this.generations, 0.0);
      this.bestHistory = arrMgr.array1dNums(this.generations, 0.0);
      this.avgHammDistHistory = arrMgr.array1dNums(this.generations, 0.0);
      this.uniquenessHistory = arrMgr.array1dNums(this.generations, 0.0);

      this.stuckLocalOptima = new ArrayList<Integer>();

      // for dev
      if (this.LOG_LEVEL > 0) {
         this.numUnchangedPhenotype = 0;
         this.numChangedPhenotype = 0;
         this.fitCalledCount = 0;
         this.randomWalkCount = 0;
         this.hillClimbCount = 0;
         this.steepestHillClimbCount = 0;
         this.hillClimbSuccessCount = 0;
         this.steepestHillClimbSuccessCount = 0;
      }

      System.out.println("algorithm constructed");

   } // end constructor

   // simple test method
   public void activate() {
      System.out.println();
      System.out.println("algorithm active");
      System.out.println();
   }

   // reset()
   public void reset() {

      this.fitCalledCount = 0;
      this.genotypes = arrMgr.copyArray2d(this.initialGenotypes);
      this.fitness = arrMgr.array1dNums(this.populationSize, 0.0);
      this.averageHistory = arrMgr.array1dNums(this.generations, 0.0);
      this.bestHistory = arrMgr.array1dNums(this.generations, 0.0);

      if (this.calculateDiversity) {
         this.avgHammDistHistory = arrMgr.array1dNums(this.generations, 0.0);
         this.uniquenessHistory = arrMgr.array1dNums(this.generations, 0.0);
      }

      this.stuckLocalOptima = new ArrayList<Integer>();

      // for dev
      if (this.LOG_LEVEL > 0) {
         this.numUnchangedPhenotype = 0;
         this.numChangedPhenotype = 0;
         this.fitCalledCount = 0;
         this.randomWalkCount = 0;
         this.hillClimbCount = 0;
         this.steepestHillClimbCount = 0;
         this.hillClimbSuccessCount = 0;
         this.steepestHillClimbSuccessCount = 0;
      }

      System.out.println();
      System.out.println("Current method: reset()");
      System.out.println("reset() successful");
      System.out.println();

   } // end reset()

   // plotFitness()
   public void plotFitness() {

      // instantiate writer and file
      PrintWriter csvWriter;
      File csvFile = new File("src/main/plot_data.csv");

      // create file, disregard overwriting
      try {
         csvFile.createNewFile();
      } catch (IOException e) {
         System.err.println("CSV file not created");
      }

      // write data
      try {
         csvWriter = new PrintWriter(csvFile);

         // first row
         csvWriter.print("generation,bestFitness,avgFitness\n");

         // each row is a generation
         for (int gen = 0; gen < this.generations; gen++) {
            csvWriter.printf("%d,%f,%f\n", gen, this.bestHistory[gen], this.averageHistory[gen]);
         }

         // close writer
         csvWriter.flush();
         csvWriter.close();

      } catch (FileNotFoundException e) {
         System.err.println("CSV file not found");
      }

      Plot plt = Plot.create();

      System.out.println();
      System.out.println("Current method: plotFitness()");
      System.out.println("TODO: integrate with csv_parser and implement matplotlib there");
      System.out.println();
   }

   // diversityStats()
   public Double[] diversityStats() {

      int k = 0;

      int hamDistCount = (this.populationSize * (this.populationSize - 1)) / 2;
      Double[] hamDistList = arrMgr.array1dNums(hamDistCount, 1.0);
      Double[] isUniqueList = arrMgr.array1dNums(this.populationSize, 1.0);

      // iterate through all hamming distances
      for (int i = 0; i < this.populationSize; i++) {
         for (int j = i + 1; j < this.populationSize; j++) {

            // calculate hamming distance between two genotypes and store in hamDistList
            hamDistList[k] = arrMgr.array1dMean(arrMgr.array1dAbsDiff(genotypes[i], genotypes[j]));

            // if the hamming distance falls within the overlapTolerance, the genotypes are
            // not unique
            if (hamDistList[k] <= this.overlapTolerance) {
               isUniqueList[i] = 0.0;
               isUniqueList[j] = 0.0;
            }

            // increment k to represent the next pair of genotypes
            k += 1;
         }
      }

      // the current generation's average hamming distance (i.e. diversity) and degree
      // of uniqueness
      Double avgHammDist = arrMgr.array1dMean(hamDistList);
      Double uniqueness = arrMgr.array1dMean(isUniqueList);

      // record in history
      this.avgHammDistHistory[this.currGeneration] = avgHammDist;
      this.uniquenessHistory[this.currGeneration] = arrMgr.array1dMean(isUniqueList);

      // stats array
      Double[] statsArray = new Double[2];
      statsArray[0] = avgHammDist;
      statsArray[1] = uniqueness;

      System.out.println();
      System.out.println("Current method: diversityStats()");
      System.out.println("diversityStats() successful");
      System.out.println();

      return statsArray;

   } // end diversityStats()

   // fitnessStats()
   public Double[][] fitnessStats() {

      // compute fitness stats for current generation
      Integer[] bestIndividual = this.genotypes[arrMgr.array1dMaxIndex(this.fitness)];
      Double bestFitness = arrMgr.array1dMax(this.fitness);
      Double avgFitness = arrMgr.array1dMean(this.fitness);

      // record in history
      this.averageHistory[this.currGeneration] = avgFitness;
      this.bestHistory[this.currGeneration] = bestFitness;

      // statsArray
      Double[][] statsArray = new Double[3][bestIndividual.length];
      statsArray[0][0] = avgFitness;
      statsArray[1][0] = bestFitness;
      statsArray[2] = arrMgr.array1dIntToDouble(bestIndividual);

      System.out.println();
      System.out.println("Current method: fitnessStats()");
      System.out.println("fitnessStats() successful");
      System.out.println();

      return statsArray;

   } // end fitnessStats()

   // stepRandomWalk()
   public Integer[] stepRandomWalk(Integer[] phenotype, Integer stepSize) {

      // for dev
      if (this.LOG_LEVEL > 0) {
         this.randomWalkCount += 1;
      }

      ArrayList<Integer> indexList = new ArrayList<Integer>();
      while (indexList.size() < stepSize) {

         // choose random gene to flip
         Integer geneIndxToFlip = randomizer.nextInt(this.genomeLength);

         if (!indexList.contains(geneIndxToFlip)) {
            indexList.add(geneIndxToFlip);

            // access gene in phenotype at specified index, and flip
            phenotype[geneIndxToFlip] = (phenotype[geneIndxToFlip] + 1) % 2;
         }
      }

      System.out.println();
      System.out.println("Current method: stepRandomWalk()");
      System.out.printf("stepRandomWalk() successful for %d step(s)", stepSize);
      System.out.println();
      System.out.println();

      return phenotype;
   } // end stepRandomWalk()

   // stepHillClimb()
   public Integer[] stepHillClimb(Integer[] phenotype, Integer stepSize, Integer attempts) {

      // for dev
      if (this.LOG_LEVEL > 0) {
         this.hillClimbCount += 1;
      }

      // record original phenotype
      Integer bestFitness = this.landscape.fitness(phenotype);
      Integer[] bestPhenotype = this.arrMgr.copyArray1d(phenotype);

      for (int a = 0; a < attempts; a++) {

         // construct new phenotype, that will climb up the gradient
         Integer[] newPhenotype = this.arrMgr.copyArray1d(phenotype);

         // basically random walk
         ArrayList<Integer> indexList = new ArrayList<Integer>();
         while (indexList.size() < stepSize) {
            Integer geneIndxToFlip = randomizer.nextInt(this.genomeLength);
            if (!indexList.contains(geneIndxToFlip)) {
               indexList.add(geneIndxToFlip);
               newPhenotype[geneIndxToFlip] = (newPhenotype[geneIndxToFlip] + 1) % 2;
            }
         }

         // evaluate fitness after random walk
         Integer newFitness = this.landscape.fitness(newPhenotype);

         // if new fitness is higher, replace best phenotype
         if (newFitness > bestFitness) {

            // record success count for dev
            if (this.LOG_LEVEL > 0) {
               this.hillClimbSuccessCount += 1;
            }
            bestFitness = newFitness;
            bestPhenotype = newPhenotype;
         }

      }

      System.out.println();
      System.out.println("Current method: stepHillClimb()");
      System.out.printf("stepHillClimb() successful for %d step(s), with %d attempt(s)", stepSize, attempts);
      System.out.println();
      System.out.println();

      return bestPhenotype;

   } // end stepHillClimb()

} // end class