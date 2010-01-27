package org.jboss.resteasy.plugins.server.servlet;

/**
 * Constant list of bootstrap classes.  This is used by JBoss AS to determine whether or not it should scan or not
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyBootstrapClasses
{
   public static String[] BOOTSTRAP_CLASSES = {
           HttpServletDispatcher.class.getName(),
           ResteasyBootstrap.class.getName()
   };

}
