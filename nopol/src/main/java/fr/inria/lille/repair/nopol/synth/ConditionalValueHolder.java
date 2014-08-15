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
package fr.inria.lille.repair.nopol.synth;

import fr.inria.lille.repair.nopol.NoPol;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConditionalValueHolder {

	public static final String VARIABLE_NAME = ConditionalValueHolder.class.getName() + '.' + "booleanValue";
	public static final String ENABLE_CONDITIONAL = ConditionalValueHolder.class.getName() + '.' + "enableConditional[";
	public static int ID_Conditional = 0;
	
	
	
	/**
	 * Optimist...
	 */
	public static volatile boolean booleanValue = true;

	public static boolean enableConditional[];

	
	public static void flip() {
		booleanValue = !booleanValue;
	}

	public static void createEnableConditionalTab() {
		enableConditional = new boolean[ID_Conditional];
		for ( int i = 0 ; i < ID_Conditional ; i++ ){
			enableConditional[i] = false;
		}
	}

	public static void enableNextCondition() {
		for ( int i = 0 ; i < enableConditional.length ; i++ ){
			if ( enableConditional[i] && i < enableConditional.length-1 ){
				enableConditional[i] = false;
				enableConditional[i+1] = true;
				return;
			}
		}
		enableConditional[0] = true;
	}
	
	public static void disableAllCondition() {
		for ( int i = 0 ; i < enableConditional.length ; i++ ){
				enableConditional[i] = false;
		}
	}
	
	public static int getEnableID(){
		if ( !NoPol.isOneBuild() ){
			return 0;
		}
		for ( int i = 0 ; i < enableConditional.length ; i++ ){
			if ( enableConditional[i] ){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 
	 */
	private ConditionalValueHolder() {}



}
