package fr.inria.lille.commons.synthesis;

import java.util.List;

import static java.util.Arrays.asList;

public class NullCodeGenesis extends CodeGenesis {

    public NullCodeGenesis() {
        super(null, null);
    }

    @Override
    protected void writeCode() {
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public String returnStatement() {
        return "";
    }

    @Override
    public List<CodeLine> codeLines() {
        return asList();
    }

    @Override
    public int numberOfInputLines() {
        return 0;
    }

    @Override
    public int totalNumberOfLines() {
        return 0;
    }

    @Override
    public String toString() {
        return "Unsuccessful code genesis";
    }
}
