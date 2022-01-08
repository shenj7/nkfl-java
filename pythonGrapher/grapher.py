import csv
import os
import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib import cm
# from colorspacious import cspace_converter
from collections import OrderedDict
from tkinter.filedialog import askopenfilename

import ExperimentStorer
import GraphMaker

cmaps=OrderedDict()
filename = askopenfilename()
filename2 = askopenfilename()

experimentParamatersHeader = "EXPERIMENT_PARAMS"

experimentStorer = 0 #Name of the experiment storer

parent_dir = os.path.split(os.getcwd())[0]
experimentNumber = -1 #will get set inside of the open loop
genRowNumber = -1

#parse the CSV file
with open(filename) as csvfile:
    datareader = csv.reader(csvfile, delimiter=',')

    for row in datareader:
        # print(row)
        if(len(row)==0):
            print("finished parsing data")
        elif(row[0] == experimentParamatersHeader):
            experimentData = ExperimentStorer.ExperimentStorer(row)
        elif(row[0].startswith('Experiment Number: ')):
            experimentData.beginAddingExperiment(row)
        elif(row[0].startswith('Comparison: ')):
            experimentData.finishAddingExperiment(row)
        elif(row[0].startswith('FITNESS_AT_STEPS')):
            experimentData.addFitnessArray(row)
        elif(row[0].startswith('STUCK_AT_LOCAL_OPTIMA')):
            experimentData.addStuckAtLocalOptimaArray(row)
        elif(row[0].endswith(':STEP_FREQUENCIES_OF')):
            experimentData.addStepFrequencyArray(row)
        elif(row[0].startswith('CONSECUTIVE RWS')):
            experimentData.addConsecutiveRWS(row)
        else:
            experimentData.addGenerationToExperiment(row)

with open(filename2) as csvfile2:
    datareader2 = csv.reader(csvfile2, delimiter=',')

    for row in datareader2:
        # print(row)
        if(len(row)==0):
            print("finished parsing data")
        elif(row[0] == experimentParamatersHeader):
            experimentData2 = ExperimentStorer.ExperimentStorer(row)
        elif(row[0].startswith('Experiment Number: ')):
            experimentData2.beginAddingExperiment(row)
        elif(row[0].startswith('Comparison: ')):
            experimentData2.finishAddingExperiment(row)
        elif(row[0].startswith('FITNESS_AT_STEPS')):
            experimentData2.addFitnessArray(row)
        elif(row[0].startswith('STUCK_AT_LOCAL_OPTIMA')):
            experimentData2.addStuckAtLocalOptimaArray(row)
        elif(row[0].endswith(':STEP_FREQUENCIES_OF')):
            experimentData2.addStepFrequencyArray(row)
        elif(row[0].startswith('CONSECUTIVE RWS')):
            experimentData2.addConsecutiveRWS(row)
        else:
            experimentData2.addGenerationToExperiment(row)

# GraphMaker.makeSingleStrategyPlotAllStepsVsFitness(experimentData)
# GraphMaker.makeFinalFitnessofStrategiesPlot(experimentData)
GraphMaker.makeRelativeRWLocationAtSteps(experimentData, experimentData2)
# GraphMaker.makeConRWsofStrategiesPlot(experimentData)




