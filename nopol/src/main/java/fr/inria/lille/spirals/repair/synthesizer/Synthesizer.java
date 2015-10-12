package fr.inria.lille.spirals.repair.synthesizer;

import fr.inria.lille.spirals.repair.commons.Candidates;

/**
 * Created by Thomas Durieux on 06/03/15.
 */
public interface Synthesizer {

    /**
     * Run the synthesizer
     *
     * @param remainingTime
     * @return the patch
     */
    Candidates run(long remainingTime);
}
