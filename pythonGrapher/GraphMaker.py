import ExperimentStorer
import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib import cm

def makeSingleStrategyPlotVsFitness(experimentStorer, stepToInspect):
    xaxis = np.arange(0, int(experimentStorer.strategyLength), 1)

    fig = plt.figure()
    ax = fig.add_subplot(111)

    plt.xlabel("Step Number")
    ax.plot(xaxis, experimentStorer.experiments[0].getFinalGeneration().stepFrequencies[stepToInspect])
    #set y-axis label to frequency of SCH steps
    plt.ylabel("Frequency of SHC steps")
    ax.plot(xaxis, experimentStorer.experiments[0].getFinalGeneration().fitnessAtSteps)
    #set the title to Step Number vs. Frequency of SCH steps
    plt.title("Step Number vs. Frequency of 'SHC' steps")
    #add a second y-axis title that is the fitness at each step
    ax.axhline(y=experimentStorer.experiments[0].comparisonFitness, color='r', linestyle=':')
    #make a legend for the plot
    plt.legend(["Frequency of SHC steps", "Fitness at step", "Pure SHC Comparison"])

    for x,y in zip(xaxis, experimentStorer.experiments[0].getFinalGeneration().fitnessAtSteps):
        ax.annotate(str(experimentStorer.experiments[0].getFinalGeneration().stuckAtLocalOptima[x]), xy=(x, y), textcoords="offset points", xytext=(0,10), ha='center')

    plt.show()

def makeSingleStrategyPlotAllStepsVsFitness(experimentStorer):
    xaxis = np.arange(0, int(experimentStorer.strategyLength), 1)

    step0freqs = experimentStorer.experiments[0].getFinalGeneration().stepFrequencies[0]
    step1freqs = experimentStorer.experiments[0].getFinalGeneration().stepFrequencies[1]
    step2freqs = experimentStorer.experiments[0].getFinalGeneration().stepFrequencies[2]
    step3freqs = experimentStorer.experiments[0].getFinalGeneration().stepFrequencies[3]

    width = 0.15

    fig, ax = plt.subplots()
    rects1 = ax.bar(xaxis - 3/2* width, step0freqs, width, color='r', label='RW')
    rects2 = ax.bar(xaxis - 1/2*width, step1freqs, width, color='y', label='SCH')
    rects3 = ax.bar(xaxis + width/2, step2freqs, width, color='g', label='Same')
    rects4 = ax.bar(xaxis + 3/2*width, step3freqs, width, color='b', label='Opposite')

    ax.set_ylabel('Frequency of SCH steps')
    ax.set_xlabel('Step Number')
    ax.set_title('Step Number vs. Frequency of SCH steps')
    ax.set_xticks(xaxis)
    ax.legend()

    # ax2 = plt.subplots(112)
    # ax2.plot(xaxis, experimentStorer.experiments[0].getFinalGeneration().stepFrequencies[0])
    # ax.bar_label(rects1)
    # ax.bar_label(rects2)
    # ax.bar_label(rects3)
    # ax.bar_label(rects4)

    plt.show()
