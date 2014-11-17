
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

import org.arendelle.android.Main;
import org.arendelle.android.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

public class Spaces {

	/** replaces all spaces (variables) in the given expression with their values
	 * @param expression
	 * @return The final expression
	 */
	public static String replace(String expression, CodeScreen screen, SortedMap<String, String> spaces) {
		
		for (String name : spaces.keySet()) {
			expression = expression.replaceAll('@' + name, spaces.get(name));
		}
		
		return expression;
	}
	
	/** Spaces kernel which parses and edit spaces
	 * @param arendelle a given Arendelle instance
	 * @param screen
	 * @param spaces
	 */
	public static void parse(Arendelle arendelle, CodeScreen screen, SortedMap<String, String> spaces) {
		
		// determine if it should be a stored space
		if (arendelle.code.charAt(arendelle.i + 1) == '$') {
			StoredSpaces.parse(arendelle, screen, spaces);
			return;
		}
		
		// get name
		String name = "";
		for (int i = arendelle.i + 1; !(arendelle.code.charAt(i) == ',' || arendelle.code.charAt(i) == ')'); i++) {
			name += arendelle.code.charAt(i);
			arendelle.i = i;
		}
		
		// get mathematical expression for condition
		String expression = "";
		int nestedGrammars = 0;
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
			//TODO:String value = JOptionPane.showInputDialog("Sign space '@" + name + "' with a number:");
			//TODO:spaces.put(name, String.valueOf(new Expression(Replacer.replace(value, screen, spaces)).eval().intValue()));
			
		} else if (expression.equals("done")) {
			
			// remove space
			spaces.remove(name);
			return;
			
		} else {
			
			switch(expression.charAt(0)) {
			
			case '"':
				
				// get user input by message
				if (!screen.interactiveMode) {
					Reporter.report("Not running in Interactive Mode!", arendelle.line);
					return;
				}
				//TODO:String value = JOptionPane.showInputDialog(expression.substring(1, expression.length() - 1));
				//TODO:spaces.put(name, String.valueOf(new Expression(Replacer.replace(value, screen, spaces)).eval().intValue()));
				
				break;
				
			case '+':
			case '-':
			case '*':
			case '/':
				// edit space
				spaces.put(name, String.valueOf(new Expression(Replacer.replace(spaces.get(name) + expression.charAt(0) + expression.substring(1), screen, spaces)).eval().intValue()));
				break;
				
			default:
				// create space
				spaces.put(name, String.valueOf(new Expression(Replacer.replace(expression, screen, spaces)).eval().intValue()));
				break;
				
			}
			
		}
		
	}
	
}
