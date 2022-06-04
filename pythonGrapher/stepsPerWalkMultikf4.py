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

kValues = []

kArrayS = []
kArrayF = []

arrayS = []
arrayF = []

currS = []
currF = []

kpurewalk = []
kSHC = []
kAlternate = []
kBalance = []

arrayPureWalk = []
arraySHC = []
arrayAlternate = []
arrayBalance = []


currPureWalk = []
currSHC = []
currAlternate = []
currBalance = []

topGen = '50'

toSkip = 0 #skip lines
#parse the CSV file

specialStrat = "none"
with open(filename) as csvfile:
    datareader = csv.reader(csvfile, delimiter=',')
    currentK = ""
    for row in datareader:
        if(toSkip > 0):
            toSkip = toSkip - 1
            continue
        elif(row[0] == "SIMULATION"):
            # print(currentK + " " + row[5] + "==" + str(currentK==row[5]))
            specialStrat = 'none'
            if(currentK == ""):
                currentK = int(row[5][8:])
            if(currentK != int(row[5][8:])):
                kValues.append(currentK)
                currentK = int(row[5][8:])
                # print(currentK)
                kArrayS.append(arrayS)
                kArrayF.append(arrayF)
                kpurewalk.append(arrayPureWalk)
                kSHC.append(arraySHC)
                kAlternate.append(arrayAlternate)
                kBalance.append(arrayBalance)
                arrayS = []
                arrayF = []
                arrayPureWalk = []
                arraySHC = []
                arrayAlternate = []
                arrayBalance = []
        elif(len(row)==0):
            print("finished parsing data")
            # print(row)
        elif(row[0] == 'GENERATION'):
            if(row[1] != topGen):
                toSkip = 2
                continue
        elif(row[0] == 'STRATEGY_ROW'):
            j = 0
            for i in row:
                if(i != 'STRATEGY_ROW'):
                    currS.append(i)
                    j = j + 1
            arrayS.append(currS)
            currS = []
        elif(row[0] == 'FITNESS_ROW'):
            if(specialStrat == 'none'):
                j=0
                for i in row:
                    if(i != 'FITNESS_ROW'):
                        currF.append(float(i))
                        j = j + 1
                arrayF.append(currF)
                currF = []
            elif(specialStrat == 'PureWalk'):
                for x in row:
                    if(x != 'FITNESS_ROW'):
                        currPureWalk.append(float(x))
                arrayPureWalk.append(currPureWalk)
                currPureWalk = []
            elif(specialStrat == 'SHC'):
                 for x in row:
                    if(x != 'FITNESS_ROW'):
                       currSHC.append(float(x))
                 arraySHC.append(currSHC)
                 currSHC = []
            elif(specialStrat == 'Alternate'):
                 for x in row:
                    if(x != 'FITNESS_ROW'):
                        currAlternate.append(float(x))
                 arrayAlternate.append(currAlternate)
                 currAlternate = []
            elif(specialStrat == 'Balanced'):
                 for x in row:
                    if(x != 'FITNESS_ROW'):
                       currBalance.append(float(x))
                 arrayBalance.append(currBalance)
                 currBalance = []
        # elif(row[0] )
        elif(row[0] == 'COMPARISON_STRATEGIES'):
            continue
        elif(row[0] == 'PureWalk'):
            specialStrat = 'PureWalk'
            toSkip = 1
        elif(row[0] == 'Steep Hill Climb'):
            specialStrat = 'SHC'
            toSkip = 1
        elif(row[0] == 'AlternateLookWalk'):
            specialStrat = 'Alternate'
            toSkip = 1
        elif(row[0] == 'Balanced'):
            specialStrat = 'Balanced'
            toSkip = 1
        
#Add on the last one
kArrayS.append(arrayS)
kArrayF.append(arrayF)
kValues.append(currentK)
kpurewalk.append(arrayPureWalk)
kSHC.append(arraySHC)
kAlternate.append(arrayAlternate)
kBalance.append(arrayBalance)

print(len(kArrayF[0][0]))

simsPerK = len(kArrayS[0])
stepsPerSim = len(kArrayS[0][0])

kLooksBetweenWalk = []
arrLooksBetweenWalk = []
RWSatWalks = []

kFitness = []
arrFitness = []
fitness = []

numtot = 0
nk = 0

#arrange out-of-order Ks
SSorted= []
FSorted= []
PureWalkSorted = []
SHCSorted = []
AlternateSorted = []
BalancedSorted = []
for i in range(len(kValues)):
    SSorted.append([])
    FSorted.append([])
    PureWalkSorted.append([])
    SHCSorted.append([])
    AlternateSorted.append([])
    BalancedSorted.append([])


for i in range(len(kValues)):
    SSorted[kValues[i]] = kArrayS[i]
    FSorted[kValues[i]] = kArrayF[i]
    PureWalkSorted[kValues[i]] = kpurewalk[i]
    SHCSorted[kValues[i]] = kSHC[i]
    AlternateSorted[kValues[i]] = kAlternate[i]
    BalancedSorted[kValues[i]] = kBalance[i]

kArrayS = SSorted
kArrayF = FSorted
kpurewalk = PureWalkSorted
kSHC = SHCSorted
kAlternate = AlternateSorted
kBalance = BalancedSorted

kLooksBetweenWalk = []
arrLooksBetweenWalk = []
looksBetweenWalks = []

fitness = []
arrFitness = []
kFitness = []

numRW = 0
numtot = 0
nk = 0
for k in kArrayS:
    nsim = 0
    for sim in k:
        looksSinceLastWalk = 0
        stepnum = 0
        for step in sim:
            if(step == 'Walk'):
                numtot = numtot + 1
                if(looksSinceLastWalk == 0):
                    numRW = numRW + 1
                looksBetweenWalks.append(looksSinceLastWalk)
                looksSinceLastWalk = 0
                fitness.append(kArrayF[nk][nsim][stepnum])
            else:
                looksSinceLastWalk = looksSinceLastWalk + 1
            stepnum = stepnum + 1
        arrLooksBetweenWalk.append(looksBetweenWalks)
        looksBetweenWalks = []
        arrFitness.append(fitness)
        fitness = []
        nsim = nsim + 1
    nk = nk + 1
    kLooksBetweenWalk.append(arrLooksBetweenWalk)
    arrLooksBetweenWalk = []
    kFitness.append(arrFitness)
    arrFitness = []

# print(kLooksBetweenWalk[0][0])


for numInvestigating in range(len(kFitness)):
    looksAtSteps = []
    fitnessAtSteps = []
    simInvestigating = kLooksBetweenWalk[numInvestigating]
    fitInvestigating = kFitness[numInvestigating]

    maxNumOfWalks = 0
    for sim in simInvestigating:
        # if(len(sim) != 10):
        #     print(sim)
        if(len(sim) > maxNumOfWalks):
            maxNumOfWalks = len(sim)

    for i in range(maxNumOfWalks):
        looksAtSteps.append([])
        fitnessAtSteps.append([])

    for sim in simInvestigating:
        for i in range(maxNumOfWalks):
            if(i < len(sim)):
                looksAtSteps[i].append(sim[i])
                fitnessAtSteps[i].append(fitInvestigating[simInvestigating.index(sim)][i])
            else:
                looksAtSteps[i].append(0)
                fitnessAtSteps[i].append(0)



    # print(len(looksAtSteps))

    plot_error = "standard_error"
    #plot_error = "standard_deviation"
    mean_values = []
    lower_errors = []
    upper_errors = []


    n = simsPerK
    xaxis = np.arange(0, maxNumOfWalks, 1)
    # print(maxNumOfWalks)
    for i in range(len(looksAtSteps)):
        mean = statistics.mean(looksAtSteps[i])
        mean_values.append(mean)

        std = statistics.stdev(looksAtSteps[i])
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

    mean_values2 = []
    lower_errors2 = []
    upper_errors2 = []

    for i in range(len(fitnessAtSteps)):
        mean = statistics.mean(fitnessAtSteps[i])
        mean_values2.append(mean)

        std = statistics.stdev(fitnessAtSteps[i])
        if plot_error == "standard_error":
            error = std/np.sqrt(n)
            lower_errors2.append( mean - error )
            upper_errors2.append( mean + error )
        elif plot_error == "standard_deviation":
            lower_errors2.append( mean - std )
            upper_errors2.append( mean + std )
        else:
            print("plot_error must be standard_error or standard_deviation")
            exit(1)

    n = simsPerK
    xaxis2 = np.arange(0, maxNumOfWalks, 1)

    # for i in range(l)
                
    # print(looksBetweenWalksPerSim)
    fig, ax = plt.subplots()
    ax2 = ax.twinx()
    plt.title("Number of Looks Between Walks at K=" + str(numInvestigating))

    leg1 = ax.plot(xaxis, mean_values, color="red", label="num looks between walks" )
    legerror1 = ax.fill_between(xaxis, lower_errors, upper_errors, alpha=0.25, facecolor="red", label="num looks between walks error")

    leg2 = ax2.plot(xaxis2, mean_values2, color="blue", label="fitness" )
    legerror2 = ax2.fill_between(xaxis2, lower_errors2, upper_errors2, alpha=0.25, facecolor="blue", label="fitness error")

    # # for arr in looksBetweenWalksPerSim:
    # #     ax.plot(arr, color="blue", alpha=0.1)

ax.set_xlabel('Walk Number')
ax.set_ylabel('Looks Before Walk')
ax.legend(loc = 'upper left')

ax.set_ylim([0, max(mean_values) + 1])
ax2.set_ylim([0, 1])
ax2.legend(loc = 'upper right')

plt.xticks(np.arange(0, maxNumOfWalks, 2))
plt.savefig("plotOutput/"+"k=" + str(numInvestigating) + ".png")
    # plt.show()