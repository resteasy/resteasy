package se.unlogic.standardutils.zip;

import se.unlogic.standardutils.streams.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipUtils {

	public static void addFile(File file, ZipOutputStream outputStream) throws IOException{
		
		ZipEntry zipEntry = new ZipEntry(file.getName());
		
		FileInputStream inputStream = null;
		
		try{
			inputStream = new FileInputStream(file);			
			
			outputStream.putNextEntry(zipEntry);
			
			StreamUtils.transfer(inputStream, outputStream);
			
			outputStream.closeEntry();
		}finally{
			
			StreamUtils.closeStream(inputStream);
		}
	}
	
	public static void addFiles(File[] files, ZipOutputStream outputStream) throws IOException{
		
		for(File file : files){
			
			addFile(file, outputStream);
		}
	}
}
