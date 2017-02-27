package plugin.wrapper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;
import plugin.gui.ConfigPanel;

import javax.swing.*;

/**
 * Created by bdanglot on 9/21/16.
 */
public class ConfigWrapper extends DialogWrapper {

	private final JComponent panel;

	public ConfigWrapper(Project project) {
		super(false);
		this.panel = new ConfigPanel(project);
		this.init();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return panel;
	}

	@Override
	protected void doOKAction() {
		super.doOKAction();
	}

	@Nullable
	@Override
	protected ValidationInfo doValidate() {
		return super.doValidate();
	}

	@Override
	public void doCancelAction() {
		super.doCancelAction();
	}
}
