
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

/** Arendelle instance */
public class Arendelle {

	/** Arendelle code */
	public String code;
	
	/** current evaluated char */
	public int i;
	
	/** current evaluated line */
	public int line;
	
	@Override
	public String toString() {
		return code;
	}
	
	/** creates a new Arendelle instance
	 * @param code Arendelle code
	 */
	public Arendelle(String code) {
		
		// set variables
		this.code = code;
		this.i = 0;
		this.line = 1;
		
	}
	
}
