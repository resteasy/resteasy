package org.jboss.resteasy.test.client;

import javax.ws.rs.ext.RuntimeDelegate;

public class NullStringBeanRuntimeDelegate extends StringBeanRuntimeDelegate {

   public NullStringBeanRuntimeDelegate(RuntimeDelegate orig) {
      super(orig);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> arg0)
           throws IllegalArgumentException {
      if (arg0 == StringBean.class)
         return (HeaderDelegate<T>) new NullStringBeanHeaderDelegate();
      else
         return super.createHeaderDelegate(arg0);
   }

}
