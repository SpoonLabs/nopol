package plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import plugin.EventSender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ActionGenerator implements ActionListener {

		private final String BASE_PATH_TOY_PROJECT;

		private final Project project;

		public ActionGenerator(Project project) {
			this.project = project;
			this.BASE_PATH_TOY_PROJECT = this.project.getBasePath() + "/toy-project";
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				final File fdRootModule = new File(this.BASE_PATH_TOY_PROJECT);
				fdRootModule.mkdir();
				final File srcFolder = new File(this.BASE_PATH_TOY_PROJECT + "/src");
				srcFolder.mkdir();
				new File(this.BASE_PATH_TOY_PROJECT + "/src/nopol_example/").mkdir();
				final File testFolder = new File(this.BASE_PATH_TOY_PROJECT + "/test");
				testFolder.mkdir();
				new File(this.BASE_PATH_TOY_PROJECT + "/test/nopol_example/").mkdir();
				copyFileFromResources("/toy-project/toy-project.iml.resource", this.BASE_PATH_TOY_PROJECT + "/toy-project.iml");
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
			project.getBaseDir().refresh(false, true);
			buildModule();
			EventSender.send(EventSender.Event.GENERATE_TOY_PROJECT);
		}

		private void buildModule() {
			try {
				copyFileFromResources("/toy-project/src/NopolExample.java", this.BASE_PATH_TOY_PROJECT + "/src/nopol_example/NopolExample.java");
				copyFileFromResources("/toy-project/test/NopolExampleTest.java", this.BASE_PATH_TOY_PROJECT + "/test/nopol_example/NopolExampleTest.java");
				copyFileFromResources("/toy-project/pom.xml", this.BASE_PATH_TOY_PROJECT + "/pom.xml");
			} catch (IOException e) {
				e.printStackTrace();
			}

			WriteCommandAction.runWriteCommandAction(project, () -> {
				try {
					ModuleManager.getInstance(project).loadModule(this.BASE_PATH_TOY_PROJECT + "/toy-project.iml");
					project.getBaseDir().refresh(false, true);
				} catch (Exception ignored) {
					ignored.printStackTrace();
				}
			});
		}

		private void copyFileFromResources(String in, String out) throws IOException {
			final File file = new File(this.getClass().getResource(in).getPath());
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			final File imlFile = new File(out);
			imlFile.createNewFile();
			FileWriter writer = new FileWriter(imlFile);
			final String nl = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				writer.write(line + nl);
			}
			writer.close();
		}
	}