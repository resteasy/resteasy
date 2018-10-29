package org.jboss.resteasy.test.core.basic.resource;

/**
 * A PartialAnnotationResourceImpl.
 *
 * @author pjurak
 */
public class PartialAnnotationResourceImpl implements PartialAnnotationResource
{

   public static final String FOO_RESPONSE = "foo response";

   public static final String BAR_RESPONSE = "bar response";

   @Override
   public String bar()
   {
      return BAR_RESPONSE;
   }

   @Override
   public String foo()
   {
      return FOO_RESPONSE;
   }

}
