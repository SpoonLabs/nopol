package fr.inria.lille.repair.common;

import java.util.List;

import fr.inria.lille.repair.nopol.patch.Patch;

public interface Fixer {
	List<Patch> repair();
}
