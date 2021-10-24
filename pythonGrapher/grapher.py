import csv
import os
import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib import cm
# from colorspacious import cspace_converter
from collections import OrderedDict
from tkinter.filedialog import askopenfilename

cmaps=OrderedDict()
filename = askopenfilename()

experimentParamatersHeader = "EXPERIMENT_PARAMS"
experimentParamaters = []
experimentData = []

#Experiment data is stored as follows
#experimentData[X] will access the xth experiment
#experimentData[X][X] will access the xth generation of the xth experiment
#experimentData[X][X][X] will access the xth step of the ... 

#ExperimentData[X] has experiment number at [0], the comparison fitness value at [1], the list of generation fitnesses at [2]. the list of generation numbers at [3], and the list of generations at index [4]
#ExperimentData[X][Y] is the Yth generation of the Xth experiment, and it is a list of average frequencies of '1' steps

# def initExperimentData():
#     numExperiments = int(experimentParamaters[1],10)
#     numGenerationsPerExperiment = int(experimentParamaters[2],10)
#     numStepsPerStrategy = int(experimentParamaters[3],10)
#     experimentData = numpy.zeros((numExperiments, numGenerationsPerExperiment, numStepsPerStrategy), dtype=numpy.double)


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
            experimentParamaters = row
        elif(row[0].startswith('Experiment Number: ')):
            experimentNumber = int(row[2])
            experimentData.append([experimentNumber, -1])
            genRowNumber = 0
            experimentData[experimentNumber].append(np.zeros(int(experimentParamaters[2]), dtype=float)) #2
            experimentData[experimentNumber].append(np.zeros(int(experimentParamaters[2]), dtype=int)) #3
            experimentData[experimentNumber].append([]) #4
        elif(row[0].startswith('Comparison: ')):
            experimentData[experimentNumber][1] = float(row[1])
        else:
            experimentData[experimentNumber][4].append(np.zeros(int(experimentParamaters[3]), dtype=float))
            for i in range(len(row)):
                if(i == 0):
                    experimentData[experimentNumber][3][genRowNumber] = int(row[i])
                elif(i == 1):
                    experimentData[experimentNumber][2][genRowNumber] = float(row[i])
                else:
                    experimentData[experimentNumber][4][genRowNumber][i-2] = float(row[i])
            genRowNumber = genRowNumber + 1


print("Comparison :" + str(experimentData[0][1]) + ", evolved: " + str(experimentData[0][2][9]))

#get our data to graph
numGenerations = int(experimentParamaters[2])
colors = cm.get_cmap('RdYlGn') #copper is pretty neat
numColors = colors.N
numColorsToIteratePerGeneration = colors.N / numGenerations

#get our generations to graph
generations = []
for i in range(numGenerations):
    generations.append(experimentData[0][4][i])

fig, ax = plt.subplots()
#set up out plot
ax.set_title("1 landscape 1 location 1 evolution")
ax.set_xlabel("Step Number")
ax.set_ylabel("Average frequency of SHC steps")

#actually make the graph
for i in range(numGenerations):
  ax.plot([0,1,2,3,4,5,6,7,8,9,10,11,12,13,14],generations[i],color=colors(i*30))


#show the graph
plt.show()