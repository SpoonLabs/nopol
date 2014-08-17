package fr.inria.lille.commons.synthesis;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.boolSort;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.intSort;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.numberSort;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.smtlib.ISort;

import xxl.java.container.map.Multimap;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.expression.ObjectTemplate;
import fr.inria.lille.commons.synthesis.smt.locationVariables.IndexedLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;


public class LocationVariableTest {

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void emptyBySort() {
		Multimap<ISort, ObjectTemplate<?>> bySort = ObjectTemplate.bySort((List) Arrays.asList());
		assertTrue(bySort.isEmpty());
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void bySortWithElements() {
		IndexedLocationVariable<Boolean> a = new IndexedLocationVariable<>(new Expression<>(Boolean.class, "a"), "in0", 0);
		IndexedLocationVariable<Integer> b = new IndexedLocationVariable<>(new Expression<>(Integer.class, "b"), "in1", 1);
		IndexedLocationVariable<Double> c = new IndexedLocationVariable<>(new Expression<>(Double.class, "c"), "in2", 2);
		
		Multimap<ISort,LocationVariable<?>> bySort = (Multimap) ObjectTemplate.bySort((List) Arrays.asList(a, b, c));
		assertEquals(3, bySort.size());

		ISort boolSort = boolSort();
		assertTrue(bySort.containsKey(boolSort));
		assertEquals(1, bySort.get(boolSort).size());
		assertEquals(a, bySort.get(boolSort).toArray()[0]);
		
		ISort intSort = intSort();
		assertTrue(bySort.containsKey(intSort));
		assertEquals(1, bySort.get(intSort).size());
		assertEquals(b, bySort.get(intSort).toArray()[0]);
		
		ISort numberSort = numberSort();
		assertTrue(bySort.containsKey(numberSort));
		assertEquals(1, bySort.get(numberSort).size());
		assertEquals(c, bySort.get(numberSort).toArray()[0]);
	}
}
