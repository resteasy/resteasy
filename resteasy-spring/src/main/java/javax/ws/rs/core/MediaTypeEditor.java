package javax.ws.rs.core;

import java.beans.PropertyEditorSupport;

/**
 * A MediaType PropertyEditor. Spring automatically looks for a class
 * "Editor"
 * 
 * @author <a href="justin@justinedelson.com">Justin Edelson</a>
 * @version $Revision$
 */
public class MediaTypeEditor extends PropertyEditorSupport
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
