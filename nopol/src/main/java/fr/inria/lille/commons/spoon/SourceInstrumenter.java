package fr.inria.lille.commons.spoon;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import spoon.processing.Processor;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.io.ProjectReference;

public class SourceInstrumenter {

	public SourceInstrumenter(File sourceFile, URL[] classpath) {
		this(new ProjectReference(sourceFile, classpath));
	}
	
	public SourceInstrumenter(ProjectReference project) {
		this.project = project;
	}
	
	public Map<String, Class<?>> instrumentedWith(Processor<?> processor) {
		return instrumentedWith((Collection) ListLibrary.newLinkedList(processor));
	}
	
	public Map<String, Class<?>> instrumentedWith(Collection<Processor<?>> processors) {
		SpoonClassLoader spoonLoader = spoonLoader(processors);
		String[] classesToBeProcessed = spoonLoader.modelledClassesNames().toArray(new String[0]);
		return loadedClasses(spoonLoader, classesToBeProcessed);
	}
	
	public Map<String, Class<?>> instrumentedWith(Processor<?> processor, String... classesToBeProcessed) {
		return instrumentedWith((Collection) ListLibrary.newLinkedList(processor), classesToBeProcessed);
	}
	
	public Map<String, Class<?>> instrumentedWith(Collection<Processor<?>> processors, String... classesToBeProcessed) {
		return loadedClasses(spoonLoader(processors), classesToBeProcessed);
	}
	
	private Map<String, Class<?>> loadedClasses(SpoonClassLoader spoonLoader, String... classesToBeProcessed) {
		Map<String, Class<?>> loadedClasses = MapLibrary.newHashMap();
		for (String classToBeProcessed : classesToBeProcessed) {
			try {
				Class<?> loadedClass = spoonLoader.loadClass(classToBeProcessed);
				loadedClasses.put(classToBeProcessed, loadedClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return loadedClasses;
	}
	
	private SpoonClassLoader spoonLoader(Collection<Processor<?>> processors) {
		return new SpoonClassLoader(project().sourceFile(), project().classpath(), processors);
	}
	
	protected ProjectReference project() {
		return project;
	}
	
	private ProjectReference project;
}
