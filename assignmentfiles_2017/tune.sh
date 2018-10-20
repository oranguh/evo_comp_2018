
# # baseline parameters
# popsizes=(50 100 200 400)
# parentcounts=(2 3 4 6 12 24)
# aritys=(2 3 4 6 12 24)
# recombinationProbabilitys=(0.80 0.95 0.99 1.0)
# ks=(2 3 6 12 24)

# # Model extension parameters
# # disabled for now
# sigmas=(0)
# islandNs=(1)
# epochs=(99999)
# migrationsizes=(2)

# baseline parameters
popsizes=(50 100 200 400)
parentcounts=(24)
aritys=(3)
recombinationProbabilitys=(1.0)
recombinationProbabilitys=(0.99)
ks=(2)

# Model extension parameters
sigmas=(0.2 0.4 0.7 1.0)
sigmas=(0.0003 0.001 0.003 0.01)
islandNs=(1 2 4 6 12)
epochs=(5 15 45)
migrationsizes=(2)

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


for popsize in "${popsizes[@]}"; do
	for parentcount in "${parentcounts[@]}"; do
		for arity in "${aritys[@]}"; do
			for k in "${ks[@]}"; do
				for recombinationProbability in "${recombinationProbabilitys[@]}"; do
					for sigma in "${sigmas[@]}"; do
						for islandN in "${islandNs[@]}"; do
							if [[ $islandN -gt 1 ]]; then
								for epoch in "${epochs[@]}"; do
									for migrationsize in "${migrationsizes[@]}"; do
										runName="$islandN;$epoch;true;${sigma};$migrationsize;$popsize;$parentcount;$arity;${recombinationProbability};$k"
										parameterString="-Dpopsize=${popsize} -Dparentcount=${parentcount} -Darity=${arity} -DrecombinationProbability=${recombinationProbability} -Dk=${k} -Dsharefitness -Dsigma=${sigma} -Dislands=${islandN} -Depochsize=${epoch} -Dmigrationsize=${migrationsize} "
										parameterString="${parameterString} $defaultParameters"
										./test.sh $runName 10 $parameterString
									done
								done
							elif [[ $islandN == 1 ]]; then
								runName="$islandN;0;true;${sigma};0;$popsize;$parentcount;$arity;$recombinationProbability;$k"
								parameterString="-Dpopsize=${popsize} -Dparentcount=${parentcount} -Darity=${arity} -DrecombinationProbability=${recombinationProbability} -Dk=${k} -Dsharefitness -Dsigma=${sigma} -Dislands=${islandN}"
								parameterString="${parameterString} $defaultParameters"
								./test.sh $runName 10 $parameterString
							fi
						done
					done
				done
			done
		done
	done
done
