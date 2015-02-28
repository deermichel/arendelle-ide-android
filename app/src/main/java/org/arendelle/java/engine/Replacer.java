
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

public class Replacer {

	/** replaces all placeholders in the given expression with their values
	 * @param expression
	 * @param screen
	 * @param spaces
	 * @return The final expression
	 */
	public static String replace(String expression, CodeScreen screen, HashMap<String, String> spaces) {

		expression = Replacer.replaceFunctions(expression, screen, spaces);
		expression = Sources.replace(expression, screen);
		expression = Spaces.replace(expression, screen, spaces);
		expression = StoredSpaces.replace(expression, screen, spaces);
		
		expression = Replacer.catchErrors(expression);
		
		return expression;
	}
	
	/** replaces all functions in the given expression with their return values
	 * @param expression
	 * @param screen
	 * @param spaces
	 * @return The final expression
	 */
	public static String replaceFunctions(String expression, CodeScreen screen, HashMap<String, String> spaces) {

		// copy whole code without functions
		String expressionWithoutFunctions = "";
		for (int i = 0; i < expression.length(); i++) {
			
			if (expression.charAt(i) == '!') {
				
				String funcExpression = "";
				int nestedGrammars = 0;
				
				// get function expression
				while (!(expression.charAt(i) == ')' && nestedGrammars == 1)) {
					funcExpression += expression.charAt(i);
					
					if (expression.charAt(i) == '[' || expression.charAt(i) == '{' || expression.charAt(i) == '(') {
						nestedGrammars++;
					} else if (expression.charAt(i) == ']' || expression.charAt(i) == '}' || expression.charAt(i) == ')') {
						nestedGrammars--;
					}

					i++;
					if (i >= expression.length()) break;
				}
				funcExpression += expression.charAt(i);
				
				// get index
				String index = "";
				if (i < expression.length() - 1 && expression.charAt(i + 1) == '[') {
					nestedGrammars = 0;
					for (int j = i + 2; !(expression.charAt(j) == ']' && nestedGrammars == 0); j++) {
						index += expression.charAt(j);
						i = j;
						
						if (expression.charAt(j) == '[') {
							nestedGrammars++;
						} else if (expression.charAt(j) == ']') {
							nestedGrammars--;
						}
					}
					index = String.valueOf(new Expression(Replacer.replace(index, screen, spaces)).eval().intValue());
					i++;
				} else {
					index = "0";
				}
				
				// setup temporarely Arendelle instance and run the function expression
				Arendelle tempArendelle = new Arendelle(funcExpression);
				expressionWithoutFunctions += Arrays.getArray(FunctionParser.parse(tempArendelle, screen, spaces)).get(index);
				
			} else {
				expressionWithoutFunctions += expression.charAt(i);
			}
			
		}
		
		return expressionWithoutFunctions;
	}
	
	/** catches all errors in the given expression
	 * @param expression
	 * @return The final expression
	 */
	public static String catchErrors(String expression) {
		
		// copy whole code without errors
		String expressionWithoutErrors = "";
		for (int i = 0; i < expression.length(); i++) {
			
			if (expression.charAt(i) == '@') {

				i++;

				String name = "";
				switch (expression.charAt(i - 1)) {

				// get unsigned spaces
				case '@':
					
					while(!(expression.substring(i, i + 1).matches("[^A-Za-z0-9]"))) {
						name += expression.charAt(i);
						i++;
						if (i >= expression.length()) break;
					}

					Reporter.report("Unsigned space '@" + name + "' found.", -1);
					
					break;
				
				}
				
				i--;
				expressionWithoutErrors += "0";
				
			} else {
				expressionWithoutErrors += expression.charAt(i);
			}
			
		}
		
		return expressionWithoutErrors;
	}
	
	/** replaces all placeholders in the given string with their values
	 * @param string
	 * @param screen
	 * @param spaces
	 * @return The final title
	 */
	public static String replaceInString(String string, CodeScreen screen, HashMap<String, String> spaces) {

		// copy whole code without placeholders
		String expressionWithoutPlaceholders = "";
		for (int i = 0; i < string.length(); i++) {
			
			if (string.charAt(i) == '\\' && i < string.length() - 2 && string.charAt(i + 1) == '(') {
				
				// get placeholder without spaces
				String placeholder = "";
				int nestedGrammars = 0;
				for (int j = i + 2; !(string.charAt(j) == ')' && nestedGrammars == 0); j++) {
					if (string.charAt(j) != ' ') placeholder += string.charAt(j);
					i = j;
					
					if (string.charAt(j) == '[' || string.charAt(j) == '(' || string.charAt(j) == '{') {
						nestedGrammars++;
					} else if (string.charAt(j) == ']' || string.charAt(j) == ')' || string.charAt(j) == '}') {
						nestedGrammars--;
					}
					
				}
				i++;
				
				// evaluate placeholder
				expressionWithoutPlaceholders += new Expression(Replacer.replace(placeholder, screen, spaces)).eval().toPlainString();
				
			} else if (i < string.length() - 1 && string.substring(i, i + 2).equals("\\\"")) {
				expressionWithoutPlaceholders += "\"";
				i++;
			} else if (i < string.length() - 1 && string.substring(i, i + 2).equals("\\'")) {
				expressionWithoutPlaceholders += "'";
				i++;
			} else {
				expressionWithoutPlaceholders += string.charAt(i);
			}
			
		}
		
		return expressionWithoutPlaceholders;
	}
	
}
