package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.NopolContext;

/**
 * Created by bdanglot on 10/3/16.
 */
public class OchiaiFaultLocalizer extends CocoSpoonBasedSpectrumBasedFaultLocalizer {

	public OchiaiFaultLocalizer(NopolContext nopolContext) {
		super(nopolContext, new Ochiai());
	}
}
