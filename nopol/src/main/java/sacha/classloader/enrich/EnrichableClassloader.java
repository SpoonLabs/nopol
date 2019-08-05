package sacha.classloader.enrich;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EnrichableClassloader extends URLClassLoader{

	List<String> addedUrls = new ArrayList<>();
	private String M2REPO = null;
	private File metadataFolder = null;
	
	public EnrichableClassloader(URL[] urls) {
		super(urls);
	}
	
	public void addURL(String url){
		try {
			addURL(new File(url).toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}


	public void addEclipseMetadata(File metadataFolder) {
		this.metadataFolder=metadataFolder;
	}
	
	public void addEclipseProject(String path){
		String classpathEntries = getEclipseClassPath(path);
		 for (String classpathEntry : classpathEntries.split(File.pathSeparator)) {
			 if(classpathEntry.endsWith(".jar") && !(classpathEntry.endsWith("/") || classpathEntry.endsWith("\\"))){
				 addURL(classpathEntry);
				 addedUrls.add(classpathEntry);
			 }
			 else{
				 addURL(classpathEntry+File.separator);
				 addedUrls.add(classpathEntry+File.separator);
			 }
		 }
	}
	
	private String getM2REPO() throws FileNotFoundException {
		return M2REPO==null?setM2REPO():M2REPO;
	}
	
	private String setM2REPO() throws FileNotFoundException {
		String prefsFile = metadataFolder.getAbsolutePath()+File.separator+".plugins/org.eclipse.core.runtime/.settings/org.eclipse.jdt.core.prefs";
		File file = new File(prefsFile);
		if(!file.exists() || !file.canRead()){
			throw new FileNotFoundException("cannot find or read "+prefsFile);
		}
		BufferedReader isr = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		try {
			String line = null;
			while((line = isr.readLine())!=null){
				if(line.startsWith("org.eclipse.jdt.core.classpathVariable.M2_REPO=")){
					M2REPO = line.replace("org.eclipse.jdt.core.classpathVariable.M2_REPO=", "");
					return M2REPO;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				isr.close();
			} catch (IOException e) {
			}
		}
		throw new MissingResourceException("cannot find org.eclipse.jdt.core.classpathVariable.M2_REPO" +
				" in file "+file,"org.eclipse.jdt.core.prefs","org.eclipse.jdt.core.classpathVariable.M2_REPO");
	}//org.eclipse.jdt.core.classpathVariable.M2_REPO=/home/bcornu/.m2/repository

	private String getEclipseClassPath(String srcFolderPath,String... ignoredPaths) {
		String classpaths = null;
		try{
			if(srcFolderPath==null || srcFolderPath.replaceAll("\\s", "").isEmpty())return ".";
			String[] folderPaths = srcFolderPath.split(File.pathSeparator);
			if(folderPaths.length>1){
				String tmp;
				Set<String> paths = new TreeSet<String>();
				for (String folderPath : folderPaths) {
					tmp = getEclipseClassPath(folderPath,folderPaths);
					if(tmp!=null && !tmp.isEmpty())
						for (String entry : tmp.split(File.pathSeparator)) {
							paths.add(entry);
						}
				}
				String res = ".";
				for (String path : paths) {
					res+=File.pathSeparator+path;
				}
				return res;
			}
			File currentPath = new File(srcFolderPath);
			String workspaceLoc = null;
			if(currentPath.isFile())
				currentPath = currentPath.getParentFile();
			while(currentPath!=null && currentPath.exists()){
				for (File file : currentPath.listFiles()) {
					if(file.getName().equals(".classpath")){
						if(workspaceLoc==null)
							workspaceLoc = currentPath.getParent();
						FileInputStream reader = new FileInputStream(file);
						Element cpElement;
						try {
							DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
							cpElement = parser.parse(new InputSource(reader)).getDocumentElement();
						} catch (SAXException e) {
							throw new IOException("bad format"); 
						} catch (ParserConfigurationException e) {
							throw new IOException("bad format"); 
						} finally {
							reader.close();
						}
					
						if (!cpElement.getNodeName().equalsIgnoreCase("classpath")) {
							throw new IOException("bad format"); 
						}
						NodeList list = cpElement.getElementsByTagName("classpathentry");
						int length = list.getLength();
						Node node;
						for (int i = 0; i < length; ++i) {
							if ((node = list.item(i)).getNodeType() == Node.ELEMENT_NODE){
								if("output".equalsIgnoreCase(node.getAttributes().getNamedItem("kind").getNodeValue()) 
										|| "lib".equalsIgnoreCase(node.getAttributes().getNamedItem("kind").getNodeValue())){
									String currentPathString = node.getAttributes().getNamedItem("path").getNodeValue();
									if(!(currentPathString.startsWith("/") || currentPathString.startsWith("\\")))
										currentPathString=currentPath.getAbsolutePath()+File.separator+currentPathString;
									if(classpaths == null)
										classpaths = currentPathString;
									else
										classpaths+= File.pathSeparator+currentPathString;
								}else if("var".equalsIgnoreCase(node.getAttributes().getNamedItem("kind").getNodeValue())){
									String currentPathString = node.getAttributes().getNamedItem("path").getNodeValue();
									if(currentPathString.contains("M2_REPO"))
										currentPathString = currentPathString.replaceFirst("M2_REPO", getM2REPO());
									else{
										System.err.println("cannot resolve : "+currentPathString);
										continue;
									}
									if(!(currentPathString.startsWith("/") || currentPathString.startsWith("\\")))
										currentPathString=currentPath.getAbsolutePath()+File.separator+currentPathString;
									if(classpaths == null)
										classpaths = currentPathString;
									else
										classpaths+= File.pathSeparator+currentPathString;
								}else if("src".equalsIgnoreCase(node.getAttributes().getNamedItem("kind").getNodeValue())){
									String currentPathString = node.getAttributes().getNamedItem("path").getNodeValue();
									boolean perform = currentPathString.startsWith("/");
									if(perform && ignoredPaths!=null)
									{
										currentPathString = workspaceLoc+currentPathString;
										for (String ignoredPath : ignoredPaths)
											if(ignoredPath.equalsIgnoreCase(currentPathString))
												perform=false;
									}
									if(perform){
										File projectFolder=new File(currentPathString);
										if(projectFolder.exists() && projectFolder.isDirectory()){
											for (File file1 : projectFolder.listFiles()) {
												if(file1.getName().equals(".classpath")){
													FileInputStream reader1 = new FileInputStream(file1);
													Element cpElement1;
													try {
														DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
														cpElement1 = parser.parse(new InputSource(reader1)).getDocumentElement();
													} catch (SAXException e) {
														throw new IOException("bad format"); 
													} catch (ParserConfigurationException e) {
														throw new IOException("bad format"); 
													} finally {
														reader1.close();
													}
													if (!cpElement1.getNodeName().equalsIgnoreCase("classpath")) {
														throw new IOException("bad format"); 
													}
													NodeList list1 = cpElement1.getElementsByTagName("classpathentry");
													int length1 = list1.getLength();
													Node node1;
													for (int i1 = 0; i1 < length1; ++i1) {
														if ((node1 = list1.item(i1)).getNodeType() == Node.ELEMENT_NODE 
																&& "output".equalsIgnoreCase(node1.getAttributes().getNamedItem("kind").getNodeValue())){
															String outputPathString = projectFolder.getAbsolutePath()+File.separator+node1.getAttributes().getNamedItem("path").getNodeValue();
															if(classpaths == null)
																classpaths = outputPathString;
															else
																classpaths+= File.pathSeparator+outputPathString;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				if(classpaths==null)
					currentPath = currentPath.getParentFile();
				else break;
			}
		}catch(Exception e){
	    	System.err.println("will use default classpath due to :"+e);
		}
		return classpaths;
	}
	
}
