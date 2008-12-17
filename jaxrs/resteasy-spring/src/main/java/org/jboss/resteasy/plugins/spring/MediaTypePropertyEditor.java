/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.spring;

import java.beans.PropertyEditorSupport;

import javax.ws.rs.core.MediaType;

/**
 * A MediaType PropertyEditor.
 * 
 * @author <a href="justin@justinedelson.com">Justin Edelson</a>
 * @version $Revision$
 */
public class MediaTypePropertyEditor extends PropertyEditorSupport
{

   /**
    * {@inheritDoc}
    */
   @Override
   public String getAsText()
   {
      return ((MediaType) getValue()).toString();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setAsText(String text) throws IllegalArgumentException
   {
      setValue(MediaType.valueOf(text));
   }

}
