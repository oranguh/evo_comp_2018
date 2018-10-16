# testName="$1"
# runCount="$2"
# if [ -z "$testName" ] || [ -z "$runCount" ]
# then
# 	echo "usage: test.sh <test_name> <runcount>"
# 	exit 1
# fi

evalNames=( "BentCigarFunction" "SchaffersEvaluation" )

sigmas=(0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9)
islandNs=(0 1 2)
epochs=(10 20 30 40 50)
fitnessSharingBools=(false true)

for fitnessBool in "${fitnessSharingBools[@]}"; do
	for islandN in "${islandNs[@]}"; do
		for epoch in "${epochs[@]}"; do
			for sigma in "${sigmas[@]}"; do
				runName="$fitnessBool;$islandN;$epoch;$sigma"

				# # Placeholder sigma/epoch values aren't actually used if
				# # !fitnessSharing or islandN > 1. Checking whether epoch==10
				# # is so that it runs once
				if [[ islandN == 0 ]] && [[ !epoch==10 ]]; then
					continue
				elif [[ islandN -gt 0 ]]; then
					echo $runName
				fi <<< "$runName"
				if [[ !fitnessBool ]] && [[ !sigma==0.1 ]]; then
					continue
				elif [ fitnessBool ]; then
					runName="$runName;$sigma"
					echo $runName
				fi <<< "$runName"

				echo $runName
				#./test.sh $runName 10 islandN epoch fitnessSharingBool sigma
			done
		done
	done
done
