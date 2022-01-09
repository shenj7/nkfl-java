
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
    
    def addConsecutiveRWS(self, row):
        self.experiments[self.numberOfExperiments - 1].addConsecutiveRWS(row[1], row[2:])

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
    
    def addConsecutiveRWS(self, num, list):
        self.generations[self.numGenerations - 1].addConsecutiveRWS(num, list)
    
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
        self.consecutiveRWS = 0
        self.stepFrequencies = [-1, -1, -1, -1]
        self.consecutiveRWSAtSteps = []
        # for i in range(len(stepFrequencies)):
        #     if(stepFrequencies[i] != ""):
        #         self.stepFrequencies.append(float(stepFrequencies[i]))
    
    def addFitnessArray(self, fitnessAtSteps):
        self.fitnessAtSteps = [float(i) for i in fitnessAtSteps]
    
    def addConsecutiveRWS(self, num, list):
        self.consecutiveRWS = num
        self.consecutiveRWSAtSteps = [float(i) for i in list]
        # print(self.consecutiveRWSAtSteps)
    
    def addStepFrequencyArray(self, step, frequencies):
        self.stepFrequencies[step] = [float(i) for i in frequencies]
    
    def addStuckAtLocalOptimaArray(self, stuckAtLocalOptima):
        self.stuckAtLocalOptima = [float(i) for i in stuckAtLocalOptima]
    
    # def getNumConsecutiveRWSteps(self, threshold):
    #     stepFrequenciesLength = len(self.stepFrequencies)
    #     print(self.stepFrequencies)
    #     conRWs = 0
    #     for i in range(len(self.stepFrequencies[0])-1):
    #         if(self.stepFrequencies[0][i] >= threshold and self.stepFrequencies[0][i+1] >= threshold):
    #             conRWs += 1
    #     return conRWs
    