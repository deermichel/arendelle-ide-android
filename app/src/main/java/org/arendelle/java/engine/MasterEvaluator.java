
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MasterEvaluator {
	
	/**
	 * The very main evaluator which runs the given code on the screen
	 * @param code a given Arendelle code
	 * @param screen a given Screen instance
	 */
	public static void evaluate(String code, CodeScreen screen) {
		
		// remove comments
		code = MasterEvaluator.removeComments(code);
		
		// remove spaces for faster performance
		code = MasterEvaluator.removeSpaces(code);
		
		// setting up the spaces
		HashMap<String, String> spaces = new HashMap<String, String>();

		// reset errors
		Reporter.errors = "";
		
		// setting up an Arendelle instance
		Arendelle arendelle = new Arendelle(code);
		
		
		//////////////////
		/// EVALUATING ///
		//////////////////
		
		// running
		Kernel.eval(arendelle, screen, spaces);
		
	}
	
	public static String removeComments(String code) {
		
		String codeWithoutComments = "";
		char command = 0;
		
		// copy whole code without comments
		for (int i = 0; i < code.length(); i++) {
			
			command = code.charAt(i);
			
			if (command == '/') {
				
				command = code.charAt(i + 1);
				
				switch (command) {
				
				case '/':
					
					// skip single-line comment
					while (code.charAt(i) != '\n' && i < code.length() - 1) i++;
					
					break;
					
				case '*':
					
					// skip multi-line comment
					while (!(code.charAt(i) == '*'  && code.charAt(i + 1) == '/') && i < code.length() - 2) i++;
					i++;
					
					break;
					
				default:
					
					// ignore
					codeWithoutComments += '/';
					
					break;
				
				}
				
			} else {
				
				codeWithoutComments += command;
				
			}
			
		}
		
		return codeWithoutComments;
	}
	
	public static String removeSpaces(String code) {

		String codeWithoutSpaces = "";
		
		// copy whole code without spaces
		for (int i = 0; i < code.length(); i++) {
			
			// exclude strings
			if (code.charAt(i) == '\'' || code.charAt(i) == '"') {
				Pattern pattern = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1");
				Matcher matcher = pattern.matcher(code.substring(i));
				if (matcher.find()) {
					codeWithoutSpaces += matcher.group();
					i += matcher.group().length() - 1;
				} else {
					break;
				}
				
			} else if (code.charAt(i) != ' ') {
				codeWithoutSpaces += code.charAt(i);
			}
			
		}
		
		return codeWithoutSpaces;
	}
	
}
