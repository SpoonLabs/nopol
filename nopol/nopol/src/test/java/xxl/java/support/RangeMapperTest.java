package xxl.java.support;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xxl.java.container.various.Pair;

public class RangeMapperTest {

	@Test
	public void pairsWithUnaryBase() {
		RangeMapper ranger = new RangeMapper(1, 1);
		checkRange(0, 1, ranger, 0);
		checkRange(1, 2, ranger, 1);
		checkRange(2, 3, ranger, 2);
		checkRange(3, 4, ranger, 3);
		checkRange(99, 100, ranger, 99);
		checkRange(300, 301, ranger, 300);
		checkRange(100041, 100042, ranger, 100041);
	}
	
	@Test
	public void pairsWithStep1() {
		RangeMapper ranger = new RangeMapper(10, 1);
		checkRange(0, 1, ranger, 0);
		checkRange(1, 2, ranger, 1);
		checkRange(2, 3, ranger, 2);
		checkRange(9, 10, ranger, 9);
		checkRange(10, 20, ranger, 10);
		checkRange(10, 20, ranger, 11);
		checkRange(20, 30, ranger, 20);
		checkRange(20, 30, ranger, 21);
		checkRange(30, 40, ranger, 30);
		checkRange(40, 50, ranger, 40);
		checkRange(50, 60, ranger, 50);
		checkRange(90, 100, ranger, 90);
		checkRange(100, 200, ranger, 100);
		checkRange(200, 300, ranger, 200);
		checkRange(900, 1000, ranger, 900);
		checkRange(1000, 2000, ranger, 1000);
	}
	
	@Test
	public void pairsWithStep2() {
		RangeMapper ranger = new RangeMapper(10, 2);
		checkRange(0, 2, ranger, 0);
		checkRange(0, 2, ranger, 1);
		checkRange(2, 4, ranger, 2);
		checkRange(2, 4, ranger, 3);
		checkRange(4, 6, ranger, 4);
		checkRange(4, 6, ranger, 5);
		checkRange(6, 8, ranger, 6);
		checkRange(6, 8, ranger, 7);
		checkRange(8, 10, ranger, 8);
		checkRange(8, 10, ranger, 9);
		checkRange(10, 20, ranger, 10);
		checkRange(10, 20, ranger, 11);
		checkRange(20, 40, ranger, 20);
		checkRange(20, 40, ranger, 30);
		checkRange(40, 60, ranger, 40);
		checkRange(40, 60, ranger, 50);
		checkRange(60, 80, ranger, 60);
		checkRange(80, 100, ranger, 80);
		checkRange(100, 200, ranger, 100);
		checkRange(200, 400, ranger, 200);
		checkRange(800, 1000, ranger, 800);
		checkRange(1000, 2000, ranger, 1000);
	}
	
	@Test
	public void pairsWithStep5() {
		RangeMapper ranger = new RangeMapper(10, 5);
		checkRange(0, 5, ranger, 0);
		checkRange(0, 5, ranger, 1);
		checkRange(0, 5, ranger, 3);
		checkRange(5, 10, ranger, 5);
		checkRange(5, 10, ranger, 8);
		checkRange(10, 50, ranger, 10);
		checkRange(10, 50, ranger, 30);
		checkRange(50, 100, ranger, 50);
		checkRange(50, 100, ranger, 75);
		checkRange(100, 500, ranger, 100);
		checkRange(500, 1000, ranger, 666);
		checkRange(1000, 5000, ranger, 1111);
		checkRange(5000, 10000, ranger, 6666);
		checkRange(10000, 50000, ranger, 11111);
	}
	
	@Test
	public void pairsWithStepMinus5() {
		RangeMapper ranger = new RangeMapper(10, 5);
		checkRange(0, 5, ranger, 0);
		checkRange(-5, 0, ranger, -1);
		checkRange(-5, 0, ranger, -3);
		checkRange(-10, -5, ranger, -5);
		checkRange(-10, -5, ranger, -8);
		checkRange(-50, -10, ranger, -10);
		checkRange(-50, -10, ranger, -30);
		checkRange(-100, -50, ranger, -50);
		checkRange(-100, -50, ranger, -75);
		checkRange(-500, -100, ranger, -100);
		checkRange(-1000, -500, ranger, -666);
		checkRange(-5000, -1000, ranger, -1111);
		checkRange(-10000, -5000, ranger, -6666);
		checkRange(-50000, -10000, ranger, -11111);
	}
	
	@Test
	public void pairsWithBinaryBaseStep1() {
		RangeMapper ranger = new RangeMapper(2, 1);
		checkRange(0, 1, ranger, 0);
		checkRange(1, 2, ranger, 1);
		checkRange(2, 4, ranger, 2);
		checkRange(2, 4, ranger, 3);
		checkRange(4, 8, ranger, 4);
		checkRange(4, 8, ranger, 5);
		checkRange(4, 8, ranger, 6);
		checkRange(4, 8, ranger, 7);
		checkRange(8, 16, ranger, 8);
		checkRange(16, 32, ranger, 16);
		checkRange(32, 64, ranger, 32);
		checkRange(64, 128, ranger, 64);
	}
	
	@Test
	public void pairsWithOctaryBaseStep4() {
		RangeMapper ranger = new RangeMapper(8, 4);
		checkRange(0, 4, ranger, 0);
		checkRange(4, 8, ranger, 4);
		checkRange(8, 32, ranger, 8);
		checkRange(32, 64, ranger, 32);
		checkRange(64, 256, ranger, 64);
		checkRange(256, 512, ranger, 256);
		checkRange(512, 2048, ranger, 512);
		checkRange(2048, 4096, ranger, 2048);
		checkRange(4096, 16384, ranger, 4096);
	}
	
	private void checkRange(int low, int high, RangeMapper ranger, int value) {
		assertEquals(Pair.from(low, high), ranger.rangeFor(value));
	}
}
