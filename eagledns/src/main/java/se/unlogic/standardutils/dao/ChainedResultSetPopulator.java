package se.unlogic.standardutils.dao;

import java.sql.ResultSet;
import java.sql.SQLException;


public interface ChainedResultSetPopulator<T> {

	void populate(T bean, ResultSet resultSet) throws SQLException;
}
