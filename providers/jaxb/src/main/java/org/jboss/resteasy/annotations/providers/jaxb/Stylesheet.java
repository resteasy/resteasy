package org.jboss.resteasy.annotations.providers.jaxb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.xml.bind.Marshaller;

import org.jboss.resteasy.annotations.Decorator;
import org.jboss.resteasy.plugins.providers.jaxb.StylesheetProcessor;

/**
 * Specifies an XML stylesheet header
 * <p>
 * e.g.
 *
 * <pre>
 * {@literal <}?xml-stylesheet type='text/xsl' href='foobar.xsl' ?{@literal >}
 * </pre>
 * <p>
 * You can use replacement expressions in value string.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @see org.jboss.resteasy.util.StringContextReplacement
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Decorator(processor = StylesheetProcessor.class, target = Marshaller.class)
public @interface Stylesheet {
    String XSL = "text/xsl";
    String CSS = "text/css";

    String href();

    String type();
}
