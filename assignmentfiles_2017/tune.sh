# evalNames=( "BentCigarFunction" "SchaffersEvaluation" )
evalNames=( "SchaffersEvaluation" )

sigmas=(0.1 0.3 0.4 0.5 0.6 0.8 1)
islandNs=(1 2 4 5 10)
epochs=(10 20 30 40 50)
popsizes=(50 100 200 400 500)
parentcounts=(2 5 10 15 20 30)
migrationsizes=(1 2 5 10 15 20)
aritys=(2 5)
recombinationProbabilitys=(0.5 0.7)
ks=(2 5 10)

defaultParameters="-Dparentselection=tournament -Dsurvivorselection=tournament -Drecombination=m-1crossover -Dmutation=adaptivegauss"

# Fitness sharing off
# fitnessBool=false
# for islandN in "${islandNs[@]}"; do
# 	if [[ $islandN -gt 1 ]]; then
# 		for epoch in "${epochs[@]}"; do
# 			runName="$islandN;$epoch;false;0.0"
# 			parameterString="-Dislands=${islandN} -Depochsize=${epoch}"
# 			parameterString="${parameterString} $defaultParameters"
# 			./test.sh $runName 10 $parameterString
# 		done
# 	elif [[ $islandN == 1 ]]; then
# 		runName="$islandN;0;false;0.0"
# 		parameterString="-Dislands=${islandN}"
# 		parameterString="${parameterString} $defaultParameters"
# 		./test.sh $runName 10 $parameterString
# 	fi
# done

# Fitness sharing on
#TODO: Add
#-Dmigrationsize=2
#-Dpopsize=100
#-Dparentcount=10
# -Darity
# -DrecombinationProbability
# -Dk

fitnessBool=true
for islandN in "${islandNs[@]}"; do
	if [[ $islandN -gt 1 ]]; then
		for epoch in "${epochs[@]}"; do
			for sigma in "${sigmas[@]}"; do
				for migrationsize in "${migrationsizes[@]}"; do
					for popsize in "${popsizes[@]}"; do
						for parentcount in "${parentcounts[@]}"; do
							for arity in "${aritys[@]}"; do
								for recombinationProbability in "${recombinationProbabilitys[@]}"; do
									for k in "${ks[@]}"; do
										runName="$islandN;$epoch;true;${sigma};$migrationsize;$popsize;$parentcount;$arity;${recombinationProbability};$k"
										parameterString="-Dislands=${islandN} -Depochsize=${epoch} -Dsharefitness -Dsigma=${sigma} -Dmigrationsize=${migrationsize} -Dpopsize=${popsize} -Dparentcount=${parentcount} -Darity=${arity} -DrecombinationProbability=${recombinationProbability} -Dk=${k}"
										parameterString="${parameterString} $defaultParameters"
										./test.sh $runName 10 $parameterString
									done
								done
							done
						done
					done
				done
			done
		done
	elif [[ $islandN == 1 ]]; then
		for sigma in "${sigmas[@]}"; do
			for popsize in "${popsizes[@]}"; do
				for parentcount in "${parentcounts[@]}"; do
					for arity in "${aritys[@]}"; do
						for recombinationProbability in "${recombinationProbabilitys[@]}"; do
							for k in "${ks[@]}"; do
								runName="$islandN;0;true;${sigma};$popsize;$parentcount;$arity;$recombinationProbability;$k"
								parameterString="-Dislands=${islandN} -Depochsize=${epoch} -Dsharefitness -Dsigma=${sigma} -Dpopsize=${popsize} -Dparentcount=${parentcount} -Darity=${arity} -DrecombinationProbability=${recombinationProbability} -Dk=${k}"
								parameterString="${parameterString} $defaultParameters"
								./test.sh $runName 10 $parameterString
							done
						done
					done
				done
			done
		done
	fi
done
