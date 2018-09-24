import argparse
import glob
import csv
import numpy as np
import matplotlib.pyplot as plt

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

# Calculate means of each metric
means = metrics.mean(axis=2)

# Onto plotting!
colors = ['black', 'tab:red', 'tab:blue', 'tab:green', 'gray'] # more than 4 metrics? fuck you

# Plot each metric
plots = []
fig, host = plt.subplots()
host.set_xlabel(metricNames[0], color=colors[0])
axis = host
for i in range(1, metrics.shape[1]):
	# axis.spines['top'].set_visible(False)
	axis.set_ylabel(metricNames[i], color=colors[i])
	axis.tick_params(axis='y', labelcolor=colors[i])
	axis.plot(means[:,0], metrics[:,i,:], color=colors[i], alpha=0.1)
	p, = axis.plot(means[:,0], means[:,i], color=colors[i], alpha=1.0, label=metricNames[i])
	plots.append(p)
	if i < metrics.shape[1]-1:
		axis = host.twinx()
		axis.spines["right"].set_position(('outward', 50*(i-1)))

# Some further layout
plt.title(args.title)
#plt.legend(plots, [p.get_label() for p in plots])
fig.tight_layout()

# Save to file
plt.savefig(args.dst, bbox_inches='tight')