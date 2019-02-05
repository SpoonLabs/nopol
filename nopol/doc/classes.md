## NoPol
In charge of finding the repair. The general process is the following:

1. From a class path of an Eclipse Target Project (ETP), it only keeps the test classes.

2. For each test class, it uses `GZoltar` to run each test. After executing all tests, it obtains a list of statements ordered by their suspiciousness.

3. For each statement, it tries to generate a repair using `SynthesizerFactory`.

  3.1 `BugKindDetector` dictates whether the repair is for a predicate inside an `if/then/else` statement or to create a precondition.

  3.2 A `DelegatingProcessor` is built according to the `BugKind`. This processor has children processors, among which there are the `ConditionalLoggingInstrumenter` and processors which set angelic values (`ConditionalAdder` and `ConditionalReplacer`).

  3.3 `ConstraintModelBuilder` loads the different tests classes from the ETP. Doing so, will trigger the compilation of classes and the execution of the processors (`SpoonClassLoader.processJavaFile`).

      [3.3.1] The `ConditionalLoggingInstrumenter` injects the code needed to obtain input-output values into the ETP source files.

      [3.3.2] Modified classes are recompiled.

      [3.3.3] Test classes are executed on the modified code. Then the angelic value is flipped, and another execution is performed.

      [3.3.4] `DefaultSynthesiser` obtains input-output values.

  3.4 If the statement is indeed an angelic fix localization, `DefaultSynthesiser` iterates through increasing levels of components complexity to find a repair. For each level, it creates a suitable model which is solved by `ConstraintSolver` (which ultimately uses the `SMTLIB`). If there exists a solution, it is decoded as a string repair.

4. After the `SynthesizerFactory` returns a repair, it checks that the repair is indeed _correct_ (when the `TestPatch` with the new repair now passes all tests). If so, then `NoPol` ends the execution with that repair. Otherwise, it keeps analysing another statement.


## GZoltarSuspiciousProgramStatements
This class uses the `GZoltar` library (actually, a `GZoltarJava7` object which -just for compatibility issues- wraps a `GZoltar` object). Its goal is to obtain suspicious statements, which may be responsible for failures in the tests cases. Each statement is represented as a `GZoltarStatement` object which wraps a `Statement` object (from the original `GZoltar` library).

All information from test executions (whether they passed or not and the statements covered during execution) is recorded and summarised in order to calculate suspiciousness of each statement. The final output is an ordered list of statements in descending order of their suspiciousness.


## SynthesizerFactory
This class has the responsibility to build a patch synthesiser, depending on what the faulty statement problem is. It uses a `BugKindDetector` to classify the kind of problem and then it builds a `DefaultSynthesiser` with the corresponding `ConstraintModelBuilder`.

For a conditional problem, the `ConstraintModelBuilder` is built with a `DelegatingProcessor`, a `ConditionalReplacer` and a `ConditionalLoggingInstrumenter`. On the other hand, for a precondition problem a `DelegatingProcessor`, a `ConditionalLoggingInstrumenter` and a `ConditionalAdder` are used.


## BugKindDetector
It extends the class `AbstractProcessor` from `Spoon` Library. Its goal is to analyse what is the underlying problem of a faulty statement: _conditional_, _precondition_ or none. A conditional problem is found when the suspicious statement consist of an `"if/then/else"` or `"(predicate)? branch_1 : branch_2"` statement. A precondition problem is found when the suspicious statement may need a precondition before its execution.

**Performance overhead:** given that it extends an `AbstractProcessor` it traverses the whole AST of the project, although the constructor of the `BugKindDetector` receives the file and line number it has to analyse. So, the Visitor Pattern of Spoon will keep searching throughout all the elements in the ETP discarding unwanted `CtElement`'s with the call to `BugKindDetector.isToBeProcessed` until it reaches the `CtElement` corresponding to that file and line number.


## DelegatingProcessor
This is just a compound processor. That is to say that the way it processes a `CtElement` is by iterating through all its "child" processors and let them process it one at a time. However, his children only process a `CtElement` when it is the _correct_ one: the element is related to the correct problem (conditional or precondition, depending on how was the `DelegatingProcessor` built by the `SynthesizerFactory`), it belongs to the correct file and in the correct line number. (Of course, it has the same performance overhead as mention above).


## ConditionalReplacer
This processor should only be used when dealing with statement that have a conditional problem. The only thing it does is setting the condition to `true` or `false` (angelic value). It does so using a code snippet with Spoon to transform the corresponding `CtExpression<Boolean>`.

The code snippet refers to a static variable `ConditionalValueHolder.VARIABLE_NAME` which, of course, can be changed dynamically. Therefore, the modified file with the attached code snippet needs only to be recompiled once (after the insertion of the code snippet). If the code snippet didn't refer to a static value and instead had the hardcoded value `true` or `false`, then one would need to recompile the code each time he wants to change the angelic value. This is because Spoon lets the programmer modify the AST of `*.java` files, but the JVM loads compiled `*.class` files.


## ConditionalAdder
This processor receives a statement that may need a precondition before its execution and it wraps it inside a `CtIf` element (`if/then` statement). The precondition value is set to `true` or `false` (angelic value). Again, this instrumentation of the code is achieved using Spoon.


## Level
This is just an `enum` class in order to distinguish different levels of complexity for the components that will be used for the program synthesis. For example, `Constants`, `Comparison`, `Logic`, `Arithmetic`.


## ValuesCollector
Static class, it represents a `Map<String,Object>` dictionary. The key is a code snippet and the object is the result of the code snippet. The main idea is to be able to store code snippets that can be used inside predicates for the `if/then/else` condition. It can store code snippets like `"true"`, `"variable != null"` but also (with the help of the `SubValuesCollector`) `"anArray.length"`, `"anIterator.hasNext()"`, and other well-known methods of Java collections.


## ResultMatrixBuilderListener
This class makes it possible to relate the collected values by `ValuesCollector` with the angelic value. In short, it is possible to relate object information (variable values, collection size, null comparison, etc) with the angelic value. Therefore, it represents a constraint on the repair statement: "given a program state with certain information, one must formulate a predicate which evaluates to the angelic value."


## ConditionalLoggingInstrumenter
This class' main purpose is to analyse a `CtElement` and traverse its scope (from its location and upwards) to find all accessible initialised variables whose values could be used in `if/then/else` predicates. For each found variable, `ValuesCollector` stores its values. The code for that method calls is generated dynamically for each suspicious statement analysis.


## ConstraintModelBuilder
This class is responsible for collecting results after the angelic value has been set. Up to this point, `NoPol` is trying to repair one statement and one angelic value has been set. This class executes the test classes of the ETP, then performs another run with another angelic value (changing `true` to `false` in the new statement repair), and returns the input-output values.


## ConstraintSolver
This class solves and synthesises a repair for the given input-output values. It does so using a script to be executed with the `SMTLIB` binary, delegating the script generation to a `Synthesis` object. If a solution is found, the `RepairCandidateBuilder` translates the solution to a readable string repair.


## Synthesis
Used to generate a `SMTLIB` solvable script, which is defined by different constraints.


## DefaultSynthesiser
This class is responsible for producing the repair for the source code. After obtaining the input-output values from `ConstraintModelBuilder`, it tries to produce a repair statement that can adjust to those values. It does so in an increasing order of complexity of components used to synthesise the code (starting from level `CONSTANTS` up to level `MULTIPLICATION`). For each level, the `ConstraintSolver` tries to find a new repair with the input-output values.