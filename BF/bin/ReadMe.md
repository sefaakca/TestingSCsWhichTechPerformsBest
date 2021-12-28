# Blackbox fuzzing technique 

BF implementation takes two arguments as inputs – the Application Binary Interface (ABI), and a bytecode file. These are generated from the Solidity contract with
the Solidity compiler.  Our implementation supports all Solidity types such as signed/unsigned integers types with widths ranging from 8 to 256 bits. The generated
inputs call each of the functions in the smart contract and are written into a JSON file.

# Way to generate Solidity dependent file - ABI, OPCODE, BYTECODE 

solc SAMPLECONTRACT.SOL --abi >> $ABI_FILE

solc SAMPLECONTRACT.SOL --bin >> $BYTECODE_FILE

solc SAMPLECONTRACT.SOL --opcodes >> $OPCODE_FILE



# Way to run using a Jar file
java -jar "Name of the Jar file" "Name of the ABI file".abi  "Name of the Bytecode file".txt "Name of the output file name".json


