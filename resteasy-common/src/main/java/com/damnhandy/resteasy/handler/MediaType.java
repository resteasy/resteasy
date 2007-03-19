/*
 * MediaType.java
 *
 * Created on November 17, 2006, 8:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.damnhandy.resteasy.handler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Ryan J. McDonough
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MediaType {
    
	/**
	 * 
	 * @return
	 */
    public String type();
    
    /**
     * 
     * @return
     */
    public String[] extentions();
}
