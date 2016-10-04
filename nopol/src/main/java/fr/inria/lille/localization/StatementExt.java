package fr.inria.lille.localization;

import com.gzoltar.core.components.Component;
import com.gzoltar.core.components.Statement;
import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.repair.nopol.SourceLocation;

/**
 * Created by spirals on 24/07/15.
 */
public class StatementExt extends StatementSourceLocation {

    private Statement statement;

    public StatementExt(Component c, int lN) {
        super(new SourceLocation(c.getLabel(), lN));
        this.statement = new Statement(c, lN);
    }

    public StatementExt(Statement s) {
        super(new SourceLocation(s.getClazz().getLabel(), s.getLineNumber()));
        this.statement = new Statement(s.getParent(), s.getLineNumber());
        this.statement.setLabel(s.getLabel());
        this.statement.setSuspiciousness(s.getSuspiciousness());
        this.statement.setLineNumber(s.getLineNumber());
    }

    public double getSuspiciousness(Metric metric) {
        return metric.value(super.getEf(), super.getEp(), super.getNf(), super.getNp());
    }

    public int getLineNumber() {
        return this.statement.getLineNumber();
    }

    public String getLabel() {
        return this.statement.getMethod().getParent().getLabel();
    }
}
