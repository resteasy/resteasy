package org.jboss.resteasy.test.validation;

import java.lang.reflect.Method;

import org.junit.Ignore;
import org.junit.Test;

public class Experiment {

	public interface Foo<T> {
	    void bar(T qux);
	}

	public class FooImpl implements Foo<String> {
	    @Override
	     public void bar(String qux) {}
	}
	
	@Test
	@Ignore
	public void test()
	{
		Class<?> clazz = FooImpl.class;
		while (clazz != null)
		{
			Method m;
			try {
				m = clazz.getDeclaredMethod("bar", String.class);
				System.out.println(m);
			} catch (NoSuchMethodException e) {
				System.out.println("no foo on " + clazz);
			}
			finally
			{
				clazz = clazz.getSuperclass();
			}
		}
		
		Class<?>[] clazzes = FooImpl.class.getInterfaces();
		for (int i = 0; i < clazzes.length; i++)
		{
			Method m;
			try {
				m = clazzes[i].getDeclaredMethod("bar", String.class);
				System.out.println(m);
			} catch (NoSuchMethodException e) {
				System.out.println("no foo on " + clazzes[i]);
			}
		}
	}
}
