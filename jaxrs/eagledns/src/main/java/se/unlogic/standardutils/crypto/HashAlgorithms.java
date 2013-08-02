package se.unlogic.standardutils.crypto;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class HashAlgorithms {

	public static final String MD2 = "MD2";
	public static final String MD5 = "MD5";
	public static final String SHA1 = "SHA-1";
	public static final String SHA256 = "SHA-256";
	public static final String SHA384 = "SHA-384";
	public static final String SHA512 = "SHA-512";
	
	private static final List<String> ALGORITHMS;
	
	static{
		Field[] fields = HashAlgorithms.class.getFields();
		
		ALGORITHMS = new ArrayList<String>(fields.length);
		
		HashAlgorithms hashAlgorithms = new HashAlgorithms();
		
		for(Field field : fields){
		
			try {
				ALGORITHMS.add((String) field.get(hashAlgorithms));
				
			} catch (IllegalArgumentException e) {
				
				throw new RuntimeException(e);
				
			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);
			}
		}
	}
	
	public static String[] getAlgorithms(){
			
		return ALGORITHMS.toArray(new String[ALGORITHMS.size()]);
	}
}
