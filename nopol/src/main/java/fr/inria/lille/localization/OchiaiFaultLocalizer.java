package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.Config;

import java.io.File;
import java.net.URL;

/**
 * Created by bdanglot on 10/3/16.
 */
public class OchiaiFaultLocalizer extends CocoSpoonBasedSpectrumBasedFaultLocalizer {

	public OchiaiFaultLocalizer(File[] sourcesClasses, URL[] classpath, String[] testClasses, Config config) {
		super(sourcesClasses, classpath, testClasses, config, new Ochiai());
	}
}
