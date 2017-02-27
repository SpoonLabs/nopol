package plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import plugin.action.ActionGenerator;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 12/7/16
 */
public class Generator extends AnAction {

    public Generator() {
        super("Generate toy-project");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        new ActionGenerator(e.getProject()).actionPerformed(null);
    }
}
