package org.hornetq.rest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueDeployment
{
   private String name;
   private boolean duplicatesAllowed;

   public QueueDeployment()
   {
   }

   public QueueDeployment(String name, boolean duplicatesAllowed)
   {
      this.name = name;
      this.duplicatesAllowed = duplicatesAllowed;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public boolean isDuplicatesAllowed()
   {
      return duplicatesAllowed;
   }

   public void setDuplicatesAllowed(boolean duplicatesAllowed)
   {
      this.duplicatesAllowed = duplicatesAllowed;
   }
}