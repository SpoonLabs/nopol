package fr.inria.lille.commons.synthesis;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.smtlib;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.smtlib.ISort;

import fr.inria.lille.commons.collections.Multimap;
import fr.inria.lille.commons.synthesis.expression.ValuedExpression;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ValuedExpressionLocationVariable;


public class LocationVariableTest {

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void emptyBySort() {
		Multimap<ISort,LocationVariable<?>> bySort = LocationVariable.bySort((List) Arrays.asList());
		assertTrue(bySort.isEmpty());
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void bySortWithElements() {
		ValuedExpressionLocationVariable<Boolean> a = new ValuedExpressionLocationVariable<>(new ValuedExpression<>(Boolean.class, "a", true), "in0", 0);
		ValuedExpressionLocationVariable<Integer> b = new ValuedExpressionLocationVariable<>(new ValuedExpression<>(Integer.class, "b", 1), "in1", 0);
		ValuedExpressionLocationVariable<Double> c = new ValuedExpressionLocationVariable<>(new ValuedExpression<>(Double.class, "c", 1.0), "in2", 0);
		
		Multimap<ISort,LocationVariable<?>> bySort = LocationVariable.bySort((List) Arrays.asList(a, b, c));
		assertEquals(3, bySort.size());

		ISort boolSort = smtlib().boolSort();
		assertTrue(bySort.containsKey(boolSort));
		assertEquals(1, bySort.get(boolSort).size());
		assertEquals(a, bySort.get(boolSort).toArray()[0]);
		
		ISort intSort = smtlib().intSort();
		assertTrue(bySort.containsKey(intSort));
		assertEquals(1, bySort.get(intSort).size());
		assertEquals(b, bySort.get(intSort).toArray()[0]);
		
		ISort numberSort = smtlib().numberSort();
		assertTrue(bySort.containsKey(numberSort));
		assertEquals(1, bySort.get(numberSort).size());
		assertEquals(c, bySort.get(numberSort).toArray()[0]);
	}
}
