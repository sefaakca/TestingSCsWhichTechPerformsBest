# Pre-requisite:
1. Python 3 and the pip package manager
2. z3 solver

   z3 solver can be install by 
   ```bash
   pip install z3-solver
   ```

# Usage

Simply supply the constraints extracted from the Solidity code and run

```
python solve.py constraints.txt
```

The output shall be placed in the corresponding part of the JSON input file to the EVM.