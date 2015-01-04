
//
//  JArendelle - Java Portation of the Arendelle Language
//  Copyright (c) 2014 Micha Hanselmann <h@arendelle.org>
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//

package org.arendelle.java.engine;

import java.util.HashMap;

public class LoopParser {
	
	/** timout of while-loops (in milliseconds) */
	public static final int TIMEOUT = 2000;
	
	/** if true the actual loop will be aborted */
	public static boolean breakLoop = false;
	

	/** LoopParser kernel which parses and runs a loop
	 * @param arendelle a given Arendelle instance
	 * @param screen 
	 * @param spaces
	 */
	public static void parse(Arendelle arendelle, CodeScreen screen, HashMap<String, String> spaces) {
		
		// get mathematical expression for loop
		String expression = "";
		int nestedGrammars = 0;
		for (int i = arendelle.i + 1; !(arendelle.code.charAt(i) == ',' && nestedGrammars == 0); i++) {
			expression += arendelle.code.charAt(i);
			arendelle.i = i;
			
			if (arendelle.code.charAt(i) == '[' || arendelle.code.charAt(i) == '{' || arendelle.code.charAt(i) == '(') {
				nestedGrammars++;
			} else if (arendelle.code.charAt(i) == ']' || arendelle.code.charAt(i) == '}' || arendelle.code.charAt(i) == ')') {
				nestedGrammars--;
			}
			
		}
		
		// get loop code
		String loopCode = "";
		for (int i = arendelle.i + 2; !(arendelle.code.charAt(i) == ']' && nestedGrammars == 0); i++) {
			loopCode += arendelle.code.charAt(i);
			arendelle.i = i;
			
			if (arendelle.code.charAt(i) == '[' || arendelle.code.charAt(i) == '{' || arendelle.code.charAt(i) == '(') {
				nestedGrammars++;
			} else if (arendelle.code.charAt(i) == ']' || arendelle.code.charAt(i) == '}' || arendelle.code.charAt(i) == ')') {
				nestedGrammars--;
			}
			
		}
		
		arendelle.i++;
		
		// create Arendelle instances and temporarely spaces
		Arendelle loopArendelle = new Arendelle(loopCode);
		HashMap<String, String> loopSpaces = new HashMap<String, String>();
		loopSpaces.putAll(spaces);

		// fix for floOR error
		expression = expression.toLowerCase();
		expression = expression.replaceAll("floor", "floo_r");
		
		// determine if expression is a number (for-loop) or a boolean (while-loop)
		if (expression.contains("=") || expression.contains("<") || expression.contains(">") || expression.contains("true") || 
				expression.contains("false") || expression.contains("and") || expression.contains("or") || expression.contains("not")) {
			
			// run the while loop
			long timestamp = System.currentTimeMillis();
			while (new Expression(Replacer.replace(expression, screen, loopSpaces)).eval().intValue() != 0) {
				loopArendelle.i = 0;
				Kernel.eval(loopArendelle, screen, loopSpaces);
				if (!screen.interactiveMode && System.currentTimeMillis() - timestamp > TIMEOUT) {
					Reporter.report("While timeout expired.", arendelle.line);
					return;
				}
				if (breakLoop) break;
			}
			
		} else {
			
			// run the for loop
			int loopTimes = new Expression(Replacer.replace(expression, screen, loopSpaces)).eval().intValue();
			for (int i = 0; i < loopTimes; i++) {
				loopArendelle.i = 0;
				Kernel.eval(loopArendelle, screen, loopSpaces);
				if (breakLoop) break;
			}
			
		}
		
		// reset break sign
		breakLoop = false;
		
		// remove spaces created while loop was running
		for (String name : spaces.keySet()) {
			spaces.put(name, loopSpaces.get(name));
		}
		
	}
	
}
