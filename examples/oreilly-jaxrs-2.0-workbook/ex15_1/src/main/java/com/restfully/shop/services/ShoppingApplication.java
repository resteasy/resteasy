package com.restfully.shop.services;

import com.restfully.shop.features.OneTimePasswordAuthenticator;
import com.restfully.shop.features.PerDayAuthorizer;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/services")
public class ShoppingApplication extends Application
{
   private Set<Object> singletons = new HashSet<Object>();

   public ShoppingApplication()
   {
      singletons.add(new CustomerResource());
      HashMap<String, String> userSecretMap = new HashMap<String, String>();
      userSecretMap.put("bburke", "geheim");
      singletons.add(new OneTimePasswordAuthenticator(userSecretMap));
      singletons.add(new PerDayAuthorizer());
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}
