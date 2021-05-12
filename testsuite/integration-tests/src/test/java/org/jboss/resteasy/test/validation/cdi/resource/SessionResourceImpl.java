package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.ejb.Stateless;

@Stateless
public class SessionResourceImpl implements SessionResourceLocal, SessionResourceRemote {
   public String test(String param) {
      return param;
   }
}
