package org.jboss.resteasy.propertyeditor.mediatype.editor;

import jakarta.ws.rs.core.MediaType;
import java.beans.PropertyEditorSupport;

/**
 * Spring uses property editors heavily for managing conversion
 * between String values and custom Object types; this is based
 * on Java Beans PropertyEditor.
 *
 * Standard JavaBeans infrastructure will automatically discover
 * PropertyEditor classes if they are in the same package as the
 * class they handle. Also, these need to have the same name
 * as that class plus the Editor suffix
 *
 * This is a property editor for jakarta.ws.rs.core.MediaType.  This
 * editor does not reside in the same package as MediaType, so
 * a custom binding between the required type and the property editor
 * would need to be defined in order to Spring-framework to use it.
 * The property editory would need to be registered in the Spring
 * Controller using the method annotated with @InitBinder.
 *
 * example
 *
 * {@literal @}InitBinder
 * public void initBinder(WebDataBinder binder) {
 *     binder.registerCustomEditor(MediaType.class,
 *         new MediaTypeEditor());
 * }
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
