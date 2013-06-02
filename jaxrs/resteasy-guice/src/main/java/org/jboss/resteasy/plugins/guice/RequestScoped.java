package org.jboss.resteasy.plugins.guice;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Provides an instance-per-request.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestScoped
{
}
