#The experimentStorer class is used to store the results of the experiments.
#The experimentStorer class has the following instance variables

#High Level Data Storage (Overall Data)
#ExperimentRawNumber: The raw number of the experiment
#ExperimentDetailNumber: A string (format X.X.X) that stores the experiment number.starting location number.evoultion number
#ExperimentParamaters: A list of strings of the parameters used in the experiment

#Mid Level Data Storage (Arrays based Data)
#ExperimentSHCFitness: An int that is the fitness of SHC
#ExperimentSHCFitnessArray: An array of the fitness of SHC over the steps
#Generations: An array with each generation of the experiment

#Each Generation has the following data
#StepFrequencies: An array of the frequencies of '1' in each step
#FitnessAtStep: An array of the fitness at each step
#FinalFitness: An int that is the average final fitness of the generation

#Low Level Data Storage (Individual Paramaters)
#numberOfGenerations: The number of generations in the experiment
#numberGenerationsWritten: The number of generations that have been written to the file
#popsPerGeneration: The number of individuals in each generation
#childrenPercentage: The percentage of children in each generation
#mutatePercentage: The mutation rate
#strategyLength: The length of the strategy
#n: The n value of the landscape
#k: The k value of the landscape


#ExperimentComparisonFitness: A string that is the fitness of pure SHC for this experiment
class ExperimentStorer:

    def __init__(self, experimentParameters):
        self.experimentParamaters = experimentParameters
        self.numberOfExperiments = 0
        self.experiments = []
        if (self.parseExperimentParamaters() == -1):
            print("Experiment Paramaters not properly formatted")

    def beginAddingExperiment(self, row):
        self.numberOfExperiments += 1
        self.experiments.append(Experiment(row[1],row[2]))
    
    def addGenerationToExperiment(self, row):
        self.experiments[self.numberOfExperiments - 1].addGeneration(row)

    def addFitnessArray(self, row):
        self.experiments[self.numberOfExperiments - 1].addFitnessArrayToMostRecentGeneration(row[1:])

    def addStepFrequencyArray(self, row):
        self.experiments[self.numberOfExperiments - 1].addStepFrequencyArrayToMostRecentGeneration(int(row[0][0]), row[1:])

    def addStuckAtLocalOptimaArray(self, row):
        self.experiments[self.numberOfExperiments - 1].addStuckAtLocalOptimaArray(row[1:])
    
    def finishAddingExperiment(self, row):
        self.experiments[self.numberOfExperiments - 1].addComparisonFitness(row)
    
    #TODO: Once java paramater map is implemented, rewrite this method
    def parseExperimentParamaters(self):
        if(self.experimentParamaters[0] != "EXPERIMENT_PARAMS"):
            return -1
        self.numSimulations = self.experimentParamaters[1]
        self.numGenerationsWritten = self.experimentParamaters[2]
        self.strategyLength = self.experimentParamaters[3]
        self.numGenerations = self.experimentParamaters[4]
        self.popsPerGeneration = self.experimentParamaters[5]
        self.childrenPercentage = self.experimentParamaters[6]
        self.mutatePercentage = self.experimentParamaters[7]
        self.n = self.experimentParamaters[8]
        self.k = self.experimentParamaters[9]
        self.hillClimbSteepest = self.experimentParamaters[10]
        self.seed = self.experimentParamaters[11]
        self.reportPercentage = self.experimentParamaters[12]
        return 0

        
class Experiment:
    def __init__(self, rawExperimentNumber, detailExperimentNumber):
        self.generations = []
        self.numGenerations = 0
        self.rawExperimentNumber = rawExperimentNumber
        self.detailExperimentNumber = detailExperimentNumber
        self.finalFitness = 0
        self.comparisonFitness = 0
    
    def addGeneration(self, row):
        self.generations.append(Generation(row[0],row[1],row[2],row[3]))
        self.numGenerations += 1
    
    def addFitnessArrayToMostRecentGeneration(self, array):
        self.generations[self.numGenerations - 1].addFitnessArray(array)

    def addStepFrequencyArrayToMostRecentGeneration(self, step, array):
        self.generations[self.numGenerations - 1].addStepFrequencyArray(step, array)

    def addStuckAtLocalOptimaArray(self, array):
        self.generations[self.numGenerations - 1].addStuckAtLocalOptimaArray(array)

    def addComparisonFitness(self, row):
        self.finalFitness = self.generations[self.numGenerations - 1].fitness
        self.comparisonFitness = float(row[1])

    def getFinalGeneration(self):
        return self.generations[self.numGenerations - 1]

class Generation:
    def __init__(self, generationNumber, fitness, fitnessLuckAdjusted, wholeFitness):
        self.generationNumber = generationNumber
        self.fitnessLuckAdjusted = fitnessLuckAdjusted
        self.fitness = fitness
        self.wholeFitness = wholeFitness
        self.stepFrequencies = [-1, -1, -1, -1]
        # for i in range(len(stepFrequencies)):
        #     if(stepFrequencies[i] != ""):
        #         self.stepFrequencies.append(float(stepFrequencies[i]))
    
    def addFitnessArray(self, fitnessAtSteps):
        self.fitnessAtSteps = [float(i) for i in fitnessAtSteps]
    
    def addStepFrequencyArray(self, step, frequencies):
        self.stepFrequencies[step] = [float(i) for i in frequencies]
    
    def addStuckAtLocalOptimaArray(self, stuckAtLocalOptima):
        self.stuckAtLocalOptima = [float(i) for i in stuckAtLocalOptima]
    