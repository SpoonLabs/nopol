package plugin;

import com.intellij.openapi.project.Project;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Created by bdanglot on 9/22/16.
 */
public class Ziper {

	private final FileOutputStream fos;
	private final ZipOutputStream zos;
	private final String root;

	public Ziper(String zipFilename, Project project) {
		try {
			this.root = project.getBasePath();
			this.fos = new FileOutputStream(zipFilename);
			this.zos = new ZipOutputStream(fos);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void zipIt(String prefix, File root) {

		List<String> files = this.generateFileList(root);

		byte[] buffer = new byte[1024];
		try {
			for (String file : files) {

				String fileName = file;
				if (root.isDirectory()) {
					fileName = file.replace(root.getAbsolutePath() + "/", "");
				} else if (root.isFile()) {
					fileName = root.getName();
				}

				ZipEntry ze = new ZipEntry(prefix + "/" + fileName);
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		try {
			zos.closeEntry();
			zos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Traverse a directory and get all files,
	 * and add the file into fileList
	 *
	 * @param node file or directory
	 */
	private List<String> generateFileList(File node) {

		List<String> fileList = new ArrayList<>();

		//add file only
		if (node.isFile()) {
			fileList.add(node.getAbsoluteFile().toString());
		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				fileList.addAll(generateFileList(new File(node, filename)));
			}
		}
		return fileList;
	}

}
