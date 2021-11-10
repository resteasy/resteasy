package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public class HeaderDelegateAsProviderHeaderDelegate implements HeaderDelegate<HeaderDelegateAsProviderHeader> {

   @Override
   public HeaderDelegateAsProviderHeader fromString(String value) {
      int i = value.indexOf(";");
      HeaderDelegateAsProviderHeader th = null;
      if (i < 0) {
         th = new HeaderDelegateAsProviderHeader("fromString:" + value, "");
      } else {
         th = new HeaderDelegateAsProviderHeader("fromString:" + value.substring(0, i), value.substring(i + 1));
      }
      return th;
   }

   @Override
   public String toString(HeaderDelegateAsProviderHeader value) {
      return "toString:" + value.getMajor() + ";" + value.getMinor();
   }
}
