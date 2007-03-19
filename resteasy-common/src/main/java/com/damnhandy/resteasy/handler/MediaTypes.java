/*
 * MediaTypes.java
 *
 * Created on November 17, 2006, 8:55 AM
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
 * @author ryan
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MediaTypes {
    public MediaType[] types();
}
