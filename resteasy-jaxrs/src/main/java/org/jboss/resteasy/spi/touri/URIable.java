package org.jboss.resteasy.spi.touri;

/**
 * This is an interface which allows an object to perform its own uri creation
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
public interface URIable
{
   String toURI();
}
