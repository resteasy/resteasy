package org.jboss.resteasy.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class DelegateClassLoader extends SecureClassLoader
{
   private final ClassLoader delegate;

   private final ClassLoader parent;

   public DelegateClassLoader(final ClassLoader delegate, final ClassLoader parent)
   {
      super(parent);
      this.delegate = delegate;
      this.parent = parent;
   }

   /** {@inheritDoc} */
   @Override
   public Class<?> loadClass(final String className) throws ClassNotFoundException
   {
      if (parent != null)
      {
         try
         {
            return parent.loadClass(className);
         }
         catch (ClassNotFoundException cnfe)
         {
            //NOOP, use delegate
         }
      }
      return delegate.loadClass(className);
   }

   /** {@inheritDoc} */
   @Override
   public URL getResource(final String name)
   {
      URL url = null;
      if (parent != null)
      {
         url = parent.getResource(name);
      }
      return (url == null) ? delegate.getResource(name) : url;
   }

   /** {@inheritDoc} */
   @Override
   public Enumeration<URL> getResources(final String name) throws IOException
   {
      final ArrayList<Enumeration<URL>> foundResources = new ArrayList<Enumeration<URL>>();

      foundResources.add(delegate.getResources(name));
      if (parent != null)
      {
         foundResources.add(parent.getResources(name));
      }

      return new Enumeration<URL>()
      {
         private int position = foundResources.size() - 1;

         public boolean hasMoreElements()
         {
            while (position >= 0)
            {
               if (foundResources.get(position).hasMoreElements())
               {
                  return true;
               }
               position--;
            }
            return false;
         }

         public URL nextElement()
         {
            while (position >= 0)
            {
               try
               {
                  return (foundResources.get(position)).nextElement();
               }
               catch (NoSuchElementException e)
               {
               }
               position--;
            }
            throw new NoSuchElementException();
         }
      };
   }

   /** {@inheritDoc} */
   @Override
   public InputStream getResourceAsStream(final String name)
   {
      InputStream is = null;
      if (parent != null)
      {
         is = parent.getResourceAsStream(name);
      }
      return (is == null) ? delegate.getResourceAsStream(name) : is;
   }
}