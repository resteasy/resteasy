/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.io;

import se.unlogic.standardutils.callback.Callback;
import se.unlogic.standardutils.streams.StreamUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for handling files and folders
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 * 
 */
public class FileUtils {

	public static String toAsciiFilename(String string) {

		return string.replaceAll("[^0-9a-zA-Z-.]", "_");
	}

	public static String toValidHttpFilename(String string) {

		return string.replaceAll("[^0-9a-öA-Ö-+. ()-+!@é&%$§=´]", "_");
	}	
	
	public final static byte[] getRawBytes(File f) throws IOException {
		FileInputStream fin = new FileInputStream(f);
		byte[] buffer = new byte[(int) f.length()];
		fin.read(buffer);
		fin.close();
		return buffer;
	}

	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}

	public static String getFileExtension(String filename) {

		int dotIndex = filename.lastIndexOf(".");

		if (dotIndex == -1 || (dotIndex + 1) == filename.length()) {
			return null;
		} else {
			return filename.substring(dotIndex + 1);
		}
	}

	public static boolean fileExists(String path) {

		File file = new File(path);

		return file.exists();
	}

	/**
	 * Removes all files in the given directory matching the given filter
	 * 
	 * @param directory
	 *            the directory to be cleared
	 * @param filter
	 *            {@link FileFilter} used to filter files
	 * @param recursive
	 *            controls weather files should be deleted from sub directories too
	 */
	public static int deleteFiles(String directory, FileFilter filter, boolean recursive) {

		return deleteFiles(new File(directory), filter, recursive);
	}
	
	public static int deleteFiles(File dir, FileFilter filter, boolean recursive) {

		if (dir.exists() && dir.isDirectory()) {

			int deletedFiles = 0;

			File[] files = dir.listFiles(filter);

			for (File file : files) {

				if (file.isDirectory()) {

					if (recursive) {

						deletedFiles += deleteFiles(file, filter, recursive);
					}

				} else {

					if(file.delete()){
					
						deletedFiles++;
					}
				}
			}

			return deletedFiles;
		}

		return 0;
	}	

	public static int replace(File dir, String filename, File replacementFile, boolean recursive, boolean caseSensitive, Callback<File> callback) {

		if (dir.exists() && dir.isDirectory()) {

			int replacedFiles = 0;

			File[] files = dir.listFiles();

			for (File file : files) {

				if (file.isDirectory()) {

					if (recursive) {

						replacedFiles += replace(file, filename,replacementFile, recursive, caseSensitive, callback);
					}

				} else {

					if(caseSensitive){
						
						if(!file.getName().equals(filename)){
							
							continue;	
						}
						
					}else if(!file.getName().equalsIgnoreCase(filename)){
						
						continue;
					}
					
					if(file.canWrite()){
						
						try {
							if(callback != null){
								callback.callback(file);
							}
							
							replaceFile(file,replacementFile);
							
							replacedFiles++;
							
						} catch (IOException e) {}
					}
				}
			}

			return replacedFiles;
		}

		return 0;
	}
	
	public static void replaceFile(File target, File replacement) throws IOException {
		
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		
		try{			
			inputStream = new FileInputStream(replacement);
			outputStream = new FileOutputStream(target);
			
			inputStream.getChannel().transferTo(0, replacement.length(), outputStream.getChannel());
			
			StreamUtils.transfer(inputStream, outputStream);
			
		}finally{
			StreamUtils.closeStream(inputStream);
			StreamUtils.closeStream(outputStream);
		}
	}

	public static boolean deleteDirectory(String directoryPath) {

		return deleteDirectory(new File(directoryPath));
	}

	public static boolean deleteDirectory(File directory) {

		if (directory.exists()) {

			File[] files = directory.listFiles();

			for (File file : files) {

				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		return directory.delete();
	}

	public static void deleteFile(String path) {

		File file = new File(path);
		
		if(file.exists()){
			file.delete();
		}
	}
}
