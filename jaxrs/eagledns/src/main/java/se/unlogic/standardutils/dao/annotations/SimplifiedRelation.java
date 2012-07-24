/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.annotations;

import se.unlogic.standardutils.dao.enums.Order;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used in conjunction with the {@link OneToMany} annotation to indicate the annotated field
 * is a relation to a table containing only two columns, key and value and therefore doesn't require a separate bean class.
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SimplifiedRelation {
	String keyField() default "";
	String table();
	String remoteKeyColumnName() default "";
	String remoteValueColumnName();
	Order order() default Order.ASC;
	boolean addTablePrefix() default false;
	boolean deplurifyTablePrefix() default false;
	boolean preserveListOrder() default false;
	String indexColumn() default "";
}
