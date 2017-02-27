package plugin.wrapper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.*;
import com.intellij.psi.search.EverythingGlobalScope;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.action.ApplyPatchAction;
import plugin.gui.ApplyPatchPanel;

import javax.swing.*;
import java.util.List;

/**
 * Created by bdanglot on 9/21/16.
 */
public class ApplyPatchWrapper extends DialogWrapper {

	private final JComponent panel;

	public Project getProject() {
		return project;
	}

	public Patch getSelectedPatch() {
		return selectedPatch;
	}

	public PsiElement getBuggyElement() {
		return buggyElement;
	}

	private final Project project;

	private Patch selectedPatch;
	private PsiStatement patchStatement;
	private PsiElement buggyElement;

	public ApplyPatchWrapper(Project project, List<Patch> patches) {
		super(true);
		this.project = project;
		this.panel = new ApplyPatchPanel(this, patches);
		this.init();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return this.panel;
	}


	@NotNull
	@Override
	protected Action[] createActions() {
		Action[] actions = new Action[2];
		actions[0] = new ApplyPatchAction(this);
		actions[1] = this.getCancelAction();
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

	public void setSelectedPatch(Patch selectedPatch) {
		System.out.println(selectedPatch.asString() + " selected! ");
		this.selectedPatch = selectedPatch;
		final SourceLocation location = this.selectedPatch.getSourceLocation();
		PsiClass classToBeFix = JavaPsiFacade.getInstance(this.project).findClass(this.selectedPatch.getRootClassName(), new EverythingGlobalScope(this.project));
		classToBeFix.accept(new JavaRecursiveElementVisitor() {
			@Override
			public void visitStatement(PsiStatement statement) {
				if (location.getBeginSource() == statement.getTextOffset() &&
						location.getEndSource() == statement.getTextOffset() + statement.getTextLength() - 1) {
					buggyElement = statement;
				}
				super.visitStatement(statement);
			}
		});
	}
}