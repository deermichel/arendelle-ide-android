
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

import org.arendelle.android.Files;

import java.io.File;
import java.util.HashMap;

public class Spaces {

	/** replaces all spaces (variables) in the given expression with their values
	 * @param expression
	 * @param screen
	 * @param spaces
	 * @return The final expression
	 */
	public static String replace(String expression, CodeScreen screen, HashMap<String, String> spaces) {

		// copy whole code without spaces
		String expressionWithoutSpaces = "";
		for (int i = 0; i < expression.length(); i++) {
			
			if (expression.charAt(i) == '@') {
				
				i++;
				
				// get name
				String name = "";
				while(!(expression.substring(i, i + 1).matches("[^A-Za-z0-9.]"))) {
					name += expression.charAt(i);
					i++;
					if (i >= expression.length()) break;
				}

				// get index
				if (i < expression.length() && expression.charAt(i) == '[') {
					String index = "";
					int nestedGrammars = 0;
					for (int j = i + 1; !(expression.charAt(j) == ']' && nestedGrammars == 0); j++) {
						index += expression.charAt(j);
						i = j;
						
						if (expression.charAt(j) == '[') {
							nestedGrammars++;
						} else if (expression.charAt(j) == ']') {
							nestedGrammars--;
						}
					}
					index = String.valueOf(new Expression(Replacer.replace(index, screen, spaces)).eval().intValue());
					expressionWithoutSpaces += Arrays.getArray(spaces.get(name)).get(index);
					i++;
				}
				
				// or count items
				else if (i < expression.length() && expression.charAt(i) == '?') {
					expressionWithoutSpaces += String.valueOf(Arrays.getArray(spaces.get(name)).size());
				}
				
				// or return index = 0
				else {
					expressionWithoutSpaces += Arrays.getArray(spaces.get(name)).get("0");
					i--;
				}
				
			} else {
				expressionWithoutSpaces += expression.charAt(i);
			}
			
		}
		expression = expressionWithoutSpaces;
		
		return expression;
	}
	
	/** Spaces kernel which parses and edit spaces
	 * @param arendelle a given Arendelle instance
	 * @param screen
	 * @param spaces
	 */
	public static void parse(Arendelle arendelle, CodeScreen screen, HashMap<String, String> spaces) {
		
		// determine if it should be a stored space
		if (arendelle.code.charAt(arendelle.i + 1) == '$') {
			StoredSpaces.parse(arendelle, screen, spaces);
			return;
		}
		
		// get name
		String name = "";
		for (int i = arendelle.i + 1; !(arendelle.code.charAt(i) == ',' || arendelle.code.charAt(i) == ')' || arendelle.code.charAt(i) == '['); i++) {
			name += arendelle.code.charAt(i);
			arendelle.i = i;
		}
		
		// get index for array
		String index = "";
		boolean explicitIndex = true;
		int nestedGrammars = 0;
		if (arendelle.code.charAt(arendelle.i + 1) == '[') {
			for (int i = arendelle.i + 2; !(arendelle.code.charAt(i) == ']' && nestedGrammars == 0); i++) {
				index += arendelle.code.charAt(i);
				arendelle.i = i;
				
				if (arendelle.code.charAt(i) == '[') {
					nestedGrammars++;
				} else if (arendelle.code.charAt(i) == ']') {
					nestedGrammars--;
				}
			}
			index = String.valueOf(new Expression(Replacer.replace(index, screen, spaces)).eval().intValue());
			arendelle.i++;
		} else {
			explicitIndex = false;
			index = "0";
		}
		
		// get mathematical expression for condition
		String expression = "";
		if (arendelle.code.charAt(arendelle.i + 1) == ',') {
			for (int i = arendelle.i + 2; !(arendelle.code.charAt(i) == ')' && nestedGrammars == 0); i++) {
				expression += arendelle.code.charAt(i);
				arendelle.i = i;
				
				if (arendelle.code.charAt(i) == '[' || arendelle.code.charAt(i) == '{' || arendelle.code.charAt(i) == '(') {
					nestedGrammars++;
				} else if (arendelle.code.charAt(i) == ']' || arendelle.code.charAt(i) == '}' || arendelle.code.charAt(i) == ')') {
					nestedGrammars--;
				}
				
			}
		}
		
		arendelle.i++;
		
		// determine action
		if (expression == "") {
			
			// get user input
			if (!screen.interactiveMode) {
				Reporter.report("Not running in Interactive Mode!", arendelle.line);
				return;
			}
			
		} else if (expression.equals("done")) {
			
			// remove space
			spaces.remove(name);
			
		} else if(!explicitIndex && expression.charAt(0) == '@' && spaces.containsKey(expression.substring(1))) {
			
			// create space array from another space array
			HashMap<String, String> array = Arrays.getArray(name);
			array.putAll(Arrays.getArray(spaces.get(expression.substring(1))));
			spaces.put(name, Arrays.getRawSpace(array));
			
		} else if(!explicitIndex && expression.charAt(0) == '$' && new File(screen.mainPath + "/" + expression.substring(1).replace('.', '/') + ".space").exists()) {
			
			// try to create space array from a stored space array
			try {
				HashMap<String, String> array = Arrays.getArray(name);
				array.putAll(Arrays.getArray(Files.read(new File(screen.mainPath + "/" + expression.substring(1).replace('.', '/') + ".space"))));
				spaces.put(name, Arrays.getRawSpace(array));
			} catch (Exception e) {
				Reporter.report(e.toString(), arendelle.line);
			}
			
		} else {
			
			switch(expression.charAt(0)) {
			
			case '"':
				
				// get user input by message
				if (!screen.interactiveMode) {
					Reporter.report("Not running in Interactive Mode!", arendelle.line);
					return;
				}

				break;
				
			case '+':
			case '-':
			case '*':
			case '/':
				// edit space
				Arrays.put(String.valueOf(new Expression(Replacer.replace(Arrays.getArray(spaces.get(name)).get(index) + expression.charAt(0) + expression.substring(1), screen, spaces)).eval().intValue()), index, name, spaces);
				break;
				
			default:
				// create space
				Arrays.put(String.valueOf(new Expression(Replacer.replace(expression, screen, spaces)).eval().intValue()), index, name, spaces);
				break;
				
			}
			
		}
		
	}
	
}
