package plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.synth.RepairType;
import plugin.actors.ActorManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import plugin.wrapper.LauncherWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Benjamin DANGLOT (benjamin.danglot@inria.fr) on 9/15/16.
 */
public class Plugin extends AnAction {

    private static final String CONFIG_PATHNAME = "config.properties";

    public static final Properties properties = new Properties();

    public static boolean enableFancyRobot = true;

    public static final NopolContext nopolContext = new NopolContext();

    public Plugin() {
        super("NoPol");
        ActorManager.createActorSystem(Plugin.class.getClassLoader());
        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerAdapter() {
            @Override
            public void projectOpened(Project project) {
                ActorManager.launchNopol();
            }

            @Override
            public void projectClosed(Project project) {
                ActorManager.stopNopolLocally();
            }
        });
        try {
            properties.load(new FileInputStream(new File(Plugin.class.getClassLoader().getResource(CONFIG_PATHNAME).toURI())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initConfig();
        EventSender.send(EventSender.Event.START_PLUGIN);
    }

    private static void initConfig() {
        nopolContext.setSynthesis(NopolContext.NopolSynthesis.DYNAMOTH);
        nopolContext.setType(RepairType.PRE_THEN_COND);
//        config.setLocalizer(Config.NopolLocalizer.COCOSPOON); //CoCospoon take too much time
        nopolContext.setLocalizer(NopolContext.NopolLocalizer.GZOLTAR);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        LauncherWrapper dialog = new LauncherWrapper(event);
        dialog.getPeer().setTitle("NoPol");
        dialog.show();
    }


}
