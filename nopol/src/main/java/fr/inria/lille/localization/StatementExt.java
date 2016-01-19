package fr.inria.lille.localization;

import com.gzoltar.core.components.Component;
import com.gzoltar.core.components.Statement;
import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.localization.metric.Ochiai;

/**
 * Created by spirals on 24/07/15.
 */
public class StatementExt extends Statement  {
    private int ep;
    private int ef;
    private int np;
    private int nf;
    private Metric defaultMetric;

    public StatementExt(Component c, int lN) {
        super(c, lN);
    }
    public StatementExt(Statement s) {
        this(s, new Ochiai());
    }

    public StatementExt(Statement s, Metric defaultMetric) {
        super(s.getParent(), s.getLineNumber());
        this.defaultMetric = defaultMetric;
        this.setLabel(s.getLabel());
        this.setSuspiciousness(s.getSuspiciousness());
        this.setLineNumber(s.getLineNumber());
    }

    public int getEf() {
        return ef;
    }

    public int getEp() {
        return ep;
    }

    public int getNf() {
        return nf;
    }

    public int getNp() {
        return np;
    }

    public void setEf(int ef) {
        this.ef = ef;
    }

    public void setEp(int ep) {
        this.ep = ep;
    }

    public void setNf(int nf) {
        this.nf = nf;
    }

    public void setNp(int np) {
        this.np = np;
    }

    @Override
    public double getSuspiciousness() {
        return getSuspiciousness(this.defaultMetric);
    }

    public double getSuspiciousness(Metric metric) {
        return metric.value(ef, ep, nf, np);
    }

    @Override
    public int compareTo(Component s) {
        if(s instanceof StatementExt) {
            return (int) Math.floor(s.getSuspiciousness() - getSuspiciousness());
        }
        return super.compareTo(s);
    }

    @Override
    public String toString() {
        return super.getName() + ":" + getLineNumber() + " " + getSuspiciousness();
    }
}
