evalNames=( "BentCigarFunction" "SchaffersEvaluation" )

sigmas=(0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9)
islandNs=(1 2 4 5)
epochs=(10 20 30 40 50)

defaultParameters="-Dpopsize=100 -Dparentcount=10 -Dparentselection=tournament -Dmigrationsize=2 -Dsurvivorselection=tournament -Drecombination=m-1crossover -Dmutation=adaptivegauss"

# Fitness sharing off
fitnessBool=false
for islandN in "${islandNs[@]}"; do
	if [[ $islandN -gt 1 ]]; then
		for epoch in "${epochs[@]}"; do
			runName="$islandN;$epoch;false;0.0"
			parameterString="-Dislands=${islandN} -Depochsize=${epoch}"
			parameterString="${parameterString} $defaultParameters"
			./test.sh $runName 10 $parameterString
		done
	elif [[ $islandN == 1 ]]; then
		runName="$islandN;0;false;0.0"
		parameterString="-Dislands=${islandN}"
		parameterString="${parameterString} $defaultParameters"
		./test.sh $runName 10 $parameterString
	fi
done

# Fitness sharing on
fitnessBool=true
for islandN in "${islandNs[@]}"; do
	if [[ $islandN -gt 1 ]]; then
		for epoch in "${epochs[@]}"; do
			for sigma in "${sigmas[@]}"; do
				runName="$islandN;$epoch;true;${sigma}"
				parameterString="-Dislands=${islandN} -Depochsize=${epoch} -Dsharefitness -Dsigma=${sigma}"
				parameterString="${parameterString} $defaultParameters"
				./test.sh $runName 10 $parameterString
			done
		done
	elif [[ $islandN == 1 ]]; then
		for sigma in "${sigmas[@]}"; do
			runName="$islandN;0;true;${sigma}"
			parameterString="-Dislands=${islandN} -Depochsize=${epoch} -Dsharefitness -Dsigma=${sigma}"
			parameterString="${parameterString} $defaultParameters"
			./test.sh $runName 10 $parameterString
		done
	fi
done
