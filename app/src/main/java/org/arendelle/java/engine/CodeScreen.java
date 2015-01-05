
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

import java.util.Random;

/** screen instance */
public class CodeScreen {
	
	/** current x coordinate */
	public int x;
	
	/** current y coordinate */
	public int y;
	
	/** current z coordinate */
	public int z;
	
	/** width of the screen */
	public int width;
	
	/** height of the screen */
	public int height;
	
	/** depth of the screen */
	public int depth;
	
	/** current color */
	public int color;
	
	/** randomizer */
	public Random rand;
	
	/** 2d array representing the current screen */
	public int screen[][];
	
	/** current screen title */
	public String title;
	
	/** path of the main class */
	public String mainPath;
	
	/** user interaction mode */
	public boolean interactiveMode;
	
	
	/** creates a new screen instance
	 * @param width width of the screen
	 * @param height height of the screen
	 * @param mainPath path of the main class
	 * @param interactiveMode user interaction mode
	 */
	public CodeScreen(int width, int height, String mainPath, boolean interactiveMode) {
		
		// set variables
		this.x = 0;
		this.y = 0;
		this.z = 0;
		
		this.width = width;
		this.height = height;
		this.depth = 0;
		
		this.color = 0;
		
		this.rand = new Random();
		
		this.screen = new int[width][height];
		this.title = "";
		
		this.mainPath = mainPath;
		
		this.interactiveMode = interactiveMode;
		
	}
	
}
