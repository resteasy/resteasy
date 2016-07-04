package org.jboss.resteasy.cdi.extension.bean;

import javax.inject.Inject;

@Boston
public class BostonHolder
{
   public @Inject TestReader reader;
   public @Inject @Boston BostonlLeaf leaf;
   
   public TestReader getReader()
   {
      return reader;
   }

   public BostonlLeaf getLeaf()
   {
      return leaf;
   }

   public String toString()
   {
      return "\rthis:   " + System.identityHashCode(this) + "\r" + "reader: " + reader + "\r" + "leaf:   " + leaf; 
   }
}
