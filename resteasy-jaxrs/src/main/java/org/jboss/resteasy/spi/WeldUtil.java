package org.jboss.resteasy.spi;

class WeldUtil
{
   private static final String WELD_PROXY_INTERFACE_NAME = "org.jboss.weld.bean.proxy.ProxyObject";

   /**
    * Whether the given class is a proxy created by Weld or not. This is
    * the case if the given class implements the interface
    * {@code org.jboss.weld.bean.proxy.ProxyObject}.
    * 
    * This is needed because of https://issues.jboss.org/browse/WELD-1539 and
    * https://issues.jboss.org/browse/WELD-1914 which are unsolved.
    *
    * @param clazz the class of interest
    *
    * @return {@code true} if the given class is a Weld proxy,
    * {@code false} otherwise
    */
   static boolean isWeldProxy(Class<?> clazz) {
      for ( Class<?> implementedInterface : clazz.getInterfaces() ) {
         if ( implementedInterface.getName().equals( WELD_PROXY_INTERFACE_NAME ) ) {
            return true;
         }
      }

      return false;
   }
}
