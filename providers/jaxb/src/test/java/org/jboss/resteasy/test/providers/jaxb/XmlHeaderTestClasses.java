package org.jboss.resteasy.test.providers.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

public class XmlHeaderTestClasses
{
   /**
    * Test correct type (Marshaller), but incorrect media type
    * FIXME /
   @Decorator(processor = MyDecorator.class, target = Marshaller.class)
   public static @interface Junk
   {
   }

   @DecorateTypes("application/json")
   public static class MyDecorator implements DecoratorProcessor<Marshaller, Junk>
   {
      @Override
      public Marshaller decorate(Marshaller target, Junk annotation, @SuppressWarnings("rawtypes") Class type, Annotation[] annotations, MediaType mediaType)
      {
         throw new RuntimeException("FAILURE!!!!");
      }
   }

   /**
    * Test correct media type, but incorrect type
    * FIXME /
   @Decorator(processor = MyDecorator.class, target = Assert.class)
   public static @interface Junk2
   {
   }

   @DecorateTypes("application/xml")
   public static class MyDecorator2 implements DecoratorProcessor<Assert, Junk2>
   {
      @Override
      public Assert decorate(Assert target, Junk2 annotation, @SuppressWarnings("rawtypes") Class type, Annotation[] annotations, MediaType mediaType)
      {
         throw new RuntimeException("FAILURE!!!!");
      }
   }
*/
   @XmlRootElement
  //FIXME @Junk
  //FIXME @Junk2
   public static class Thing
   {
      private String name;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

}
