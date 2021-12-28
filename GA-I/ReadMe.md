# Genetic Algorithm with Single Objective

Our implementation of GA-I in Java takes 9 arguments as inputs. 4 out of 9 inputs are ABI, name of the contract, bytecode and opcode files.We use these four arguments to generate the first random population. 
The remaining 5 arguments are directly related to GA operators – namely, size of the population, minimum desired coverage, number of iterations, mutation rate and 
crossover rate.

# Way to generate Solidity dependent file - ABI, OPCODE, BYTECODE 

solc SAMPLECONTRACT.SOL --abi >> $ABI_FILE

solc SAMPLECONTRACT.SOL --bin >> $BYTECODE_FILE

solc SAMPLECONTRACT.SOL --opcodes >> $OPCODE_FILE


# Way to run using JAR file


java -jar GA_Input_Generator.jar "$ABI_FILE".abi $NAME_OF_THE_CONTRACT "$BYTECODE_FILE".txt POPULATION_SIZE MIN_DESIRED_COVERAGE NUMBER_OF_ITERATION "$PATH_OF_THE_EXECUTION_ENVIROMENT" MUTATION_RATE CROSSOVER_RATE "$OPCODE_FILE".txt "$OUTPUT_FILE"_GA_scenario.json
