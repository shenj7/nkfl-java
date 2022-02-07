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
import statistics

cmaps=OrderedDict()
filename = askopenfilename()

genFitnessArray = []
genStepArray = []

#CHANGE IF STRATEGY LENGTH CHANGES
for i in range(50):
    genFitnessArray.append([])
    genStepArray.append([])

toSkip = 0 #skip lines
#parse the CSV file
with open(filename) as csvfile:
    datareader = csv.reader(csvfile, delimiter=',')

    for row in datareader:
        if(toSkip > 0):
            toSkip = toSkip - 1
            continue
        elif(len(row)==0):
            print("finished parsing data")
            # print(row)
        elif(row[0] == 'GENERATION'):
            if(row[1] != '100'):
                toSkip = 2
                continue
        elif(row[0] == 'STRATEGY_ROW'):
            j = 0
            for i in row:
                if(i != 'STRATEGY_ROW'):
                    genStepArray[j].append(i)
                    j = j + 1
        elif(row[0] == 'FITNESS_ROW'):
            j=0
            for i in row:
                if(i != 'FITNESS_ROW'):
                    genFitnessArray[j].append(float(i))
                    j = j + 1
        elif(row[0] == 'COMPARISON_STRATEGIES'):
            toSkip = 9
            continue

# print(genFitnessArray)
# print(genStepArray)

looksBetweenWalksPerSim = []

for i in range(len(genStepArray[0])):
    looksBetweenWalksPerSim.append([])
    looksSinceLastWalk = 0
    for j in range(len(genStepArray)):
        if(genStepArray[j][i] == 'Walk'):
            looksBetweenWalksPerSim[i].append(looksSinceLastWalk)
            looksSinceLastWalk = 0
        else:
            looksSinceLastWalk = looksSinceLastWalk + 1

print(looksBetweenWalksPerSim)

looksBetweenWalksPerStep = []
for i in range(len(looksBetweenWalksPerStep)):
    looksBetweenWalksPerStep.append([])
    for j in range(len(looksBetweenWalksPerSim[i])):
        looksBetweenWalksPerStep[len(looksBetweenWalksPerStep)-1].append(looksBetweenWalksPerSim[i][j])

plot_error = "standard_error"
#plot_error = "standard_deviation"
mean_values = []
lower_errors = []
upper_errors = []
n = len(looksBetweenWalksPerSim)
xaxis = np.arange(0, len(looksBetweenWalksPerStep), 1)

for i in range(len(looksBetweenWalksPerStep)):
    mean = statistics.mean(looksBetweenWalksPerStep[i])
    mean_values.append(mean)

    std = statistics.stdev(looksBetweenWalksPerStep[i])
    if plot_error == "standard_error":
        error = std/np.sqrt(n)
        lower_errors.append( mean - error )
        upper_errors.append( mean + error )
    elif plot_error == "standard_deviation":
        lower_errors.append( mean - std )
        upper_errors.append( mean + std )
    else:
        print("plot_error must be standard_error or standard_deviation")
        exit(1)
            
# print(looksBetweenWalksPerSim)
fig, ax = plt.subplots()
plt.title("Fitness of one strategy on different runs (strategyRuns = 1000)")

ax.plot(xaxis, mean_values, color="red", label="average fitness at step" )
ax.fill_between(xaxis, lower_errors, upper_errors, alpha=0.25, facecolor="red", label="average fitness at step error")

# for arr in looksBetweenWalksPerSim:
#     ax.plot(arr, color="blue", alpha=0.1)

ax.set_xlabel('Steps')
ax.set_ylabel('Fitness')
ax.legend()

plt.show()
