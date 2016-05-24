package se.unlogic.standardutils.collections;

import se.unlogic.standardutils.dao.enums.Order;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;


public class MethodComparator<T> implements Comparator<T> {

	protected Method method;
	protected final Order order;
	
	public MethodComparator(Class<? extends T> clazz, String methodName, Order order){
		
		Method[] methods = clazz.getDeclaredMethods();
		
		for(Method method : methods){
			
			if(method.getName().equals(methodName) && Comparable.class.isAssignableFrom(method.getReturnType()) && method.getParameterTypes().length == 0){
				
				this.method = method;
				break;
			}
		}
		
		if(method == null){
			
			throw new RuntimeException("No method named " + methodName + " returning a comparable class and taking no paramaters found in " + clazz);
		}
		
		this.order = order;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compare(T o1, T o2) {
		
		try {

			if(order == Order.ASC){
				
				return ((Comparable)method.invoke(o1)).compareTo(method.invoke(o2));

			}else{
				
				return ((Comparable)method.invoke(o2)).compareTo(method.invoke(o1));
			}
			
			
		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);
			
		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
			
		} catch (InvocationTargetException e) {

			throw new RuntimeException(e);
		}		
	}
}
