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
		this(new DynamicallyCompiledClassLoader());
	}
	
	public DynamicClassCompiler(ClassLoader dynamicallyCompiledClassLoaderParent) {
		this(new DynamicallyCompiledClassLoader(dynamicallyCompiledClassLoaderParent));
	}
	
	protected DynamicClassCompiler(DynamicallyCompiledClassLoader classLoader) {
		options = asList("-nowarn");
		compiler = ToolProvider.getSystemJavaCompiler();
		diagnostics = new DiagnosticCollector<>();
		StandardJavaFileManager standardFileManager = compiler().getStandardFileManager(diagnostics(), null, null);
		fileManager = new VirtualFileObjectManager(classLoader, standardFileManager);
	}

	public synchronized ClassLoader classLoaderFor(String qualifiedName, String sourceContent) {
		Map<String, String> adHocMap = MapLibrary.newHashMap();
		adHocMap.put(qualifiedName, sourceContent);
		return classLoaderFor(adHocMap);
	}
	
	public synchronized ClassLoader classLoaderFor(Map<String, String> qualifiedNameAndContent) {
		Collection<JavaFileObject> units = addCompilationUnits(qualifiedNameAndContent);
		CompilationTask task = compiler().getTask(null, fileManager(), diagnostics(), options(), null, units);
		runCompilationTask(task);
		return dynamicClassLoader().copy();
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
		VirtualSourceFileObject sourceFile = new VirtualSourceFileObject(simpleClassName, sourceContent);
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
	
	protected VirtualFileObjectManager fileManager() {
		return fileManager;
	}
	
	protected DynamicallyCompiledClassLoader dynamicClassLoader() {
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
	private VirtualFileObjectManager fileManager;
	private DiagnosticCollector<JavaFileObject> diagnostics;
}