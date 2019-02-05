package plugin.action;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiUtilBase;
import plugin.Ziper;
import plugin.task.NoPolTask;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import static plugin.Plugin.nopolContext;

/**
 * Created by bdanglot on 9/21/16.
 */
public class NoPolAction extends AbstractAction {

	private final DialogWrapper parent;
	private final AnActionEvent event;

	public NoPolAction(DialogWrapper parent, AnActionEvent event) {
		super("Fix Me");
		this.event = event;
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Project project = event.getData(PlatformDataKeys.PROJECT);
		Editor editor = event.getData(PlatformDataKeys.EDITOR);
		PsiFile currentFile = PsiUtilBase.getPsiFileInEditor(editor, project);

		if (JavaFileType.INSTANCE != currentFile.getFileType())
			return;

		try {
			File outputZip = File.createTempFile(project.getName(), ".zip");
			VirtualFile file = buildTestProject(project, editor, currentFile);
			Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(file);
			Ziper ziper = new Ziper(outputZip.getAbsolutePath(), project);

			//sources folder
			buildSources(ziper, module);

			//Classpath
			buildClasspath(ziper, module);

			ProgressManager.getInstance().run(new NoPolTask(project, "NoPol is Fixing", outputZip.getAbsolutePath()));
			ziper.close();
			this.parent.close(0);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
	}

	private void buildSources(Ziper ziper, Module module) {
		VirtualFile[] rootsFolder = ModuleRootManager.getInstance(module).getSourceRoots();
		for (int i = 0; i < rootsFolder.length; i++)
			ziper.zipIt("src", new File(rootsFolder[i].getCanonicalPath()));
	}

	/**
	 * This method is responsible to add the current test case, if the current file is, otherwise it
	 * it will send a empty array of String in that way nopol will run all tests cases
	 * @param project
	 * @param editor
	 * @param currentFile
	 * @return
	 */
	private VirtualFile buildTestProject(Project project, Editor editor, PsiFile currentFile) {
		VirtualFile file = PsiUtilBase.getPsiFileInEditor(editor, project).getVirtualFile();
		String fullQualifiedNameOfCurrentFile = ((PsiJavaFile) currentFile).getPackageName() + "." + currentFile.getName();
		fullQualifiedNameOfCurrentFile = fullQualifiedNameOfCurrentFile.substring(0, fullQualifiedNameOfCurrentFile.length() - JavaFileType.DEFAULT_EXTENSION.length() - 1);
		if (ProjectRootManager.getInstance(project).getFileIndex().isInTestSourceContent(file)) {
			nopolContext.setProjectTests(fullQualifiedNameOfCurrentFile.split(" "));
		} else {
			nopolContext.setProjectTests(new String[0]);// we will hit all the test case in the project
		}
		return file;
	}

	private void buildClasspath(Ziper ziper, Module module) {
		VirtualFile[] roots = ModuleRootManager.getInstance(module).orderEntries().classes().getRoots();
		for (int i = 0; i < roots.length; i++) {
			String pathFile = roots[i].getCanonicalPath();
			if (!pathFile.contains("jdk")) {
				if (pathFile.endsWith("jar!/")) {
					ziper.zipIt("target", new File(pathFile.substring(0, pathFile.length() - 2)));
				} else {
					ziper.zipIt("target", new File(pathFile));
				}
			}
		}
	}


}
