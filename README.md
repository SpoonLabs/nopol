# Nopol [![Build Status](https://travis-ci.org/SpoonLabs/nopol.svg?branch=master)](https://travis-ci.org/SpoonLabs/nopol)

This is the repository of Nopol.

Nopol is an automatic software repair tool developed at Inria Lille.

This code is research code, released under the GPL licence.

If you use this code for academic research, please cite:
Automatic Repair of Buggy If Conditions and Missing Preconditions with SMT (Favio DeMarco, Jifeng Xuan, Daniel Le Berre, Martin Monperrus), In Proceedings of the 6th International Workshop on Constraints in Software Testing, Verification, and Analysis (CSTVA 2014)
```Bibtex
@InProceedings{DeMarco2014,
  Title                    = {{Automatic Repair of Buggy If Conditions and Missing Preconditions with {SMT}}},
  Author                   = {Favio DeMarco and Jifeng Xuan and Daniel Le Berre and Martin Monperrus},
  Booktitle                = {Proceedings of the 6th International Workshop on Constraints in Software Testing, Verification, and Analysis (CSTVA 2014)},
  Year                     = {2014}
}
```
A compiled version of Nopol is at nopol-0.0.3-SNAPSHOT-jar-with-dependencies.jar
It requires an SMT solver installed on the machine (e.g. Z3)

## Getting started

To compile using maven: First execute 'mvn clean' and then 'mvn install'

## Usage

```
Usage: java -jar nopol.jar
                          [(-m|--mode) <repair|ranking>] [(-e|--type) <loop|condition|precondition|arithmetic>] [(-o|--oracle) <angelic|symbolic>] [(-y|--synthesis) <smt|brutpol>] [(-l|--solver) <z3|cvc4>] [(-p|--solver-path) <solverPath>] (-s|--source) source1:source2:...:sourceN  (-c|--classpath) <classpath> [(-t|--test) test1:test2:...:testN ] [--complianceLevel <complianceLevel>] [--maxTime <maxTime>]

  [(-m|--mode) <repair|ranking>]
        Define the mode of execution. (default: repair)

  [(-e|--type) <loop|condition|precondition>]
        The type of statement to analyze (only used with repair mode). (default:
        condition)

  [(-o|--oracle) <angelic|symbolic>]
        Define the oracle (only used with repair mode). (default: angelic)

  [(-y|--synthesis) <smt|brutpol>]
        Define the patch synthesis. (default: smt)

  [(-l|--solver) <z3|cvc4>]
        Define the solver (only used with smt synthesis). (default: z3)

  [(-p|--solver-path) <solverPath>]
        Define the solver binary path (only used with smt synthesis).

  (-s|--source) source1:source2:...:sourceN 
        Define the path to the source code of the project.

  (-c|--classpath) <classpath>
        Define the classpath of the project.

  [(-t|--test) test1:test2:...:testN ]
        Define the tests of the project.

  [--complianceLevel <complianceLevel>]
        The compliance level of the project. (default: 7)

  [--maxTime <maxTime>]
        The maximum time execution in minute. (default: 60)
```

## Contact

For questions and feedback , please contact martin.monperrus@univ-lille1.fr

