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

fitnessArray = []
fitArray2 = []

for i in range(50):
    fitnessArray.append([])

#parse the CSV file
with open(filename) as csvfile:
    datareader = csv.reader(csvfile, delimiter=',')

    for row in datareader:
        if(len(row)==0):
            print("finished parsing data")
            # print(row)
        elif(row[0] == 'FITNESS_ROW'):
            j = 0
            fitArray2.append([])
            for i in row:
                if(i != 'FITNESS_ROW'):
                    fitnessArray[j].append(float(i))
                    fitArray2[len(fitArray2)-1].append(float(i))
                    j = j + 1
            
print(len(fitArray2[0]))



#just use matplotlib to plot fitnessArray
#make the x axis be the index
#make the y axis be the fitness
#have an error bar

#plotting standard error or standard deviation
# Generally standard error is the best to report
plot_error = "standard_error"
#plot_error = "standard_deviation"


fig, ax = plt.subplots()

xaxis = np.arange(0, len(fitnessArray), 1)

mean_values = []
lower_errors = []
upper_errors = []
n = len(fitnessArray[0])

for i in range(len(fitnessArray)):
    mean = statistics.mean(fitnessArray[i])
    mean_values.append(mean)

    std = statistics.stdev(fitnessArray[i])
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

plt.title("Fitness of one strategy on different runs (strategyRuns = 1000)")
# ax.plot(xaxis, mean_values, color="red", label="average fitness at step" )
# ax.fill_between(xaxis, lower_errors, upper_errors, alpha=0.25, facecolor="red", label="average fitness at step error")

for arr in fitArray2:
    ax.plot(arr, color="blue", alpha=0.1)

ax.set_xlabel('Steps')
ax.set_ylabel('Fitness')
ax.legend()

plt.show()
