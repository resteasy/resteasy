package se.unlogic.standardutils.populators;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


public class QueryParameterPopulatorRegistery {

	private final static HashMap<Class<?>, QueryParameterPopulator<?>> QUERY_PARAMETER_POPULATORS = new HashMap<Class<?>, QueryParameterPopulator<?>>();
	
	static{
		addTypePopulator(new UUIDPopulator());
	}
	
	private static void addTypePopulator(QueryParameterPopulator<?> queryParameterPopulator){
		
		QUERY_PARAMETER_POPULATORS.put(queryParameterPopulator.getType(), queryParameterPopulator);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> BeanStringPopulator<T> getQueryParameterPopulator(Class<T> clazz){
		
		return (BeanStringPopulator<T>) QUERY_PARAMETER_POPULATORS.get(clazz);
	}
	
	public static Collection<QueryParameterPopulator<?>> getQueryParameterPopulators(){
		
		return Collections.unmodifiableCollection(QUERY_PARAMETER_POPULATORS.values());
	}
}
