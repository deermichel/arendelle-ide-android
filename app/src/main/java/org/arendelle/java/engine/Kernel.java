
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

public class Kernel {

	/**
	 * Arendelles kernel which evaluates the given code
	 * @param arendelle a given Arendelle instance
	 * @param screen
	 * @param spaces
	 */
	public static void eval(Arendelle arendelle, CodeScreen screen, HashMap<String, String> spaces) {
		
		/*
		 *  So what we do is we read the code char-by-char and run the commands
		 *  Just-In-Time. If we find a grammer we let grammer parsers take care
		 *  of that.
		 */
		
		char command = 0;
		
		while (arendelle.i < arendelle.code.length()) {
			
			// get command and replace upper-case letters with the lower-case one
			command = arendelle.code.toLowerCase().charAt(arendelle.i);
			
			
			////////////////////////
			/// START OF RUNTIME ///
			////////////////////////
			
			switch (command) {
			
			//////////////////////////////////////////////////
			///                  Grammars                  ///
			//////////////////////////////////////////////////
			
			case '[':
				LoopParser.parse(arendelle, screen, spaces);
				break;
				
			case '!':
				FunctionParser.parse(arendelle, screen, spaces);
				break;
				
			case '(':
				Spaces.parse(arendelle, screen, spaces);
				break;
				
			case '{':
				ConditionParser.parse(arendelle, screen, spaces);
				break;
				
			case '\'':
				// get and set screen title
				String title = "";
				for (int i = arendelle.i + 1; !(arendelle.code.charAt(i) == '\'' && arendelle.code.charAt(i - 1) != '\\'); i++) {
					if (arendelle.code.charAt(i) == '\\') continue;
					title += arendelle.code.charAt(i);
					arendelle.i = i;
				}
				screen.title = Replacer.replace(title, screen, spaces);
				arendelle.i++;
				break;
			
				
			//////////////////////////////////////////////////
			///                  Commands                  ///
			//////////////////////////////////////////////////
				
			case 'p':
				if (screen.x >= 0 && screen.y >= 0 && screen.x < screen.width && screen.y < screen.height) {
					screen.screen[screen.x][screen.y] = screen.color + 1;
				}
				break;
				
			case 'u':
				screen.y--;
				break;
				
			case 'd':
				screen.y++;
				break;
				
			case 'r':
				screen.x++;
				break;
				
			case 'l':
				screen.x--;
				break;
				
			case 'e':
				LoopParser.breakLoop = true;
				break;
				
			case 'n':
				screen.color = (screen.color + 1) % 4;
				break;
				
			case 'c':
				if (screen.x >= 0 && screen.y >= 0 && screen.x < screen.width && screen.y < screen.height) {
					screen.screen[screen.x][screen.y] = 0;
				}
				break;
				
			case 'w':
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					Reporter.report(e.toString(), arendelle.line);
				}
				break;
				
			case 's':
				// wait until the user presses a key
			    if (!screen.interactiveMode) {
			    	Reporter.report("Not running in Interactive Mode!", arendelle.line);
			    	break;
			    }
				break;
				
			case 'i':
				screen.x = 0;
				screen.y = 0;
				break;
				
				
			/////////////////////////////////////////////////////
			///               Errors and Others               ///
			/////////////////////////////////////////////////////
				
			case ']':
				Reporter.report("Unexpected loop token ']' found.", arendelle.line);
				break;
				
			case ')':
				Reporter.report("Unexpected variable token ')' found.", arendelle.line);
				break;
				
			case '}':
				Reporter.report("Unexpected condition token '}' found.", arendelle.line);
				break;
				
			case '<':
				Reporter.report("Unexpected function header found.", arendelle.line);
				break;
				
			case '>':
				Reporter.report("Unexpected function header token '>' found.", arendelle.line);
				break;
				
			case ',':
				Reporter.report("Unexpected grammar divider ',' found.", arendelle.line);
				break;
				
			case '\n':
				arendelle.line++;
				break;
				
			default:
				if (command != ' ' && command != ';' && command != '\t' && command != '\r') {
					Reporter.report("Unknown command: '" + command + "'", arendelle.line);
					break;
				}
				
			}
			
			//////////////////////
			/// END OF RUNTIME ///
			//////////////////////

			arendelle.i++;
			
		}
		
	}
	
}
