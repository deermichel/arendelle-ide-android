
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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class StoredSpaces {

	/** replaces all stored spaces (stored variables) in the given expression with their values
	 * @param expression
	 * @param screen
	 * @param spaces
	 * @return The final expression
	 */
	public static String replace(String expression, CodeScreen screen, HashMap<String, String> spaces) {
		
		// copy whole code without stored spaces
		String expressionWithoutStoredSpaces = "";
		for (int i = 0; i < expression.length(); i++) {
			
			if (expression.charAt(i) == '$') {
				
				i++;
				
				// get name
				String name = "";
				while(!(expression.substring(i, i + 1).matches("[^A-Za-z0-9.]"))) {
					name += expression.charAt(i);
					i++;
					if (i >= expression.length()) break;
				}
				
				// get path
				String storedSpacePath = screen.mainPath + "/" + name.replace('.', '/') + ".space";
				
				// read stored space
				String rawStoredSpace = "";
				try {
					rawStoredSpace = Files.read(new File(storedSpacePath));
				} catch (Exception e) {
					Reporter.report("No stored space as '$" + name + "' found.", -1);
					expressionWithoutStoredSpaces += "0";
					i--;
					continue;
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
					expressionWithoutStoredSpaces += Arrays.getArray(rawStoredSpace).get(index);
					i++;
				}
				
				// or count items
				else if (i < expression.length() && expression.charAt(i) == '?') {
					expressionWithoutStoredSpaces += String.valueOf(Arrays.getArray(rawStoredSpace).size());
				}
				
				// or return index = 0
				else {
					expressionWithoutStoredSpaces += Arrays.getArray(rawStoredSpace).get("0");
					i--;
				}
				
			} else {
				expressionWithoutStoredSpaces += expression.charAt(i);
			}
			
		}
		expression = expressionWithoutStoredSpaces;
		
		return expression;
	}
	
	/** StoredSpaces kernel which parses and edit stored spaces
	 * @param arendelle a given Arendelle instance
	 * @param screen
	 * @param spaces
	 */
	public static void parse(Arendelle arendelle, CodeScreen screen, HashMap<String, String> spaces) {
		
		// get name
		String name = "";
		for (int i = arendelle.i + 2; !(arendelle.code.charAt(i) == ',' || arendelle.code.charAt(i) == ')' || arendelle.code.charAt(i) == '['); i++) {
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
		
		// get path
		String storedSpacePath = screen.mainPath + "/" + name.replace('.', '/') + ".space";
		
		// create array (or get existing) of stored space
		HashMap<String, String> array = new HashMap<String, String>();
		try {
			if (new File(storedSpacePath).exists()) array = Arrays.getArray(Files.read(new File(storedSpacePath)));
		} catch (Exception e) {
			Reporter.report(e.toString(), arendelle.line);
		}
		for (int i = 0; i < Integer.valueOf(index); i++) if (!array.containsKey(String.valueOf(i))) array.put(String.valueOf(i), "0");
		
		// determine action
		if (expression == "") {
			
			// get user input
			if (!screen.interactiveMode) {
				Reporter.report("Not running in Interactive Mode!", arendelle.line);
				return;
			}
			
		} else if (expression.equals("done")) {
			
			// delete stored space
			new File(storedSpacePath).delete();
			return;
			
		} else if(!explicitIndex && expression.charAt(0) == '@' && spaces.containsKey(expression.substring(1))) {
			
			// create stored space array from a space array
			array.putAll(Arrays.getArray(spaces.get(expression.substring(1))));
			
		} else if(!explicitIndex && expression.charAt(0) == '$' && new File(screen.mainPath + "/" + expression.substring(1).replace('.', '/') + ".space").exists()) {
			
			// try to create stored space array from another stored space array
			try {
				array.putAll(Arrays.getArray(Files.read(new File(screen.mainPath + "/" + expression.substring(1).replace('.', '/') + ".space"))));
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
				// edit stored space
				array.put(index, String.valueOf(new Expression(Replacer.replace(array.get(index) + expression.charAt(0) + expression.substring(1), screen, spaces)).eval().intValue()));
				break;
				
			default:
				// create stored space
				array.put(index, String.valueOf(new Expression(Replacer.replace(expression, screen, spaces)).eval().intValue()));
				break;
				
			}
			
		}
		
		// save stored space
		try {
			File storedSpace = new File(storedSpacePath);
			storedSpace.getParentFile().mkdirs();
			Files.write(storedSpace, Arrays.getRawSpace(array));
		} catch (Exception e) {
			Reporter.report(e.toString(), arendelle.line);
		}
		
	}
	
}
