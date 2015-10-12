package fr.inria.lille.commons.synthesis;

import xxl.java.container.classic.MetaList;

import java.util.Collection;
import java.util.List;


public class CodeLine {

    public static List<String> collectContent(Collection<CodeLine> codeLines) {
        List<String> contents = MetaList.newArrayList();
        for (CodeLine codeLine : codeLines) {
            contents.add(codeLine.content());
        }
        return contents;
    }

    public CodeLine(int lineNumber, String content) {
        this.lineNumber = lineNumber;
        this.content = content;
    }

    public String content() {
        return content;
    }

    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return lineNumber() + ": " + content();
    }

    private int lineNumber;
    protected String content;
}
