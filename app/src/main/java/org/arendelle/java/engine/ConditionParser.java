
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

import java.util.SortedMap;
import java.util.TreeMap;

public class ConditionParser {

	/** ConditionParser kernel which parses and runs a condition
	 * @param arendelle a given Arendelle instance
	 * @param screen
	 * @param spaces
	 */
	public static void parse(Arendelle arendelle, CodeScreen screen, SortedMap<String, String> spaces) {
		
		// get mathematical expression for condition
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
		
		// get code which will be run if condition is true
		String trueCode = "";
		for (int i = arendelle.i + 2; !((arendelle.code.charAt(i) == '}' || arendelle.code.charAt(i) == ',') && nestedGrammars == 0); i++) {
			trueCode += arendelle.code.charAt(i);
			arendelle.i = i;
			
			if (arendelle.code.charAt(i) == '[' || arendelle.code.charAt(i) == '{' || arendelle.code.charAt(i) == '(') {
				nestedGrammars++;
			} else if (arendelle.code.charAt(i) == ']' || arendelle.code.charAt(i) == '}' || arendelle.code.charAt(i) == ')') {
				nestedGrammars--;
			}
			
		}
		
		// get code which will be run if condition is false
		String falseCode = "";
		if (arendelle.code.charAt(arendelle.i + 1) == ',') {
			for (int i = arendelle.i + 2; !(arendelle.code.charAt(i) == '}' && nestedGrammars == 0); i++) {
				falseCode += arendelle.code.charAt(i);
				arendelle.i = i;
				
				if (arendelle.code.charAt(i) == '[' || arendelle.code.charAt(i) == '{' || arendelle.code.charAt(i) == '(') {
					nestedGrammars++;
				} else if (arendelle.code.charAt(i) == ']' || arendelle.code.charAt(i) == '}' || arendelle.code.charAt(i) == ')') {
					nestedGrammars--;
				}
				
			}
		}
		
		arendelle.i++;
		
		// create Arendelle instances and temporarely spaces
		Arendelle trueArendelle = new Arendelle(trueCode);
		Arendelle falseArendelle = new Arendelle(falseCode);
		SortedMap<String, String> conditionSpaces = new TreeMap<String, String>(spaces.comparator());
		conditionSpaces.putAll(spaces);
		
		// run the condition
		if (new Expression(Replacer.replace(expression, screen, conditionSpaces)).eval().intValue() != 0) {
			Kernel.eval(trueArendelle, screen, conditionSpaces);
		} else {
			Kernel.eval(falseArendelle, screen, conditionSpaces);
		}
		
		// remove spaces created while condition was running
		for (String name : spaces.keySet()) {
			spaces.put(name, conditionSpaces.get(name));
		}
		
	}
	
}
