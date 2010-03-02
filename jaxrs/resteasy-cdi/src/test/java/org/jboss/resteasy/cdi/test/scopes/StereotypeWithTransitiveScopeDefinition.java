package org.jboss.resteasy.cdi.test.scopes;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;

import javax.enterprise.inject.Stereotype;

@Target( { TYPE } )
@Retention(RUNTIME)
@ApplicationScopedStereotype
@Stereotype
public @interface StereotypeWithTransitiveScopeDefinition
{
}
