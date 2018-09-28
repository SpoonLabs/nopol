package fr.inria.lille.diff;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.RepairType;
import org.apache.commons.io.FileUtils;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.filter.LineFilter;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class PatchGenerator {
	private final Patch patch;
	private final CtElement target;
	private Factory factory;
	private NopolContext nopolContext;
	private String classContent;

	public PatchGenerator(Patch patch, Factory factory, NopolContext nopolContext) {
		this.patch = patch;
		this.factory = factory;
		this.nopolContext = nopolContext;
		this.target = getTarget();
	}

	private CtElement getTarget() {
		CtType type = factory.Type().get(patch.getSourceLocation().getRootClassName());
		EarlyTerminatingScanner<CtElement> targetFinder = new EarlyTerminatingScanner<CtElement>() {
			@Override
			protected void enter(CtElement e) {
				if (e.getPosition() instanceof NoSourcePosition) {
					return;
				}
				if (e.getPosition().getSourceStart() == patch.getSourceLocation().getBeginSource()
						&& e.getPosition().getSourceEnd() == patch.getSourceLocation().getEndSource() && e.isImplicit() == false) {
					if (patch.getType() == RepairType.CONDITIONAL && e instanceof CtIf) {
						setResult(((CtIf) e).getCondition());
					} else {
						setResult(e);
					}
					terminate();
					return;
				}
				if (e.getPosition().getSourceStart() <= patch.getSourceLocation().getBeginSource()
						&& e.getPosition().getSourceEnd() >= patch.getSourceLocation().getEndSource()) {
					super.enter(e);
				}
			}
		};
		type.accept(targetFinder);
		return targetFinder.getResult();
	}

	public String getPatch() {
		if (target == null) {
			// the target is not found
			return "";
		}
		String strPatch = generateStringPatch();
		CtStatement parentLine = getParentLine(target);
		String classContent = getClassContent();

		String[] split = classContent.split("\n");
		Writer output = new Writer("", "");
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			if (i >= parentLine.getPosition().getLine() - 1
					&& i <= parentLine.getPosition().getEndLine() - 1) {
				if (i == parentLine.getPosition().getLine() - 1) {
					output.write(strPatch).line();
				}
			} else {
				output.write(s);
				if (i < split.length - 1) {
					output.line();
				}
			}
		}

		StringReader r1 = new StringReader(classContent);
		String patchedClass = output.toString();
		StringReader r2 = new StringReader(patchedClass);
		String diff = null;
		try {
			String path = computePathForType(target.getParent(CtType.class));
			if (path != null) {

				// a and b are used by Git to distinguish the first and the second path
				// without them we cannot apply the patch apparently
				diff = com.cloudbees.diff.Diff.diff(r1, r2, false)
						.toUnifiedDiff("a" + path,
								"b" + path,
								new StringReader(classContent),
								new StringReader(patchedClass), 1);
			} else {
				return "";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return diff.replaceAll("\n\\\\ No newline at end of file", "");
	}

	private String putFirstSlash(String path) {
		if (path.startsWith("/")) {
			return path;
		} else {
			return "/" + path;
		}
	}

	/**
	 * Compute a relative path according to the given root project path
	 * or according to the input source. If no relative path can be computed
	 * it returns an absolute path.
	 */
	private String computePathForType(CtType type) {
		String path = type.getPosition().getFile().getAbsolutePath();
		String relativePath = null;

		// if root project is specified we use it to compute the relative path
		if (nopolContext.getRootProject() != null) {
			String absolutePath = nopolContext.getRootProject().toAbsolutePath().toString();

			// we compare the absolute path against the root path
			if (path.startsWith(absolutePath)) {

				// then we take only the relative path
				relativePath = path.substring(absolutePath.length());
			}

		// if root project is not specified
		// we get a relative path according to the given input source(s)
		} else {
			File[] inputSources = nopolContext.getProjectSources();
			for (File inputSource : inputSources) {
				// use the absolute path for comparison
				String absolutePath = inputSource.getAbsolutePath();

				// keep the relative (given) path for final output
				String prefixPath = inputSource.getPath();

				// we don't want to keep a "./" in the final path
				if (prefixPath.startsWith("./")) {
					prefixPath = prefixPath.substring(1);
				}
				if (!prefixPath.endsWith("/")) {
					prefixPath += "/";
				}
				if (path.startsWith(absolutePath)) {
					relativePath = prefixPath + path.substring(absolutePath.length() + 1);
					break;
				}
			}
		}

		if (relativePath != null) {
			return this.putFirstSlash(relativePath);
		} else {
			return this.putFirstSlash(path);
		}
	}

	private String generateStringPatch() {
		String classContent = getClassContent();

		Factory factory = target.getFactory();
		factory.getEnvironment().setAutoImports(true);

		String line = getLine();
		CtStatement parentLine = getParentLine(target);
		String currentIndentation = "";

		for (int i = 0; i < line.length(); i++) {
			char s = line.charAt(i);
			if (s == ' ' || s == '\t') {
				currentIndentation += s;
				continue;
			}
			break;
		}
		line = line.trim();

		final String indentation = getIndentation();
		Writer writer = new Writer(currentIndentation, indentation);

		if (patch.getType() == RepairType.PRECONDITION) {
			if (isElseIf(parentLine)) {
				writer.write("} else {").tab();
				line = getSubstring(classContent, parentLine);
			}
			if (parentLine instanceof CtLocalVariable && patch.getType() == RepairType.PRECONDITION) {
				int variableNamePosition = line.indexOf(((CtLocalVariable) parentLine).getSimpleName());
				writer.write(line.substring(0, variableNamePosition));
				writer.write(((CtLocalVariable) parentLine).getSimpleName());
				writer.write(";").line();
			}
			writer.write("if (");
			writer.write(patch.asString());
			writer.write(") {").tab();
			writer.write(writer.addIndentationToString(line)).untab();
			writer.write("}");
			if (isElseIf(parentLine)) {
				writer.untab();
				writer.write("}");
			}
		} else {
			if (isElseIf(parentLine)) {
				writer.write("} else ");
			}
			writer.write(classContent.substring(parentLine.getPosition().getSourceStart(), target.getPosition().getSourceStart()));
			writer.write(patch.asString());
			writer.write(writer.addIndentationToString(classContent.substring(target.getPosition().getSourceEnd() + 1, parentLine.getPosition().getSourceEnd() + 1)));
		}


		return writer.toString();
	}

	private boolean isElseIf(CtStatement parentLine) {
		return isElseIf(parentLine, parentLine);
	}

	private boolean isElseIf(CtStatement original, CtStatement parentLine) {
		if (parentLine.getParent() instanceof CtIf) {
			CtStatement elseStatement = ((CtIf) parentLine.getParent()).getElseStatement();
			if (elseStatement == original) {
				return true;
			} else if (elseStatement instanceof CtBlock) {
				CtBlock block = (CtBlock) elseStatement;
				if (block.isImplicit() && block.getStatement(0) == original) {
					return true;
				}
			}
		}
		if (parentLine.getParent() instanceof CtBlock) {
			return isElseIf(original, (CtStatement) parentLine.getParent());
		}
		return false;
	}

	private String getSubstring(String classContent, CtElement element) {
		SourcePosition position = element.getPosition();
		return classContent.substring(position.getSourceStart(), position.getSourceEnd() + 1);
	}

	private String getIndentation() {
		StringBuilder indentation = new StringBuilder();
		CtElement parentLine = target.getParent(CtMethod.class);
		if (parentLine == null) {
			parentLine = target.getParent(CtConstructor.class);
		}
		CtElement supParentLine = parentLine.getParent(CtType.class);

		String[] split = getClassContent().split("\n");
		String parentFirstLine = split[parentLine.getPosition().getLine() - 1];
		String supParentFirstLine = split[supParentLine.getPosition().getLine() - 1];

		for (int i = 0; i < parentFirstLine.length(); i++) {
			char s = parentFirstLine.charAt(i);
			if (s == ' ' || s == '\t') {
				indentation.append(s);
				continue;
			}
			break;
		}
		for (int i = 0; i < supParentFirstLine.length(); i++) {
			char s = supParentFirstLine.charAt(i);
			if (s == ' ' || s == '\t') {
				indentation.deleteCharAt(0);
				continue;
			}
			break;
		}

		return indentation.toString();
	}

	private String getLine() {
		CtStatement parent = getParentLine(target);
		String[] split = getClassContent().split("\n");
		StringBuilder output = new StringBuilder();

		for (int i = parent.getPosition().getLine() - 1; i < parent.getPosition().getEndLine(); i++) {
			String s = split[i];
			output.append(s);
			output.append("\n");
		}

		return output.toString();
	}

	private CtStatement getParentLine(CtElement element) {
		LineFilter lineFilter = new LineFilter();
		if (element instanceof CtStatement) {
			if (lineFilter.matches((CtStatement) element)) {
				return (CtStatement) element;
			}
		}
		return element.getParent(lineFilter);
	}

	/**
	 * Get the content of the file
	 * @param file
	 * @return
	 */
	private String getFileContent(File file) {
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new RuntimeException("File not found");
		}
	}

	public String getClassContent() {
		if (classContent == null) {
			classContent = getFileContent(target.getPosition().getFile());
		}
		return classContent;
	}
}
