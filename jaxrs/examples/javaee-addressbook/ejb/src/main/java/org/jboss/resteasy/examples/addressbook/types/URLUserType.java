/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * A URLUserType.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class URLUserType implements UserType
{

   public Object assemble(Serializable arg0, Object arg1) throws HibernateException
   {
      // FIXME assemble
      return null;
   }

   public Object deepCopy(Object arg0) throws HibernateException
   {
      // FIXME deepCopy
      return null;
   }

   public Serializable disassemble(Object arg0) throws HibernateException
   {
      // FIXME disassemble
      return null;
   }

   public boolean equals(Object arg0, Object arg1) throws HibernateException
   {
      // FIXME equals
      return false;
   }

   public int hashCode(Object arg0) throws HibernateException
   {
      // FIXME hashCode
      return 0;
   }

   public boolean isMutable()
   {
      // FIXME isMutable
      return false;
   }

   public Object nullSafeGet(ResultSet arg0, String[] arg1, Object arg2) throws HibernateException,
         SQLException
   {
      // FIXME nullSafeGet
      return null;
   }

   public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2)
         throws HibernateException, SQLException
   {
      // FIXME nullSafeSet

   }

   public Object replace(Object arg0, Object arg1, Object arg2) throws HibernateException
   {
      // FIXME replace
      return null;
   }

   public Class returnedClass()
   {
      // FIXME returnedClass
      return null;
   }

   public int[] sqlTypes()
   {
      // FIXME sqlTypes
      return null;
   }

}
