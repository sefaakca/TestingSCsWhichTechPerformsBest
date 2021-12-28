#!/usr/bin/env python3
import os
import sys
from z3 import *
from typing import Set, List, Any

OPERATORS: Set[str] = {
    '+',
    '-',
    '*',
    '/',
    '(',
    ')'
    "!",
    "*",
    "/",
    "%",
    "+",
    "-",
    "<<",
    ">>",
    "<",
    ">",
    ">=",
    "<=",
    "==",
    "!=",
    "&",
    "^",
    "|",
    "&&",
    "||",
}

#PRIORITY = {'+': 1, '-': 1, '*': 2, '/': 2}
PRIORITY= {
    "!": -10,
    "*": -20,
    "/": -20,
    "%": -20,
    "+": -30,
    "-": -30,
    "<<": -40,
    ">>": -40,
    "<": -50,
    ">": -50,
    ">=": -50,
    "<=": -50,
    "==": -60,
    "!=": -60,
    "&": -70,
    "^": -80,
    "|": -90,
    "&&": -100,
    "||": -110,
}


def infix_to_prefix(formula):
    op_stack = []
    exp_stack: List[Any] = []
    for ch in formula:
        if not ch in OPERATORS:
            exp_stack.append(ch)
        elif ch == '(':
            op_stack.append(ch)
        elif ch == ')':
            while op_stack[-1] != '(':
                op = op_stack.pop()
                a = exp_stack.pop()
                b = exp_stack.pop()
                exp_stack.append(op + " " + b + " " + a)
            op_stack.pop()  # pop '('
        else:
            while op_stack and op_stack[-1] != '(' and PRIORITY[ch] <= PRIORITY[op_stack[-1]]:
                op = op_stack.pop()
                a = exp_stack.pop()
                b = exp_stack.pop()
                exp_stack.append(op + " " + b + " " + a)
            op_stack.append(ch)

    # leftover
    while op_stack:
        op = op_stack.pop()
        a = exp_stack.pop()
        b = exp_stack.pop()
        exp_stack.append(op + " " + b + " " + a)
    print(exp_stack[-1])
    return exp_stack[-1]


def handle_scalar_variable_definition(data_type, var_name):
    result = ""
    if "bool" == data_type:
        result = var_name + " = Bool('" + var_name + "')\n"
        return result

    if "int" in data_type:
        data_size = "256"
        if "uint" in data_type:
            if len(data_type) > 4:
                data_size = data_type[4:]
        else:
            if len(data_type) > 3:
                data_size = data_type[3:]
        # I'm using bit vector for all signed and unsigned integers so that we don't need add extra constraints for
        # data range.
        result = var_name + " = BitVec('" + var_name + "', " + data_size + ")\n"
        return result
    if "address" in data_type:
        result = var_name + " = BitVec('" + var_name + "', 160)\n"
        return result


def main():
    if len(sys.argv) != 2:
        print("Please provide the constraint file")
        exit(-1)
    constraint_filename = sys.argv[1]
    if not os.path.exists(constraint_filename):
        print("Contraint file not exists")
        exit(-1)
    constraint_file = open(constraint_filename)
    constraint_program = ""
    constraint_program_definitions = {}
    constraint_program_conditions = []
    parameters = []
    for line in constraint_file:
        current_line = line
        if current_line[-1] == '\n':
            current_line = current_line[:-1]
        if len(current_line) < 3:
            continue
        line_components = current_line.strip().split(" ")
        if len(line_components) < 2:
            continue
        if line_components[0] == "PARAM":
            parameters.append(line_components[3])
            if line_components[1] == "SCALAR":
                constraint_program_definitions[line_components[3]] = \
                                     handle_scalar_variable_definition(line_components[2], line_components[3])
                if "=" in current_line:
                    initial_value = current_line[current_line.find("=") + 1:]
                    for j in initial_value.strip().split(" "):
                        if j not in constraint_program_conditions:
                            constraint_program_conditions.append(j)
                    constraint_program = constraint_program + "s.add(" + line_components[3] + " == " + initial_value +\
                        ")\n"
        elif line_components[0] == "DEF":
            if line_components[1] == "SCALAR":
                constraint_program_definitions[line_components[3]] = \
                    handle_scalar_variable_definition(line_components[2], line_components[3])
                if "=" in current_line:
                    initial_value = current_line[current_line.find("=") + 1:]
                    for j in initial_value.strip().split(" "):
                        if j not in constraint_program_conditions and j not in OPERATORS:
                            constraint_program_conditions.append(j)
                    constraint_program = constraint_program + "s.add(" + line_components[3] + " == " + initial_value + \
                                         ")\n"
        elif line_components[0] == "CON":
            lower_current_line = current_line.lower()
            if "approve" in lower_current_line or "div" in lower_current_line or "sub" in lower_current_line or \
                "mul" in lower_current_line or "add" in lower_current_line or "mod" in lower_current_line or \
                "timestamp" in lower_current_line:
                exit(0)
            else:
                current_conditions = current_line[4:]
                for j in current_conditions.strip().split(" "):
                    if j not in constraint_program_conditions and j not in OPERATORS:
                        constraint_program_conditions.append(j)
                constraint_program = constraint_program + "s.add(" + current_conditions + ")\n"

    for constraint_program_definition in constraint_program_definitions:
        if constraint_program_definition in constraint_program_conditions:
            constraint_program = constraint_program_definitions[constraint_program_definition] +\
                                 constraint_program
    print(constraint_program_conditions)
    print()
    print(constraint_program_definitions)
    print()
    print(constraint_program)
    constraint_file.close()
    s = Solver()
    exec(constraint_program)
    if s.check() == sat:
        print("model found.")
        model = s.model()
        for var in model:
            print(str(var) + ": " + str(model[var]))
        result = '"parameters": [\n'
        result_vars = {}
        for var in model:
            if str(var) in parameters:
                result_vars[str(var)] = str(model[var])
        for param in parameters:
            result = result + "  " + result_vars[param] + ",\n"
        result = result + "]"
        result_file = open(constraint_filename + ".result", "w")
        result_file.write(result)
        result_file.close()

    else:
        print("model not found.")


if __name__ == "__main__":
    main()
