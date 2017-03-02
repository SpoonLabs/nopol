package fr.inria.lille.repair.nopol;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.patch.Patch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by urli on 24/02/2017.
 */
public class NopolResult {

    private List<Patch> patches;
    private NopolContext nopolContext;
    private long durationInMilliseconds;
    private int nbStatements;
    private int nbAngelicValues;
    private int nbTests;
    private NopolStatus nopolStatus;

    public NopolResult(NopolContext nopolContext) {
        this.nopolContext = nopolContext;
        this.patches = new ArrayList<Patch>();
    }

    public NopolContext getNopolContext() {
        return nopolContext;
    }

    public List<Patch> getPatches() {
        return patches;
    }

    public void addPatches(List<Patch> patches) {
        this.patches.addAll(patches);
    }

    public long getDurationInMilliseconds() {
        return durationInMilliseconds;
    }

    public void setDurationInMilliseconds(long durationInMilliseconds) {
        this.durationInMilliseconds = durationInMilliseconds;
    }

    public int getNbStatements() {
        return nbStatements;
    }

    public void setNbStatements(int nbStatements) {
        this.nbStatements = nbStatements;
    }

    public int getNbAngelicValues() {
        return nbAngelicValues;
    }

    public void incrementNbAngelicValues() {
        this.nbAngelicValues++;
    }

    public int getNbTests() {
        return nbTests;
    }

    public void setNbTests(int nbTests) {
        this.nbTests = nbTests;
    }

    public NopolStatus getNopolStatus() {
        return nopolStatus;
    }

    public void setNopolStatus(NopolStatus nopolStatus) {
        this.nopolStatus = nopolStatus;
    }
}
