package fr.inria.lille.localization.metric;

/**
 * Created by spirals on 24/07/15.
 */
public class Ochiai implements Metric {
	@Override
	public double value(int ef, int ep, int nf, int np) {
		return ef / Math.sqrt((ef + ep) * (ef + nf));
	}
}
