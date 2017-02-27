package fr.inria.lille.repair.nopol;

import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by urli on 24/02/2017.
 */
public class NopolStatus {

    private List<Patch> patches;
    private ProjectReference projectReference;
    private Config config;
    private long durationInMilliseconds;
    private int nbStatements;
    private int nbAngelicValues;
    private int nbTests;

    public NopolStatus(ProjectReference projectReference, Config config) {
        this.projectReference = projectReference;
        this.config = config;
        this.patches = new ArrayList<Patch>();
    }

    public ProjectReference getProjectReference() {
        return projectReference;
    }

    public Config getConfig() {
        return config;
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
}
