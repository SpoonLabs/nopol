package fr.inria.lille.commons.synthesis.smt;

import java.util.Map;

import org.smtlib.IResponse;
import org.smtlib.sexpr.ISexpr;
import org.smtlib.sexpr.ISexpr.IToken;
import org.smtlib.sexpr.Sexpr.Seq;

import xxl.java.extensions.collection.MapLibrary;

public class SexprSolutionVisitor {

	public static Map<String, String> solutionsFrom(IResponse response) {
		Map<String, String> solutions = MapLibrary.newHashMap();
		visit(response, solutions);
		return solutions;
	}
	
	private static void visit(IResponse response, Map<String, String> solutionMap) {
		try {
			visit((Seq) response, solutionMap);
		}
		catch (IndexOutOfBoundsException ioobe) {
			ioobe.printStackTrace();
		}
		catch (ClassCastException cce) {
			cce.printStackTrace();
		}
	}
	
	private static void visit(Seq seq, Map<String, String> solutionMap) {
		for (ISexpr expr: seq.sexprs()) {
			Seq nestedSeq = (Seq) expr;
			IToken<?> key = (IToken<?>) nestedSeq.sexprs().get(0);
			ISexpr value = nestedSeq.sexprs().get(1);
			solutionMap.put(key.toString(), value.toString());
		}
	}
	
}
