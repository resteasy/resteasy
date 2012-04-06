package se.unlogic.standardutils.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class BeanChainPopulator<T> implements BeanResultSetPopulator<T> {

	protected BeanResultSetPopulator<T> populator;
	protected List<ChainedResultSetPopulator<T>> chainedResultSetPopulators;
	
	public BeanChainPopulator(List<ChainedResultSetPopulator<T>> chainedResultSetPopulators, BeanResultSetPopulator<T> populator) {

		super();
		this.populator = populator;
		this.chainedResultSetPopulators = chainedResultSetPopulators;
	}

	public T populate(ResultSet rs) throws SQLException {

		T bean = populator.populate(rs);
		
		for(ChainedResultSetPopulator<T> chainedPopulator : chainedResultSetPopulators){
			
			chainedPopulator.populate(bean, rs);
		}
		
		return bean;
	}
}
