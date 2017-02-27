package plugin.wrapper;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.action.NoPolAction;
import plugin.gui.LaunchPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class LauncherWrapper extends DialogWrapper {

	private AnActionEvent event;

	private static final JComponent panel = new LaunchPanel();

	public LauncherWrapper(AnActionEvent event) {
		super(true);
		this.event = event;
		this.init();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return panel;
	}

	@NotNull
	@Override
	protected Action[] createActions() {
		Action[] actions = new Action[3];
		actions[0] = new NoPolAction(this, this.event);
		actions[1] = this.getCancelAction();
		actions[2] = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigWrapper dialog = new ConfigWrapper(event.getProject());
				dialog.getPeer().setTitle("NoPol: Preferences");
				dialog.show();
			}
		};
		actions[actions.length -1 ].putValue(Action.NAME, "Preferences");
		return actions;
	}

	@Override
	protected void doOKAction() {
		//TODO Apply effectively changes
		super.doOKAction();
	}

	@Nullable
	@Override
	protected ValidationInfo doValidate() {
		//TODO Apply effectively changes
		return super.doValidate();
	}

	@Override
	public void doCancelAction() {
		//TODO CancelAllChanges
		super.doCancelAction();
	}
}