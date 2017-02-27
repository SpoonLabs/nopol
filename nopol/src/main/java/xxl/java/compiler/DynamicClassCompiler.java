package xxl.java.compiler;

import org.slf4j.Logger;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.library.JavaLibrary;
import xxl.java.library.StringLibrary;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static xxl.java.library.LoggerLibrary.logDebug;
import static xxl.java.library.LoggerLibrary.loggerFor;

public class DynamicClassCompiler {

    public DynamicClassCompiler(URL[] classpath) {
        this();
        options = optionsWithClasspath(classpath);
    }

    public DynamicClassCompiler() {
        options = asList("-nowarn");
        compiler = ToolProvider.getSystemJavaCompiler();
        diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager standardFileManager = compiler().getStandardFileManager(diagnostics(), null, null);
        fileManager = new VirtualFileObjectManager(standardFileManager);
    }

    public synchronized byte[] javaBytecodeFor(String qualifiedName, String sourceContent) {
        return javaBytecodeFor(qualifiedName, sourceContent, new HashMap<String, byte[]>());
    }

    public synchronized byte[] javaBytecodeFor(String qualifiedName, String sourceContent, Map<String, byte[]> compiledDependencies) {
        Map<String, String> adHocMap = MetaMap.newHashMap(qualifiedName, sourceContent);
        return javaBytecodeFor(adHocMap, compiledDependencies).get(qualifiedName);
    }

    public synchronized Map<String, byte[]> javaBytecodeFor(Map<String, String> qualifiedNameAndContent) {
        return javaBytecodeFor(qualifiedNameAndContent, new HashMap<String, byte[]>());
    }

    public synchronized Map<String, byte[]> javaBytecodeFor(Map<String, String> qualifiedNameAndContent, Map<String, byte[]> compiledDependencies) {
        //logDebug(logger(), format("[Compiling %d source files]", qualifiedNameAndContent.size()));
        Collection<JavaFileObject> units = addCompilationUnits(qualifiedNameAndContent);
        fileManager().addCompiledClasses(compiledDependencies);
        CompilationTask task = compiler().getTask(null, fileManager(), diagnostics(), options(), null, units);
        runCompilationTask(task);
        Map<String, byte[]> bytecodes = collectBytecodes(qualifiedNameAndContent);
        //logDebug(logger(), format("[Compilation finished successfully (%d classes)]", bytecodes.size()));
        return bytecodes;
    }

    protected Collection<JavaFileObject> addCompilationUnits(Map<String, String> qualifiedNameAndContent) {
        Collection<JavaFileObject> units = MetaList.newArrayList();
        for (String qualifiedName : qualifiedNameAndContent.keySet()) {
            String sourceContent = qualifiedNameAndContent.get(qualifiedName);
            JavaFileObject sourceFile = addCompilationUnit(qualifiedName, sourceContent);
            units.add(sourceFile);
        }
        return units;
    }

    protected JavaFileObject addCompilationUnit(String qualifiedName, String sourceContent) {
        String simpleClassName = StringLibrary.lastAfterSplit(qualifiedName, '.');
        String packageName = StringLibrary.stripEnd(qualifiedName, '.' + simpleClassName);
        VirtualSourceFileObject sourceFile = new VirtualSourceFileObject(simpleClassName, sourceContent);
        fileManager().addSourceFile(StandardLocation.SOURCE_PATH, packageName, simpleClassName, sourceFile);
        return sourceFile;
    }

    protected boolean runCompilationTask(CompilationTask task) {
        boolean success = task.call();
        if (!success) {
            Collection<String> errors = MetaList.newArrayList("[Compilation errors]");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics().getDiagnostics()) {
                errors.add(diagnostic.toString());
            }
            logDebug(logger(), errors);
            throw new DynamicCompilationException("Aborting: dynamic compilation failed");
        }
        return success;
    }

    private Map<String, byte[]> collectBytecodes(Map<String, String> qualifiedNameAndContent) {
        Map<String, byte[]> bytecodes = MetaMap.newHashMap();
        Map<String, VirtualClassFileObject> classFiles = fileManager().classFiles();
        for (String qualifiedName : classFiles.keySet()) {
            String topClassName = topClassName(qualifiedName);
            if (qualifiedNameAndContent.containsKey(topClassName)) {
                bytecodes.put(qualifiedName, classFiles.get(qualifiedName).byteCodes());
            }
        }
        return bytecodes;
    }

    private String topClassName(String qualifiedName) {
        return qualifiedName.split("[$]")[0];
    }

    private List<String> optionsWithClasspath(URL[] classpath) {
        List<String> options = MetaList.newArrayList(options());
        options.add("-cp");
        options.add(JavaLibrary.asClasspath(classpath));
        return options;
    }

    protected VirtualFileObjectManager fileManager() {
        return fileManager;
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

    private Logger logger() {
        return loggerFor(this);
    }

    private List<String> options;
    private JavaCompiler compiler;
    private VirtualFileObjectManager fileManager;
    private DiagnosticCollector<JavaFileObject> diagnostics;
}