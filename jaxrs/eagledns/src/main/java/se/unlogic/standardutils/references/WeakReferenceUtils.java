package se.unlogic.standardutils.references;

import java.lang.ref.WeakReference;


public class WeakReferenceUtils {

	public static <T> T getReferenceValue(WeakReference<T> weakReference){
		
		if(weakReference == null){
			
			return null;
		}
		
		return weakReference.get();
	}
}
