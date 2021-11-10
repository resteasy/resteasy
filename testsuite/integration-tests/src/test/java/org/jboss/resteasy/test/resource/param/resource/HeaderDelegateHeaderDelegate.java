package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public class HeaderDelegateHeaderDelegate implements HeaderDelegate<HeaderDelegateHeader>{

   @Override
   public HeaderDelegateHeader fromString(String value) {
      int i = value.indexOf(";");
      HeaderDelegateHeader hdh = null;
      if (i < 0) {
         hdh = new HeaderDelegateHeader(value, "");
      } else {
         hdh = new HeaderDelegateHeader(value.substring(0, i), value.substring(i + 1));
      }
      return hdh;
   }

   @Override
   public String toString(HeaderDelegateHeader value) {
      return value.getMajor() + ";" + value.getMinor();
   }
}
