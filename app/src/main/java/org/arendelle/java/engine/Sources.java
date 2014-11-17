
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

public class Sources {

	/** replaces all sources in the given expression with their values
	 * @param expression
	 * @return The final expression
	 */
	public static String replace(String expression, CodeScreen screen) {
		
		expression = expression.replaceAll("#x", String.valueOf(screen.x));
		expression = expression.replaceAll("#y", String.valueOf(screen.y));
		expression = expression.replaceAll("#z", String.valueOf(screen.z));
		expression = expression.replaceAll("#pi", String.valueOf(Math.PI));
		expression = expression.replaceAll("#i", String.valueOf(screen.width));
		expression = expression.replaceAll("#width", String.valueOf(screen.width));
		expression = expression.replaceAll("#j", String.valueOf(screen.height));
		expression = expression.replaceAll("#height", String.valueOf(screen.height));
		expression = expression.replaceAll("#k", String.valueOf(screen.z));
		expression = expression.replaceAll("#depth", String.valueOf(screen.depth));
		expression = expression.replaceAll("#n", String.valueOf(screen.color));
		expression = expression.replaceAll("#rnd", Sources.RNDGenerator(screen));
		
		return expression;
	}
	
	/** generates a random number in the format of '0.XXXXX'
	 * @param screen
	 * @return The generated number
	 */
	public static String RNDGenerator(CodeScreen screen) {
		
		String number = "0.";
		
		for (int i = 0; i < 5; i++) number += String.valueOf(screen.rand.nextInt(10));
		
		return number;
	}
	
}
