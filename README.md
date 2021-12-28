# empirical_evaluation_for_sc

We implemented four different testing techniques to assses the effectiveness of the test generation techniques for Solidity smart contracts. These techniques are Blackbox fuzzing, Greybox fuzzing with an SMT solver, Genetic algorithm with a single objective, and Genetic algorithm with multiple objectives.

Implementation of the each technique can be found under the main folder. BF stands for Blackbox fuzzing. GF stands for Greybox-fuzzing with an SMT solver. GA-I stands for Genetic Algorithm with a single objective, AF stands for Adaptive Fuzzing.

Implementation of the techniques are publicly available. In addition, the way to run the techniques is explained in a ReadMe file.

PS: We used two different dataset to compare the effectiveness of the techniques. The datasets are also publicly available under DATASET folder.

# Dependencies of the project

solidity compiler - this project tested under two different solidity compiler version 0.4.25 && 0.5.3

java-maven

nodejs–v8.11.3

SMT solver
