package fr.inria.lille.repair.nopol;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by urli on 24/02/2017.
 */
public class NopolResult {

    private List<Patch> patches;
    private NopolContext nopolContext;
    private long durationInMilliseconds;
    private long startTime;
    private int nbStatements;
    private int nbTests;
    private NopolStatus nopolStatus;

    public NopolResult(NopolContext nopolContext, long startTime) {
        this.nopolContext = nopolContext;
        this.patches = new ArrayList<Patch>();
        this.startTime = startTime;
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
        return angelicValues.size();
    }

    List<String> angelicValues = new ArrayList<>();
    public void incrementNbAngelicValues(SourceLocation sourceLocation, NopolProcessor conditionalProcessor) {
        this.angelicValues.add(sourceLocation.toString()+ " " + conditionalProcessor.getClass().getSimpleName());
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

    public long getStartTime() {
        return startTime;
    }

    public List<String> getAngelicValues() {
        return Collections.unmodifiableList(angelicValues);
    }
}
