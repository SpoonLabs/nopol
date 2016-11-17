package fr.inria.lille.spirals.diff.testclasses;

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
}
