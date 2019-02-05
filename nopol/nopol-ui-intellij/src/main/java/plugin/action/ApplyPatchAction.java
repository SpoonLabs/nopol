package plugin.action;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.DocCommandGroupId;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.EverythingGlobalScope;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.RepairType;
import plugin.wrapper.ApplyPatchWrapper;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ApplyPatchAction extends AbstractAction {

	private final ApplyPatchWrapper parent;

	public ApplyPatchAction(ApplyPatchWrapper parent) {
		super("ApplyPatch");
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Project project = this.parent.getProject();
		PsiElement buggyElement = this.parent.getBuggyElement();
		final Patch selectedPatch = this.parent.getSelectedPatch();

		PsiClass classToBeFix = JavaPsiFacade.getInstance(project).findClass(selectedPatch.getRootClassName(), new EverythingGlobalScope(project));
		OpenFileDescriptor descriptor = new OpenFileDescriptor(project, classToBeFix.getContainingFile().getVirtualFile(), buggyElement.getTextOffset());
		Editor editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
		Document modifiedDocument = editor.getDocument();

		final String patch;

		if (selectedPatch.getType() == RepairType.CONDITIONAL) {
			buggyElement = ((PsiIfStatement) buggyElement).getCondition();
			patch = selectedPatch.asString();
		} else {
			String newline = FileDocumentManager.getInstance().getFile(modifiedDocument).getDetectedLineSeparator();
			StringBuilder sb = new StringBuilder();
			sb.append("if( ");
			sb.append(selectedPatch.asString());
			sb.append(" ) {" + newline);
			sb.append(buggyElement.getText() + newline);
			sb.append("}");
			patch = sb.toString();
		}

		final PsiElement finalBuggyElement = buggyElement;

		CommandProcessor.getInstance().executeCommand(project, () -> WriteCommandAction.runWriteCommandAction(project, () -> {
			//Apply the patch
			modifiedDocument.replaceString(finalBuggyElement.getTextOffset(), finalBuggyElement.getTextOffset() + finalBuggyElement.getTextLength(), patch);
			//Move caret to modification
			editor.getCaretModel().moveToOffset(finalBuggyElement.getTextOffset());
			//Select patch
			editor.getSelectionModel().setSelection(finalBuggyElement.getTextOffset(), finalBuggyElement.getTextOffset() +
					(selectedPatch.getType() == RepairType.CONDITIONAL ? finalBuggyElement.getTextLength() : patch.length()));
			PsiDocumentManager.getInstance(project).commitDocument(modifiedDocument);
			CodeStyleManager.getInstance(project).reformat(PsiDocumentManager.getInstance(project).getPsiFile(modifiedDocument), false);
		}), "Apply Patch", DocCommandGroupId.noneGroupId(modifiedDocument));

		PsiDocumentManager.getInstance(project).commitDocument(modifiedDocument);

		parent.close(0);
	}
}
