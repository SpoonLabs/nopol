package plugin.task;

import actor.ConfigActor;
import actor.ConfigActorImpl;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import fr.inria.lille.repair.common.patch.Patch;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.EventSender;
import plugin.Plugin;
import plugin.actors.ActorManager;
import plugin.wrapper.ApplyPatchWrapper;
import scala.concurrent.Await;
import scala.concurrent.Future;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static plugin.Plugin.nopolContext;

/**
 * Created by bdanglot on 9/21/16.
 */
public class NoPolTask extends Task.Backgroundable {


	public NoPolTask(@Nullable Project project, @Nls(capitalization = Nls.Capitalization.Title) @NotNull String title, String outputZip) {
		super(project, title, false);
		this.outputZip = outputZip;
	}

	private String outputZip;
	private Object response;
	private Future<Object> future;

	private JFrame frame;

	private final Runnable runnerFancyRobot = () -> {
		this.frame = new JFrame();
		this.frame.getContentPane().setLayout(new BorderLayout());
		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(new ImageIcon(this.getClass().getResource("/giphy.gif")));
		JLabel header = new JLabel("NoPol is searching a patch");
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(header);
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JLabel());
		this.frame.getContentPane().add(panel, BorderLayout.NORTH);
		this.frame.getContentPane().add(imageLabel, BorderLayout.CENTER);
		this.frame.setVisible(true);
		this.frame.setLocationRelativeTo(null);
		this.frame.pack();
	};

	@Override
	public void run(@NotNull ProgressIndicator progressIndicator) {
		if (ActorManager.runNopolLocally && !ActorManager.nopolIsRunning) {
			ActorManager.launchNopol();
		}
		Timeout timeout = new Timeout(200000);
		EventSender.send(EventSender.Event.REPAIR_ATTEMPT);
		try {
			ConfigActor configActor = new ConfigActorImpl(nopolContext, Files.readAllBytes(Paths.get(outputZip)));
			this.future = Patterns.ask(ActorManager.remoteActor, configActor, timeout);
			if (Plugin.enableFancyRobot) {
				ApplicationManager.getApplication().invokeLater(runnerFancyRobot);
			}
			this.response = Await.result(future, timeout.duration());
		} catch (Exception e) {
			onError(e);
		}
	}

	@Override
	public void onError(@NotNull Exception error) {
		if (Plugin.enableFancyRobot)
			this.frame.dispose();
		Messages.showMessageDialog(getProject(), error.getMessage(), "Error", Messages.getErrorIcon());
	}

	@Override
	public void onSuccess() {
		super.onSuccess();
		if (Plugin.enableFancyRobot)
			this.frame.dispose();
		if (this.response instanceof List) {
			List<Patch> patches = (List<Patch>) this.response;
			if (patches.isEmpty())
				Messages.showMessageDialog(getProject(), "NoPol could not found any fix", "Fail", Messages.getErrorIcon());
			else {
				EventSender.send(EventSender.Event.REPAIR_SUCCESS);
				ApplyPatchWrapper dialog = new ApplyPatchWrapper(getProject(), patches);
				dialog.getPeer().setTitle("NoPol");
				dialog.show();
			}
		}
	}

	@Override
	public void onCancel() {
		super.onCancel();
		this.future.failed();
		if (Plugin.enableFancyRobot)
			this.frame.dispose();
		Messages.showMessageDialog(getProject(), "The job has been cancelled", "Cancelled", Messages.getErrorIcon());
	}

}
