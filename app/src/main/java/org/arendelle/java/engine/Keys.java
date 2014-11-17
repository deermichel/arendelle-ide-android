
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


public class Keys {
	
	// supported keys
	static boolean left = false;
	static boolean right = false;
	static boolean up = false;
	static boolean down = false;
	static boolean any = false;
	
	
	/** replaces all keys in the given expression with their state (pressed = true and not pressed = false)
	 * @param expression
	 * @return The final expression
	 */
	public static String replace(String expression, CodeScreen screen) {
		
		// only interact with keys in Interactive Mode
		if (expression.contains("&") && !screen.interactiveMode) {
			Reporter.report("Not running in Interactive Mode!", -1);
			return expression;
		}
		
		// replace key placeholders with their state
		expression = expression.replaceAll("&left", String.valueOf(left));
		expression = expression.replaceAll("&right", String.valueOf(right));
		expression = expression.replaceAll("&up", String.valueOf(up));
		expression = expression.replaceAll("&down", String.valueOf(down));
		
		return expression;
	}
	
	//TODO:/** initalizes the key listener */
	/*public static void init() {
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				
				any = (e.getID() == KeyEvent.KEY_PRESSED) ? true : false;
				left = ((e.getKeyCode() == KeyEvent.VK_LEFT || Character.toLowerCase(e.getKeyChar()) == 'a') && e.getID() == KeyEvent.KEY_PRESSED) ? true : false;
				right = ((e.getKeyCode() == KeyEvent.VK_RIGHT || Character.toLowerCase(e.getKeyChar()) == 'd') && e.getID() == KeyEvent.KEY_PRESSED) ? true : false;
				up = ((e.getKeyCode() == KeyEvent.VK_UP || Character.toLowerCase(e.getKeyChar()) == 'w') && e.getID() == KeyEvent.KEY_PRESSED) ? true : false;
				down = ((e.getKeyCode() == KeyEvent.VK_DOWN || Character.toLowerCase(e.getKeyChar()) == 's') && e.getID() == KeyEvent.KEY_PRESSED) ? true : false;
				
				return false;
			}   
			
		});
		
	}*/
	
}