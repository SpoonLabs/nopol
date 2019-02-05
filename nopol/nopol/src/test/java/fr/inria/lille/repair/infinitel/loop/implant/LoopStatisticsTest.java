package fr.inria.lille.repair.infinitel.loop.implant;

import static fr.inria.lille.repair.infinitel.loop.implant.LoopStatistics.firstQuartileOf;
import static fr.inria.lille.repair.infinitel.loop.implant.LoopStatistics.medianOf;
import static fr.inria.lille.repair.infinitel.loop.implant.LoopStatistics.sumOf;
import static fr.inria.lille.repair.infinitel.loop.implant.LoopStatistics.thirdQuartileOf;
import static java.util.Arrays.asList;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static xxl.java.container.various.Bag.newHashBag;

import org.junit.Test;

import xxl.java.container.various.Bag;

public class LoopStatisticsTest {

	@Test
	public void percentileValueOfBag() {
		Bag<Integer> bag = newHashBag();
		assertTrue(0.0 == firstQuartileOf(bag));
		assertTrue(0.0 == medianOf(bag));
		assertTrue(0.0 == thirdQuartileOf(bag));
		bag.add(1);		// |1|
		assertTrue(1.0 == firstQuartileOf(bag));
		assertTrue(1.0 == medianOf(bag));
		assertTrue(1.0 == thirdQuartileOf(bag));
		bag.add(2);		// 1 | 2
		assertTrue(1.0 == firstQuartileOf(bag));
		assertTrue(1.5 == medianOf(bag));
		assertTrue(2.0 == thirdQuartileOf(bag));
		bag.add(2);		// 1 |2| 2
		assertTrue(1.0 == firstQuartileOf(bag));
		assertTrue(2.0 == medianOf(bag));
		assertTrue(2.0 == thirdQuartileOf(bag));
		bag.add(3, 4);	// 1 2 2 |3| 3 3 3
		assertTrue(1.5 == firstQuartileOf(bag));
		assertTrue(3.0 == medianOf(bag));
		assertTrue(3.0 == thirdQuartileOf(bag));
		bag.add(4, 3);	// 1 2 2 3 3 | 3 3 4 4 4
		assertTrue(2.0 == firstQuartileOf(bag));
		assertTrue(3.0 == medianOf(bag));
		assertTrue(3.5 == thirdQuartileOf(bag));
		bag.add(5);		// 1 2 2 3 3 |3| 3 4 4 4 5
		bag.add(0, 2);	// 0 0 1 2 2 3 |3| 3 3 4 4 4 5
		bag.add(8, 5);	// 0 0 1 2 2 3 3 3 3 | 4 4 4 5 8 8 8 8 8
		assertTrue(2.0 == firstQuartileOf(bag));
		assertTrue(3.5 == medianOf(bag));
		assertTrue(6.5 == thirdQuartileOf(bag));
	}
	
	@Test
	public void sumIntegerBag() {
		Bag<Integer> bag = newHashBag();
		assertEquals(0, sumOf(bag));
		bag.addAll(asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
		assertEquals(70, sumOf(bag));
	}

	public static String absolutePathOf(int exampleNumber) {
		return format("../test-projects/src/main/java/infinitel_examples/infinitel_example_%d/InfinitelExample.java", exampleNumber);
	}

}
