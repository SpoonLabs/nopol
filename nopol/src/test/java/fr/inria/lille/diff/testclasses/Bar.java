package fr.inria.lille.diff.testclasses;

public class Bar {

	public void m() {
		if (true) {

		}

		if (true) {

		} else if (true) {

		}

		System.out.println("test");

		throw new RuntimeException("FirstLine" +
		"Second Line" +
		"Third Line");
	}

	public void m2(){
		if (true) {
			System.out.println("test");
		}
	}

	public void m3(){
		if (true) {

		} else {
			System.out.println("test1");
			System.out.println("test2");
		}
	}
}
