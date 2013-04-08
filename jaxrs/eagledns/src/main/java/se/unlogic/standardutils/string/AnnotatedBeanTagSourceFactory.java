package se.unlogic.standardutils.string;

import se.unlogic.standardutils.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


public class AnnotatedBeanTagSourceFactory<T> extends BeanTagSourceFactory<T> {

	public AnnotatedBeanTagSourceFactory(Class<T> beanClass, String defaultPrefix) {

		super(beanClass);

		List<Field> fields = ReflectionUtils.getFields(beanClass);
		
		for(Field field : fields){
			
			StringTag stringTag = field.getAnnotation(StringTag.class);
			
			if(stringTag != null){
				
				if(StringUtils.isEmpty(stringTag.name())){
					
					addFieldMapping(defaultPrefix + field.getName(), field);
					
				}else{
					
					addFieldMapping(defaultPrefix + stringTag.name(), field);
				}
			}
		}
		
		List<Method> methods = ReflectionUtils.getMethods(beanClass);
		
		for(Method method : methods){
			
			StringTag stringTag = method.getAnnotation(StringTag.class);
			
			if(stringTag != null){
				
				if(StringUtils.isEmpty(stringTag.name())){
					
					addMethodMapping(defaultPrefix + method.getName(), method);
					
				}else{
					
					addMethodMapping(defaultPrefix + stringTag.name(), method);
				}
			}
		}
	}
}
