import statistics
import ExperimentStorer
import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib import cm

def makeSingleStrategyPlotVsFitness(experimentStorer, stepToInspect):
    xaxis = np.arange(0, int(experimentStorer.strategyLength), 1)

    fig = plt.figure()
    ax = fig.add_subplot(111)
    stepname = "SHC"

    plt.xlabel("Step Number")
    ax.plot(xaxis, experimentStorer.experiments[0].getFinalGeneration().stepFrequencies[stepToInspect])
    #set y-axis label to frequency of SCH steps
    plt.ylabel("Frequency of "+stepname+" steps")
    ax.plot(xaxis, experimentStorer.experiments[0].getFinalGeneration().fitnessAtSteps)
    #set the title to Step Number vs. Frequency of SCH steps
    plt.title("Step Number vs. Frequency of '"+stepname+"' steps")
    #add a second y-axis title that is the fitness at each step
    ax.axhline(y=experimentStorer.experiments[0].comparisonFitness, color='r', linestyle=':')
    #make a legend for the plot
    plt.legend(["Frequency of "+stepname+" steps", "Fitness at step", "Pure SHC Comparison"])

    for x,y in zip(xaxis, experimentStorer.experiments[0].getFinalGeneration().fitnessAtSteps):
        ax.annotate(str(experimentStorer.experiments[0].getFinalGeneration().stuckAtLocalOptima[x]), xy=(x, y), textcoords="offset points", xytext=(0,10), ha='center')

    plt.show()

def makeRelativeRWLocationAtSteps(experimentStorer, experimentStorer2):
    #plotting standard error or standard deviation
    # Generally standard error is the best to report
    plot_error = "standard_error"
    #plot_error = "standard_deviation"

    fig, ax = plt.subplots()

    xaxis = np.arange(0, 15, 1)
    frequencies = []
    for i in range(15):
        frequencies.append([])
    totalNumRWS = 0

    for experiment in experimentStorer.experiments:
        for i in range(len(experiment.getFinalGeneration().consecutiveRWSAtSteps)):
            frequencies[i].append(int(experiment.getFinalGeneration().consecutiveRWSAtSteps[i])/10)
            totalNumRWS += int(experiment.getFinalGeneration().consecutiveRWSAtSteps[i])

    mean_values = []
    lower_errors = []
    upper_errors = []
    n = len(frequencies[0])

    for i in range(len(frequencies)):
        mean = statistics.mean(frequencies[i])
        mean_values.append(mean)

        std = statistics.stdev(frequencies[i])
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
    #second
    frequencies2 = []
    for i in range(15):
        frequencies2.append([])
    totalNumRWS2 = 0

    for experiment2 in experimentStorer2.experiments:
        for i in range(len(experiment2.getFinalGeneration().consecutiveRWSAtSteps)):
            frequencies2[i].append(int(experiment2.getFinalGeneration().consecutiveRWSAtSteps[i])/10)
            totalNumRWS2 += int(experiment2.getFinalGeneration().consecutiveRWSAtSteps[i])

    mean_values2 = []
    lower_errors2 = []
    upper_errors2 = []
    n2 = len(frequencies2[0])

    for i in range(len(frequencies2)):
        mean2 = statistics.mean(frequencies2[i])
        mean_values2.append(mean2)

        std2 = statistics.stdev(frequencies2[i])
        if plot_error == "standard_error":
            error2 = std2/np.sqrt(n2)
            lower_errors2.append( mean2 - error2 )
            upper_errors2.append( mean2 + error2 )
        elif plot_error == "standard_deviation":
            lower_errors2.append( mean2 - std2 )
            upper_errors2.append( mean2 + std2 )
        else:
            print("plot_error must be standard_error or standard_deviation")
            exit(1)

    
    #simple produce a list with 0....to (generations-1) in it
    x = xaxis
    x2 = xaxis

    #draw mean line
    ax.plot(x, mean_values, color="red", label="ranked exponential" )
    ax.plot(x2, mean_values2, color="blue", label="truncation" )
    #draw shaded region from lower to upper
    ax.fill_between(x, lower_errors, upper_errors, alpha=0.25, facecolor="red", label="ranked exponential error")
    ax.fill_between(x2, lower_errors2, upper_errors2, alpha=0.25, facecolor="blue", label="truncation error")

    #show a legend
    plt.legend()

    plt.title("Percent of 2-RWS starting at each step")

    #add labels to the axes
    plt.xlabel('Step Number')
    plt.ylabel('Frequency of 2-RWS')

    #saves to file
    plt.savefig('agg_demo_plot.png')

    #shows on the screen
    plt.show()


def makeFinalFitnessofStrategiesPlot(experimentStorer):
    xaxis = np.arange(0, 15, 1)

    fig, ax = plt.subplots()

    avgFitnesses = []
    shcFitnesses = []
    for i in range(15):
        avg = 0
        avgs = 0
        for j in range(40):
            avg += float(experimentStorer.experiments[i * 40 + j].getFinalGeneration().fitness)
            avgs += float(experimentStorer.experiments[i * 40 + j].comparisonFitness)
        avgFitnesses.append(avg / 40)
        shcFitnesses.append(avgs / 40)

    avgFitnesses2 = []
    for i in range(15):
        avg = 0
        for j in range(40):
            avg += float(experimentStorer.experiments[(i+15) * 40 + j].getFinalGeneration().fitness)
        avgFitnesses2.append(avg / 40)
    
    ax.plot(xaxis, avgFitnesses)
    ax.plot(xaxis, avgFitnesses2)
    ax.plot(xaxis, shcFitnesses)
    ax.set_ylabel('Average Fitness')
    ax.set_xlabel('K Value')
    ax.set_title('Average Fitness of Strategies at different K Values')
    plt.legend(['truncation', 'ranked', 'Pure SHC'])

    plt.show()

    
def makeConRWsofStrategiesPlot(experimentStorer):
    xaxis = np.arange(0, 15, 1)

    fig, ax = plt.subplots()

    concrws1 = []
    concrwsSHC = []
    for i in range(15):
        rws1 = 0
        rwsSHC = 0
        for j in range(40):
            rws1 += float(experimentStorer.experiments[i * 40 + j].getFinalGeneration().consecutiveRWS)
        concrws1.append(rws1 / 40)
        concrwsSHC.append(rwsSHC / 40)

    concrws2 = []
    for i in range(15):
        rws2 = 0
        for j in range(40):
            rws2 += float(experimentStorer.experiments[(i+15) * 40 + j].getFinalGeneration().consecutiveRWS)
        concrws2.append(rws2 / 40)
    
    ax.plot(xaxis, concrws1)
    ax.plot(xaxis, concrws2)
    ax.plot(xaxis, concrwsSHC)
    ax.set_ylabel('Average Number of Consecutive RW Steps')
    ax.set_xlabel('K Value')
    ax.set_title('Average # of Concecutive RW Steps at different K Values')
    plt.legend(['truncation', 'ranked', 'Pure SHC'])

    plt.show()