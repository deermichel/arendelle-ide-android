package org.arendelle.java.engine;

import java.util.HashMap;

public class Arrays {

	/** puts a value with an index into a space array
	 * @param value
	 * @param index
	 * @param name
	 * @param spaces
	 */
	public static void put(String value, String index, String name, HashMap<String, String> spaces) {
		
		String rawSpace = spaces.get(name);
		if (rawSpace == null) rawSpace = "0";
		HashMap<String, String> array = getArray(rawSpace);
		for (int i = 0; i < Integer.valueOf(index); i++) if (!array.containsKey(String.valueOf(i))) array.put(String.valueOf(i), "0");
		array.put(index, value);
		rawSpace = getRawSpace(array);
		spaces.put(name, rawSpace);
		
	}
	
	/** splits a raw space to an array
	 * @param rawSpace
	 * @return array
	 */
	public static HashMap<String, String> getArray(String rawSpace) {
		
		HashMap<String, String> array = new HashMap<String, String>();
		for (String value : rawSpace.split(";")) {
			array.put(String.valueOf(array.size()), value);
		}
		
		return array;
	}
	
	/** creates a raw space of an array
	 * @param array
	 * @return rawSpace
	 */
	public static String getRawSpace(HashMap<String, String> array) {
		
		String rawSpace = "";
		for (int i = 0; i < array.size(); i++) {
			rawSpace += array.get(String.valueOf(i)) + ";";
		}
		rawSpace = rawSpace.substring(0, rawSpace.length() - 1);
		
		return rawSpace;
	}
	
}
