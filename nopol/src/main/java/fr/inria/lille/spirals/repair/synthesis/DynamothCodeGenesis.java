package fr.inria.lille.spirals.repair.synthesis;

import fr.inria.lille.spirals.repair.commons.Candidates;

/**
 * Created by Thomas Durieux on 06/03/15.
 */
public interface DynamothCodeGenesis {

    /**
     * Run the synthesizer
     *
     * @param remainingTime
     * @return the patch
     */
    Candidates run(long remainingTime);
    
    Candidates getCollectedExpressions();
    
    Candidates getValidExpressions();

}
