package fr.inria.lille.commons.synthesis;

import static java.util.Arrays.asList;

import java.util.List;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.synthesis.theory.EmptyTheory;
import fr.inria.lille.commons.synthesis.theory.IfThenElseTheory;
import fr.inria.lille.commons.synthesis.theory.LinearTheory;
import fr.inria.lille.commons.synthesis.theory.LogicTheory;
import fr.inria.lille.commons.synthesis.theory.NonlinearTheory;
import fr.inria.lille.commons.synthesis.theory.NumberComparisonTheory;
import fr.inria.lille.commons.synthesis.theory.OperatorTheory;

public class SynthesisTheoriesBuilder {

	public static List<OperatorTheory> theoriesForConstraintBasedSynthesis() {
		List<OperatorTheory> theories = ListLibrary.newArrayList();
		EmptyTheory empty = new EmptyTheory();
		NumberComparisonTheory comparison = new NumberComparisonTheory();
		LogicTheory logic = new LogicTheory();
		LinearTheory linear = new LinearTheory();
		IfThenElseTheory ifThenElse = new IfThenElseTheory();
		NonlinearTheory nonlinear = new NonlinearTheory();
		theories.addAll(asList(empty, comparison, logic, linear, comparison, ifThenElse, nonlinear, logic, linear, ifThenElse, nonlinear));
		return theories;
	}
}
