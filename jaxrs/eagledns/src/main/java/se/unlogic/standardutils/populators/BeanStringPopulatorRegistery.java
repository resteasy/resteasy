package se.unlogic.standardutils.populators;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


public class BeanStringPopulatorRegistery {

	private final static HashMap<Class<?>, BeanStringPopulator<?>> TYPE_POPULATORS = new HashMap<Class<?>, BeanStringPopulator<?>>();
	
	static{
		addTypePopulator(new UUIDPopulator());
		addTypePopulator(new BooleanPopulator());
		addTypePopulator(new DoublePopulator());
		addTypePopulator(new FloatPopulator());
		addTypePopulator(new IntegerPopulator());
		addTypePopulator(new LongPopulator());
		addTypePopulator(new PrimitiveBooleanPopulator());
		addTypePopulator(new PrimitiveIntegerPopulator());
		addTypePopulator(new PrimitiveLongPopulator());
		addTypePopulator(new StringPopulator());
	}
	
	private static void addTypePopulator(BeanStringPopulator<?> typePopulator){
		
		TYPE_POPULATORS.put(typePopulator.getType(), typePopulator);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> BeanStringPopulator<T> getBeanStringPopulator(Class<T> clazz){
		
		return (BeanStringPopulator<T>) TYPE_POPULATORS.get(clazz);
	}
	
	public static Collection<BeanStringPopulator<?>> getBeanStringPopulators(){
		
		return Collections.unmodifiableCollection(TYPE_POPULATORS.values());
	}
}
