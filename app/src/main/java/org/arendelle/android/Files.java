package org.arendelle.android;

import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/** file operations */
public class Files {

	/** reads text from a given file */
	public static String read(File file) throws IOException {
		
		// create file if it does not exist
		file.createNewFile();
		
		StringBuilder text = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line = reader.readLine()) != null) text.append(line + "\n");
		if (text.length() > 0) text.deleteCharAt(text.length() - 1);
		reader.close();
		
		return text.toString();
	}
	
	/** writes text to the given file */
	public static void write(File file, String text) throws IOException {
		
		FileOutputStream output = new FileOutputStream(file);
		output.write(text.getBytes());
        output.flush();
		output.close();
		
	}
	
	/** parses a given config file */
	public static HashMap<String, String> parseConfigFile(File file) throws IOException {
		
		HashMap<String, String> properties = new HashMap<String, String>();
		String fileContent = Files.read(file);
		String[] lines = fileContent.split("[\r?\n]+");
		String[] property;
		for (String line : lines) {
			property = line.split("=");
			properties.put(property[0].trim(), property[1].trim());
		}
		
		return properties;
	}
	
	/** creates a config file with the given properties */
	public static void createConfigFile(File file, HashMap<String, String> properties) throws IOException {
		
		String fileContent = "";
		for (String key : properties.keySet()) fileContent += key + "=" + properties.get(key) + "\n";
		write(file, fileContent);
		
	}

    /** saves an image to the given file */
    public static void saveImage(File file, Bitmap image) throws IOException {

        FileOutputStream output = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.PNG, 0, output);
        output.flush();
        output.close();

    }

    /** gets all files of a folder and its subfolders */
    public static void getFiles(File folder, ArrayList<File> files) {

        File[] filesList = folder.listFiles();
        for (File file : filesList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                getFiles(file, files);
            }
        }

    }

    /** gets relative path */
    public static String getRelativePath(File root, File path) {
        return path.getAbsolutePath().substring(root.getAbsolutePath().length() + 1);
    }

    /** deletes a file or folder */
    public static void delete(File file) {

        if (file.isDirectory()) for (File child : file.listFiles()) delete(child);
        file.delete();

    }
	
}
