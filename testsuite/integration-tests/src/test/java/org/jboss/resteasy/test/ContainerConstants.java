package org.jboss.resteasy.test;

/**
 * Various contants for containers defined in arquillian.xml. Keep this file in sync with the values there.
 */
public class ContainerConstants {

   public static final String DEFAULT_CONTAINER_QUALIFIER = "jbossas-managed";

   public static final String GZIP_CONTAINER_QUALIFIER = "jbossas-manual-gzip";

   public static final int GZIP_CONTAINER_PORT_OFFSET = 1000;

   public static final String TRACING_CONTAINER_QUALIFIER = "jbossas-manual-tracing";

   public static final int TRACING_CONTAINER_PORT_OFFSET = 2000;

   public static final String SSL_CONTAINER_QUALIFIER = "jbossas-manual-ssl";

   public static final int SSL_CONTAINER_PORT_OFFSET = 3000;

   public static final String SSL_CONTAINER_QUALIFIER_WRONG = "jbossas-manual-ssl-wrong";

   public static final int SSL_CONTAINER_PORT_OFFSET_WRONG = 4000;

   public static final String SSL_CONTAINER_QUALIFIER_WILDCARD = "jbossas-manual-ssl-wildcard";

   public static final int SSL_CONTAINER_PORT_OFFSET_WILDCARD = 5000;

   public static final String SSL_CONTAINER_QUALIFIER_SNI = "jbossas-manual-ssl-sni";

   public static final int SSL_CONTAINER_PORT_OFFSET_SNI = 6000;

   public static final String ENCODING_CONTAINER_QUALIFIER = "jbossas-managed-encoding";

   public static final int ENCODING_CONTAINER_PORT_OFFSET = 7000;

}
