package com.restfully.shop.services;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/services")
public class ShoppingApplication extends Application
{
   private Set<Class<?>> classes = new HashSet<Class<?>>();

   public ShoppingApplication()
   {
      classes.add(EntityNotFoundExceptionMapper.class);
   }

   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   protected ApplicationContext springContext;

   @Context
   protected ServletContext servletContext;

   public Set<Object> getSingletons()
   {
      try
      {
         InitialContext ctx = new InitialContext();
         String xmlFile = (String)servletContext.getInitParameter("spring-beans-file");
         springContext = new ClassPathXmlApplicationContext(xmlFile);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         throw new RuntimeException(ex);
      }
      HashSet<Object> set = new HashSet();
      set.add(springContext.getBean("customer"));
      set.add(springContext.getBean("order"));
      set.add(springContext.getBean("product"));
      set.add(springContext.getBean("store"));
      return set;
   }

}
