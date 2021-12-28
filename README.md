# Testing Smart Contracts: Which Technique Performs Best?

We implemented four different testing techniques to assses the effectiveness of the test generation techniques for Solidity smart contracts. These techniques are Blackbox fuzzing, Greybox fuzzing with an SMT solver, Genetic algorithm with a single objective, and Genetic algorithm with multiple objectives.

Implementation of the each technique can be found under the main folder. BF stands for Blackbox fuzzing. GF stands for Greybox-fuzzing with an SMT solver. GA-I stands for Genetic Algorithm with a single objective, AF stands for Adaptive Fuzzing.

Implementation of the techniques are publicly available. In addition, the way to run the techniques is explained in a ReadMe file.

PS: We used two different dataset to compare the effectiveness of the techniques. The datasets are also publicly available under DATASET folder.


This repository created for our research paper " Testing Smart Contracts: Which Technique Performs Best?" that is published on 15th ACM/IEEE International Symposium on Empirical Software Engineering and Measurement (ESEM) 2021.

Paper is available in the link below: 

https://github.com/sefaakca/Publications

# Cite Our Work

If you use our research for your research, please kindly cite our work in your paper.

```bibtex
@inproceedings{akca2021testing,
  title={Testing Smart Contracts: Which Technique Performs Best?},
  author={Akca, Sefa and Peng, Chao and Rajan, Ajitha},
  booktitle={Proceedings of the 15th ACM/IEEE International Symposium on Empirical Software Engineering and Measurement (ESEM)},
  pages={1--11},
  year={2021}
}

```

> Akca, S., Peng, C. and Rajan, A., 2021, October. Testing Smart Contracts: Which Technique Performs Best?. In Proceedings of the 15th ACM/IEEE International Symposium on Empirical Software Engineering and Measurement (ESEM) (pp. 1-11).

# Dependencies of the project

solidity compiler - this project tested under two different solidity compiler version 0.4.25 && 0.5.3

java-maven

nodejs–v8.11.3

SMT solver
