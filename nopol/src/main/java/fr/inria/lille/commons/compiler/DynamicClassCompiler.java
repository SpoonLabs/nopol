package fr.inria.lille.commons.compiler;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.string.StringLibrary;


public class DynamicClassCompiler {
	
	public DynamicClassCompiler() {
		options = asList("-verbose", "-nowarn");
		compiler = ToolProvider.getSystemJavaCompiler();
		diagnostics = new DiagnosticCollector<>();
		DynamicallyCompiledClassLoader classLoader = new DynamicallyCompiledClassLoader();
		StandardJavaFileManager standardFileManager = compiler().getStandardFileManager(diagnostics(), null, null);
		fileManager = new BufferedFileObjectManager(classLoader, standardFileManager);
	}
	
	public synchronized Class<?> compileSource(String qualifiedName, String sourceContent) throws ClassNotFoundException {
		Map<String, String> adHocMap = MapLibrary.newHashMap();
		adHocMap.put(qualifiedName, sourceContent);
		return compileSources(adHocMap).get(qualifiedName);
	}
	
	public synchronized Map<String, Class<?>> compileSources(Map<String, String> qualifiedNameAndContent) throws ClassNotFoundException {
		Collection<JavaFileObject> units = addCompilationUnits(qualifiedNameAndContent);
		CompilationTask task = compiler().getTask(null, fileManager(), diagnostics(), options(), null, units);
		runCompilationTask(task);
		return compiledClasses(qualifiedNameAndContent.keySet());
	}
	
	protected Collection<JavaFileObject> addCompilationUnits(Map<String, String> qualifiedNameAndContent) {
		Collection<JavaFileObject> units = ListLibrary.newArrayList();
		for (String qualifiedName : qualifiedNameAndContent.keySet()) {
			String sourceContent = qualifiedNameAndContent.get(qualifiedName);
			JavaFileObject sourceFile = addCompilationUnit(qualifiedName, sourceContent);
			units.add(sourceFile);
		}
		return units;
	}

	protected JavaFileObject addCompilationUnit(String qualifiedName, String sourceContent) {
		String simpleClassName = StringLibrary.lastAfterSplit(qualifiedName, "[.]");
		String packageName = StringLibrary.stripEnd(qualifiedName, "." + simpleClassName);
		BufferedSourceFileObject sourceFile = new BufferedSourceFileObject(simpleClassName, sourceContent);
		fileManager().addSourceFile(StandardLocation.SOURCE_PATH, packageName, simpleClassName, sourceFile);
		return sourceFile;
	}
	
	protected boolean runCompilationTask(CompilationTask task) {
		boolean success = task.call();
		if (! success) {
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics().getDiagnostics()) {
				System.err.println(diagnostic);
			}
		}
		return success;
	}
	
	protected Map<String, Class<?>> compiledClasses(Collection<String> qualifiedNames) throws ClassNotFoundException {
		Map<String, Class<?>> compiledClasses = MapLibrary.newHashMap();
		for (String qualifiedName : qualifiedNames) {
			compiledClasses.put(qualifiedName, dynamicClassLoader().loadClass(qualifiedName));
		}
		return compiledClasses;
	}
	
	protected BufferedFileObjectManager fileManager() {
		return fileManager;
	}
	
	protected ClassLoader dynamicClassLoader() {
		return fileManager().classLoader();
	}
	
	private List<String> options() {
		return options;
	}
	
	
	private JavaCompiler compiler() {
		return compiler;
	}
	
	
	private DiagnosticCollector<JavaFileObject> diagnostics() {
		return diagnostics;
	}
	
	private List<String> options;
	private JavaCompiler compiler;
	private DiagnosticCollector<JavaFileObject> diagnostics;
	private BufferedFileObjectManager fileManager;
}