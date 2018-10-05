import argparse
import glob
import csv
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patheffects as mpe

# Get source csv folder and destination png file from program arguments
parser = argparse.ArgumentParser()
parser.add_argument("src")
parser.add_argument("dst")
parser.add_argument("title")
args = parser.parse_args()

# List all csv files in src folder
csvFileNames = glob.glob(args.src+"/*.csv")

# Load metric names from the header of one of the csv files
metricNames = []
with open(csvFileNames[0], newline='') as csvFile:
	reader = csv.reader(csvFile, delimiter=";")
	metricNames = next(reader)

# Load all csv contents
fileContents = []
for csvFileName in csvFileNames:
	with open(csvFileName, newline='') as csvFile:
		fileContents.append(np.loadtxt(csvFile, delimiter=";", skiprows=1))

# Put all csv contents into matrix
metrics = np.dstack(fileContents)
metricCount = metrics.shape[1]
runCount = metrics.shape[2]

# Calculate means of each metric
means = metrics.mean(axis=2)

# Onto plotting!
colors = ['black', 'tab:red', 'tab:blue', 'tab:green', 'gray'] # more than 4 metrics? fuck you

# Construct axis for each metric
fig, host = plt.subplots()
host.set_zorder(metricCount)
host.patch.set_facecolor('none')
host.set_xlabel(metricNames[0], color=colors[0])
host.set_ylabel(metricNames[1], color=colors[1])
axes = [host]
for i in range(2, metricCount):
	# For each metric other than Fitness, set log scale
	axis = host.twinx()
	axis.set_zorder(metricCount-i)
	axis.patch.set_facecolor('none')
	axis.set_yscale('log')
	axis.set_ylim([np.min(means[:,i]), np.max(means[:,i])])
	axis.set_ylabel(metricNames[i], color=colors[i])
	axis.tick_params(axis='y', labelcolor=colors[i])
	axis.spines["right"].set_position(('outward', 50*(i-2)))
	axes.append(axis)

# Plot vague line for each run first
alpha = min(2.0 / runCount, 1.0)
for i in range(1, 2):
	axis = axes[i-1]
	axis.plot(means[:,0], metrics[:,i,:], color=colors[i], alpha=alpha)

# Plot averages as thicker lines
outline = mpe.withStroke(linewidth=1.0, foreground='black')
for i in reversed(range(1, metricCount)):
	axis = axes[i-1]
	axis.plot(means[:,0], means[:,i], color=colors[i], alpha=1.0, label=metricNames[i], path_effects=[outline])

# Some further layout
plt.title(args.title)
fig.tight_layout()

# Save to file
plt.savefig(args.dst, bbox_inches='tight')