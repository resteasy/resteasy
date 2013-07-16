package javax.ws.rs.core;

import java.beans.PropertyEditorSupport;

import javax.ws.rs.core.MediaType;

/**
 * A MediaType PropertyEditor. Spring automatically looks for a class
 * "<classname>Editor"
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
