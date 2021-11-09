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

experimentParamatersHeader = "EXPERIMENT_PARAMS"

experimentStorer = 0 #Name of the experiment storer

parent_dir = os.path.split(os.getcwd())[0]
experimentNumber = -1 #will get set inside of the open loop
genRowNumber = -1

#parse the CSV file
with open(filename) as csvfile:
    datareader = csv.reader(csvfile, delimiter=',')

    for row in datareader:
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
        else:
            experimentData.addGenerationToExperiment(row)


# GraphMaker.makeSingleStrategyPlotAllStepsVsFitness(experimentData)
GraphMaker.makeSingleStrategyPlotVsFitness(experimentData, 1)


#get our data to graph
# numGenerations = int(experimentParamaters[2])
# colors = cm.get_cmap('RdYlGn') #copper is pretty neat
# numColors = colors.N
# numColorsToIteratePerGeneration = colors.N / numGenerations

# #temporary, hardcoded values (REFACTOR)
# numRunsFromEachStart = 10 #hardcoded for now
# numStarts = 50
# indexOfLastGeneration = 10
# numSteps = 100

# #get our generations to graph
# #get our data to graph
# numGenerations = int(experimentParamaters[2])
# numRunsFromEachStart = 100 #hardcoded for now
# colors = cm.get_cmap('RdYlGn') #copper is pretty neat
# numColors = colors.N
# numColorsToIteratePerGeneration = colors.N / numGenerations

# #get our generations to graph
# generations = []
# # for i in range(numRunsFromEachStart):
# generations.append(experimentData[0][4][numGenerations-1]) #grab the last generation of this run

# # stepFrequencies = []
# # for i in range(15):
# #     stepFrequencies.append([])
# #     for j in range(numRunsFromEachStart):
# #         stepFrequencies[i].append(generations[j][i])

# #set up our plot
# fig, ax = plt.subplots()
# ax.set_title("1 NKFL 1 Start Location 1 Evolution Runs K=0")
# ax.set_xlabel("Step Number")
# ax.set_ylabel("Average frequency of SHC steps")

# #add text to plot
# props = dict(boxstyle='round', facecolor='wheat', alpha=0.5)
# ax.text(0.02, 0.99, textStr, transform=ax.transAxes, fontsize=10,
#         verticalalignment='top', bbox=props)

# #actually make the graph
# ax.plot([0,1,2,3,4,5,6,7,8,9,10,11,12,13,14], experimentData[0][4][numGenerations-1])


# #show the graph
# plt.show()




