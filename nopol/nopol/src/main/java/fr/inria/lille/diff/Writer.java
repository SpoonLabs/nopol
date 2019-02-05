package fr.inria.lille.diff;

import spoon.reflect.declaration.CtElement;

public class Writer {
	private StringBuilder content = new StringBuilder();
	String currentIndentation = "";
	private final String indentation;

	Writer(String indentationInit, String indentation) {
		this.currentIndentation = indentationInit;
		this.indentation = indentation;
		this.write(indentationInit);
	}

	public Writer write(CtElement element) {
		this.content.append(element.toString());
		return this;
	}

	public Writer write(String content) {
		this.content.append(content);
		return this;
	}

	public Writer write(char content) {
		this.content.append(content);
		return this;
	}

	public Writer tab() {
		currentIndentation += indentation;
		return this.line();
	}

	public Writer line() {
		this.write("\n");
		return this.write(currentIndentation);
	}

	public Writer untab() {
		currentIndentation = currentIndentation.substring(0, currentIndentation.length() - indentation.length());
		return this.line();
	}

	public String addIndentationToString(String content) {
		Writer sb = new Writer(currentIndentation, indentation);
		String[] split = content.split("\n");
		for (int i = 0; i < split.length; i++) {
			String s = split[i].trim();
			String next = null;
			if (i < split.length - 1) {
				next = split[i + 1].trim();
			}
			sb.write(s);
			// remove comment at the end of the line
			if (s.contains("//")) {
				s = s.substring(0, s.indexOf("//")).trim();
			}
			if (s.endsWith("{")) {
				sb.tab();
			} else if (next != null && next.startsWith("}")) {
				sb.untab();
			} else if (next != null) {
				sb.line();
			}
		}
		return sb.toString().trim();
	}

	@Override
	public String toString() {
		return content.toString();
	}
}