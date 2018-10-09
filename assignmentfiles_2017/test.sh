#!/bin/bash

# File structure at the end
# root
#	/tests
#		/SomeExperimentName
#			/BentCigar
#				metrics_1.csv
#				metrics_2.csv
#				metrics_3.csv
#			/Schaffers
#				metrics_1.csv
#				metrics_2.csv
#				metrics_3.csv
#			/Katsuura
#				metrics_1.csv
#				metrics_2.csv
#				metrics_3.csv
#			SomeExperimentName.BentCigar.png
#			SomeExperimentName.Schaffers.png
#			SomeExperimentName.Katsuura.png
#		/SomeOtherExperimentName
#			/BentCigar
#				metrics_1.csv
#				...
#			...

# Get testName and runCount from parameters
testName="$1"
runCount="$2"
if [ -z "$testName" ] || [ -z "$runCount" ]
then
	echo "usage: test.sh <test_name> <runcount>"
	exit 1
fi

# Run each evaluation runCount times with different seeds
# (parallel doesn't work atm because of javabbob file access shenanigans)
evalNames=( "BentCigarFunction" "SchaffersEvaluation" ) #"KatsuuraEvaluation" )
for evalName in "${evalNames[@]}"
do
	mkdir -p "tests/${testName}/${evalName}"
	echo "running ${evalName}"
	seed=$RANDOM
	for (( i=1; i<=runCount; i++ ))
	do
		((seed++))
		java "${@:3}" -Dcsv -jar testrun.jar -submission=player34 -evaluation="${evalName}" -seed="${seed}" > "tests/${testName}/${evalName}/metrics_${seed}.csv"
	done
	wait
	# Run python script that loads csv files for each evaluation and plots metrics
	python plot.py "tests/${testName}/${evalName}" "tests/${testName}/${testName}.${evalName}.png" "${testName}: ${evalName}"
done

# Open the image files
for evalName in "${evalNames[@]}"
do
	display "tests/${testName}/${testName}.${evalName}.png" &
done

