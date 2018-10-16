import numpy as np
import pandas as pd
import os
from collections import defaultdict
from matplotlib import pyplot as plt
from matplotlib.ticker import FormatStrFormatter
import seaborn as sns
from scipy.stats import normaltest
from scipy.stats import ttest_ind
from nested_dict import nested_dict



# Tests that we are comparing
#evaluation_functions = ['BentCigarFunction', 'KatsuuraEvaluation', 'SchaffersEvaluation']
#comparing_test = ['WithFitnessSharing', 'WithoutFitnessSharing']


def find_csv_filenames(path_to_dir, suffix = ".csv"):
    filenames = os.listdir(path_to_dir)
    return [ filename for filename in filenames if filename.endswith(suffix) ]


def read_simulations_data(simulations_path, interesting_column):
	if interesting_column == 'fitness':
		interesting_index = 1
	elif interesting_column == 'diversity':
		interesting_index = 2
	elif interesting_column == 'mutation':
		interesting_index = 3

	simulations_datasets = find_csv_filenames(simulations_path)
	number_of_evaluations = pd.read_csv(os.path.join(simulations_path, simulations_datasets[0]), sep = ';').shape[0]
	output_matrix = np.empty((number_of_evaluations ,len(simulations_datasets)))
	for idx, simulations_data in enumerate(simulations_datasets):
		data = pd.read_csv(os.path.join(simulations_path, simulations_data), sep = ';')
		output_matrix[:, idx] = data.values[:, interesting_index]
	
	return output_matrix 


def find_percent_index(simulations_data, max_per_simulation, convergence_criteria = 0.90):
	number_of_simulations = simulations_data.shape[1]
	percent_90_max = max_per_simulation * convergence_criteria
	difference = simulations_data - percent_90_max

	percent_iterationNumber = np.empty(number_of_simulations)
	for simulation_number in range(number_of_simulations):
		percent_iterationNumber[simulation_number] = np.argmax(difference[:,simulation_number] > 0)

	return percent_iterationNumber


def read_comparison_data(comparing_tests, evaluation_function, metric):
	tests_data = defaultdict(dict)
	for idx, comparing_test in enumerate(comparing_tests):
		simulations_path = os.path.join('../', comparing_test, evaluation_function)
		tests_data[comparing_test]['simulations_data'] = read_simulations_data(simulations_path, metric)
		tests_data[comparing_test]['mean_per_timeStep'] = np.mean(tests_data[comparing_test]['simulations_data'], axis = 1)
		tests_data[comparing_test]['std_per_timeStep']  = np.std(tests_data[comparing_test]['simulations_data'], axis = 1)
		tests_data[comparing_test]['max_per_simulation'] = np.amax(tests_data[comparing_test]['simulations_data'], axis = 0)
		tests_data[comparing_test]['90percent_iterationNumber'] = find_percent_index(tests_data[comparing_test]['simulations_data']
																					, tests_data[comparing_test]['max_per_simulation'])

	return tests_data


def normality_tests(comparison_data, comparing_tests, plot = False, alpha = 0.05):
	tests_data = nested_dict()
	for indicator in ['max_per_simulation', '90percent_iterationNumber']:
		if plot:
			fig = plt.figure()

		for idx, comparing_test in enumerate(comparing_tests):
			isNormallyDistributed = True

			#Normality test
			stat, p_value = normaltest(comparison_data[comparing_test][indicator])
			if p_value < alpha:
				isNormallyDistributed = False
			tests_data[comparing_test][indicator]['isNormallyDistributed'] = isNormallyDistributed

			if plot:
				#Plot histogram
				ax = plt.subplot(1,2,idx + 1)
				ax.xaxis.set_major_formatter(FormatStrFormatter('%.2f'))
				ax.hist(comparison_data[comparing_test][indicator])
				ax.set_title('{}, with p-value {:.2}'.format(comparing_test, p_value))

		if plot:
			plt.suptitle(indicator)
			plt.show()

	return tests_data


def t_tests(comparison_data, comparing_tests, alpha = 0.05):
	statistics_tests = normality_tests(comparison_data, comparing_tests)

	for indicator in ['max_per_simulation', '90percent_iterationNumber']:
		comparison_data_1 = comparison_data[comparing_tests[0]][indicator]
		comparison_data_2 = comparison_data[comparing_tests[1]][indicator]

		statistics_tests[comparing_tests[0]][indicator]['mean'] = np.mean(comparison_data[comparing_tests[0]][indicator])
		statistics_tests[comparing_tests[1]][indicator]['mean'] = np.mean(comparison_data[comparing_tests[1]][indicator])
		
		statistics_tests[comparing_tests[0]][indicator]['std'] = np.std(comparison_data[comparing_tests[0]][indicator])
		statistics_tests[comparing_tests[1]][indicator]['std'] = np.std(comparison_data[comparing_tests[1]][indicator])

		t_statistic, p_value = ttest_ind(comparison_data_1, comparison_data_2, equal_var = False)

		has_equal_means = True
		if p_value < alpha:
			has_equal_means = False
		statistics_tests['Results'][indicator]['t-statistic'] = t_statistic
		statistics_tests['Results'][indicator]['p-value'] = p_value
		statistics_tests['Results'][indicator]['SameMeans'] = has_equal_means

	return statistics_tests


def generate_folders(metric, evaluation_function, comparing_tests):
	# Create main folder where all results will be stored
	separator = '_vs_'
	main_path = separator.join(comparing_tests)
	if not os.path.exists(main_path):
		os.makedirs(main_path)

	# Create folders for the evaluation funtion 
	if not os.path.exists(os.path.join(main_path, evaluation_function)):
			os.makedirs(os.path.join(main_path, evaluation_function))

	# Create folders for the metric 
	if not os.path.exists(os.path.join(main_path, evaluation_function, metric)):
			os.makedirs(os.path.join(main_path, evaluation_function, metric))

	# Create a folder for each comparing test and the final results
	for subpath in [comparing_tests[0], comparing_tests[1], 'Results']:
		if not os.path.exists(os.path.join(main_path, evaluation_function, metric, subpath)):
			os.makedirs(os.path.join(main_path, evaluation_function, metric, subpath))

	return main_path


def output_statistics(metric, evaluation_function, statistics_tests, comparing_tests):
	# Create folders
	main_path = generate_folders(metric, evaluation_function, comparing_tests)

	# Create statistics tables for each comparing test
	for comparing_test in comparing_tests:
		relevant_statistics_per_comparison = {'Metric': ['max_per_simulation', '90percent_iterationNumber']
												,'Mean': [statistics_tests[comparing_test]['max_per_simulation']['mean']
														,statistics_tests[comparing_test]['90percent_iterationNumber']['mean']]
												,'Std': [statistics_tests[comparing_test]['max_per_simulation']['std']
														,statistics_tests[comparing_test]['90percent_iterationNumber']['std']]
												, 'IsNormallyDistributed': [statistics_tests[comparing_test]['max_per_simulation']['isNormallyDistributed']
																		   ,statistics_tests[comparing_test]['90percent_iterationNumber']['isNormallyDistributed']]
											}
		# Output dataframe
		temp_statistics_df = pd.DataFrame(relevant_statistics_per_comparison)
		temp_statistics_df.to_csv(os.path.join(main_path, evaluation_function, metric, comparing_test, 'statistics.csv'))

	# Create results dataframe
	t_test_results = {'Metric': ['max_per_simulation', '90percent_iterationNumber'], 
						'P_value': [statistics_tests['Results']['max_per_simulation']['p-value'],
									statistics_tests['Results']['90percent_iterationNumber']['p-value']],
						'SameMeans': [statistics_tests['Results']['max_per_simulation']['SameMeans'],
									statistics_tests['Results']['90percent_iterationNumber']['SameMeans']]}
	#Output dataframe
	temp_statistics_df = pd.DataFrame(t_test_results)
	temp_statistics_df.to_csv(os.path.join(main_path, evaluation_function, metric, 'Results', 't_test.csv'))

	return main_path


def make_boxplot(metric, main_path, evaluation_function, comparison_data, comparing_tests):
	for indicator in ['max_per_simulation', '90percent_iterationNumber']:
		#Create dataset
		data_test1 = comparison_data[comparing_tests[0]][indicator]
		data_test2 = comparison_data[comparing_tests[1]][indicator]
		names = [comparing_tests[0]]*data_test1.shape[0] + [comparing_tests[1]]*data_test2.shape[0]
		values_data = np.concatenate([data_test1, data_test2])
		dataset = pd.DataFrame({indicator:values_data, 'Comparing_test':names})

		#Plot
		sns.boxplot(x = indicator, y = 'Comparing_test', data = dataset)
		plt.savefig(os.path.join(main_path, evaluation_function, metric, 'Results' ,'{}_boxplot.png'.format(indicator))
			,bbox_inches='tight')
			
		
def runStatisticalTests(comparing_tests, evaluation_function, metric):
	#Read Data
	comparison_data = read_comparison_data(comparing_tests, evaluation_function, metric)
	#Perform tests
	statistics_tests = t_tests(comparison_data, comparing_tests)
	#Output results
	main_path = output_statistics(metric, evaluation_function, statistics_tests, comparing_tests)
	make_boxplot(metric, main_path, evaluation_function, comparison_data, comparing_tests)



comparing_tests = ['WithFitnessSharing', 'WithoutFitnessSharing']
evaluation_functions = ['BentCigarFunction', 'KatsuuraEvaluation', 'SchaffersEvaluation']
metrics = ['fitness', 'diversity']

for evaluation_function in evaluation_functions:
	for metric in metrics:
		runStatisticalTests(comparing_tests, evaluation_function, metric)




