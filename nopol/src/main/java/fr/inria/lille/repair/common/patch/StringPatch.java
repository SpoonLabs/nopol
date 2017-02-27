/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.repair.common.patch;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.diff.PatchGenerator;
import spoon.reflect.factory.Factory;

import java.io.File;

/**
 * @author Favio D. DeMarco
 */
public class StringPatch implements Patch {

    private static final long serialVersionUID = 1150517609100930111L;
    private final SourceLocation location;
    private final String repair;
    private final StatementType type;

    /**
     * @param repair
     * @param location
     * @param type
     */
    public StringPatch(final String repair, final SourceLocation location, final StatementType type) {
        this.repair = repair;
        this.location = location;
        this.type = type;
    }

    /**
     * @see Patch#asString()
     */
    @Override
    public String asString() {
        return repair;
    }

    /**
     * @return the containingClassName
     */
    @Override
    public String getRootClassName() {
        return location.getRootClassName();
    }

    /**
     * @see Patch#getFile(File)
     */
    @Override
    public File getFile(final File sourceFolder) {
        return location.getSourceFile(sourceFolder);
    }

    /**
     * @see Patch#getLineNumber()
     */
    @Override
    public int getLineNumber() {
        return location.getLineNumber();
    }

    @Override
    public StatementType getType() {
        return type;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return this.location;
    }

    @Override
    public String toDiff(Factory spoon, NopolContext nopolContext) {
        return new PatchGenerator(this, spoon, nopolContext).getPatch();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s:%d: %s %s", location.getContainingClassName(), getLineNumber(), type, repair);
    }
}
