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
package fr.inria.lille.jefix.synth.smt.constraint;

import java.util.List;

import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr.IAsIdentifier;
import org.smtlib.IExpr.IAttribute;
import org.smtlib.IExpr.IAttributedExpr;
import org.smtlib.IExpr.IBinaryLiteral;
import org.smtlib.IExpr.IBinding;
import org.smtlib.IExpr.IDecimal;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IError;
import org.smtlib.IExpr.IExists;
import org.smtlib.IExpr.IFcnExpr;
import org.smtlib.IExpr.IForall;
import org.smtlib.IExpr.IHexLiteral;
import org.smtlib.IExpr.IKeyword;
import org.smtlib.IExpr.ILet;
import org.smtlib.IExpr.INumeral;
import org.smtlib.IExpr.IParameterizedIdentifier;
import org.smtlib.IExpr.IStringLiteral;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ILogic;
import org.smtlib.IResponse;
import org.smtlib.IResponse.IAssertionsResponse;
import org.smtlib.IResponse.IAssignmentResponse;
import org.smtlib.IResponse.IAttributeList;
import org.smtlib.IResponse.IProofResponse;
import org.smtlib.IResponse.IUnsatCoreResponse;
import org.smtlib.IResponse.IValueResponse;
import org.smtlib.ISort.IAbbreviation;
import org.smtlib.ISort.IApplication;
import org.smtlib.ISort.IFamily;
import org.smtlib.ISort.IFcnSort;
import org.smtlib.ISort.IParameter;
import org.smtlib.ITheory;
import org.smtlib.IVisitor;
import org.smtlib.sexpr.ISexpr;
import org.smtlib.sexpr.ISexpr.ISeq;

/**
 * @author Favio D. DeMarco
 *
 */
final class SeqToSexprCollectionVisitor implements IVisitor<List<ISexpr>> {

	@Override
	public List<ISexpr> visit(final IAttribute<?> e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IAttributedExpr e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IBinaryLiteral e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IBinding e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IDecimal e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IError e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final org.smtlib.IResponse.IError e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IExists e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IFcnExpr e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IForall e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IHexLiteral e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IKeyword e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final ILet e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final INumeral e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IDeclaration e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IParameterizedIdentifier e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IAsIdentifier e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IStringLiteral e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final ISymbol e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IScript e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final ICommand e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IFamily s) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IAbbreviation s) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IApplication s) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IFcnSort s) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IParameter s) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final ILogic s) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final ITheory s) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IResponse e) throws org.smtlib.IVisitor.VisitorException {
		if (e instanceof ISeq) {
			return ((ISeq) e).sexprs();
		}
		throw new IllegalStateException("Unknown response class: " + e.getClass() + " value: " + e);
	}

	@Override
	public List<ISexpr> visit(final IAssertionsResponse e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IAssignmentResponse e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IProofResponse e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IValueResponse e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IUnsatCoreResponse e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}

	@Override
	public List<ISexpr> visit(final IAttributeList e) throws org.smtlib.IVisitor.VisitorException {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("Undefined method IVisitor<List<ISexpr>>.visit");
	}
}
