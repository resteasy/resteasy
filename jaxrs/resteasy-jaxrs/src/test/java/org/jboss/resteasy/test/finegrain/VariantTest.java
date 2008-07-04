package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.AcceptableVariant;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VariantTest
{
   @BeforeClass
   public static void start()
   {
      ResteasyProviderFactory.initializeInstance();
   }

   @Test
   public void testAdd()
   {
      MediaType applicationXml = new MediaType("application", "xml");
      MediaType textPlain = new MediaType("text", "plain");
      MediaType textHtml = new MediaType("text", "html");

      Variant.VariantListBuilder builder = Variant.VariantListBuilder.newInstance();

      builder.languages("en", "fr");

      builder.add();
      List<Variant> variants = builder.build();

      Assert.assertEquals(2, variants.size());

      printVariants(variants);

      System.out.println("--------");

      builder.languages("en").encodings("gzip", "octet").mediaTypes(applicationXml);
      variants = builder.build();

      Assert.assertEquals(2, variants.size());

      printVariants(variants);

      System.out.println("--------");

      builder.languages("en", "es").mediaTypes(applicationXml, textPlain, textHtml);
      variants = builder.build();

      Assert.assertEquals(6, variants.size());
      printVariants(variants);

      System.out.println("--------");

      builder.languages("en", "es").mediaTypes(applicationXml, textPlain, textHtml).encodings("zip");
      variants = builder.build();

      Assert.assertEquals(6, variants.size());
      printVariants(variants);
   }

   @Test
   public void testVariantSorting()
   {
      List<Variant> variants = new ArrayList<Variant>();


      Variant variant1 = new Variant(MediaType.valueOf("text/plain"), "en;q=0.3", null);
      Variant variant2 = new Variant(MediaType.valueOf("text/plain"), "fr", null);
      Variant variant3 = new Variant(MediaType.valueOf("text/plain"), "zh;q=0.6", null);

      variants.add(variant1);
      variants.add(variant2);
      variants.add(variant3);

      variants = AcceptableVariant.sort(variants);
      VariantTest.printVariants(variants);

      Assert.assertTrue(variants.get(0) == variant2);
      Assert.assertTrue(variants.get(1) == variant3);
      Assert.assertTrue(variants.get(2) == variant1);


      System.out.println("--------");
   }

   @Test
   public void testVariantSorting2()
   {
      List<Variant> variants = new ArrayList<Variant>();


      Variant variant1 = new Variant(MediaType.valueOf("text/plain"), "en;q=0.3", null);
      Variant variant2 = new Variant(MediaType.valueOf("text/html;q=0.4"), "fr", null);
      Variant variant3 = new Variant(MediaType.valueOf("text/html"), "es", null);
      Variant variant4 = new Variant(MediaType.valueOf("text/plain"), "zh;q=0.6", null);

      variants.add(variant1);
      variants.add(variant2);
      variants.add(variant3);
      variants.add(variant4);

      variants = AcceptableVariant.sort(variants);
      VariantTest.printVariants(variants);

      Assert.assertTrue(variants.get(0) == variant3);
      Assert.assertTrue(variants.get(1) == variant4);
      Assert.assertTrue(variants.get(2) == variant1);
      Assert.assertTrue(variants.get(3) == variant2);

      System.out.println("--------");
   }

   @Test
   public void testVariantSorting3()
   {
      List<Variant> variants = new ArrayList<Variant>();


      Variant variant1 = new Variant(MediaType.valueOf("text/plain"), "en;q=0.3", null);
      Variant variant2 = new Variant(MediaType.valueOf("text/html;q=0.4"), "fr", null);
      Variant variant3 = new Variant(MediaType.valueOf("text/html"), "es", null);
      Variant variant4 = new Variant(null, "zh;q=0.6", null);
      Variant variant5 = new Variant(MediaType.valueOf("application/xml"), "es", "gzip");


      variants.add(variant1);
      variants.add(variant2);
      variants.add(variant3);
      variants.add(variant4);
      variants.add(variant5);

      variants = AcceptableVariant.sort(variants);
      VariantTest.printVariants(variants);

      Assert.assertTrue(variants.get(0) == variant5);
      Assert.assertTrue(variants.get(1) == variant3);
      Assert.assertTrue(variants.get(2) == variant1);
      Assert.assertTrue(variants.get(3) == variant2);
      Assert.assertTrue(variants.get(4) == variant4);

      System.out.println("--------");
   }

   @Test
   public void testGetLanguageEn()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              languages("zh").
              languages("fr").
              languages("en").add().
              build();


      List<Variant> wants = Variant.VariantListBuilder.newInstance().languages("en").build();

      Variant v = AcceptableVariant.pick(wants, has);
      Assert.assertNotNull(v);
      Assert.assertNull(v.getMediaType());
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), "en");
   }

   @Test
   public void testGetLanguageZh()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              languages("zh").
              languages("fr").
              languages("en").add().
              build();

      List<Variant> wants = Variant.VariantListBuilder.newInstance()
              .languages("zh").build();

      Variant v = AcceptableVariant.pick(wants, has);
      Assert.assertNotNull(v);
      Assert.assertNull(v.getMediaType());
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), "zh");
   }

   @Test
   public void testGetLanguageMultiple()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              languages("zh").
              languages("fr").
              languages("en").add().
              build();

      List<Variant> wants = Variant.VariantListBuilder.newInstance()
              .languages("zh;q=0.4")
              .languages("en;q=0.3")
              .languages("fr").build();

      Variant v = AcceptableVariant.pick(wants, has);
      Assert.assertNotNull(v);
      Assert.assertNull(v.getMediaType());
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), "fr");

   }

   @Test
   public void testGetComplex1()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              mediaTypes(MediaType.valueOf("image/jpeg")).add().
              mediaTypes(MediaType.valueOf("application/xml")).languages("en-us").add().
              mediaTypes(MediaType.valueOf("text/xml")).languages("en").add().
              mediaTypes(MediaType.valueOf("text/xml")).languages("en-us").add().
              build();

      List<Variant> wants = Variant.VariantListBuilder.newInstance()
              .mediaTypes(MediaType.valueOf("text/xml"))
              .mediaTypes(MediaType.valueOf("application/xml"))
              .mediaTypes(MediaType.valueOf("application/xhtml+xml"))
              .mediaTypes(MediaType.valueOf("image/png"))
              .mediaTypes(MediaType.valueOf("text/html;q=0.9"))
              .mediaTypes(MediaType.valueOf("text/plain;q=0.8"))
              .mediaTypes(MediaType.valueOf("*/*;q=0.5"))
              .languages("en-us")
              .languages("en;q=0.5").build();

      Variant v = AcceptableVariant.pick(wants, has);
      Assert.assertNotNull(v);
      Assert.assertNotNull(v.getMediaType());
      Assert.assertTrue(MediaType.valueOf("text/xml").equals(v.getMediaType()));
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), "en-us");

   }

   @Test
   public void testGetComplex2()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              mediaTypes(MediaType.valueOf("image/jpeg")).add().
              mediaTypes(MediaType.valueOf("application/xml")).languages("en-us").add().
              mediaTypes(MediaType.valueOf("text/xml")).languages("en").add().
              mediaTypes(MediaType.valueOf("text/xml")).languages("en-us").add().
              build();

      List<Variant> wants = Variant.VariantListBuilder.newInstance()
              .mediaTypes(MediaType.valueOf("text/xml"))
              .mediaTypes(MediaType.valueOf("application/xml"))
              .mediaTypes(MediaType.valueOf("application/xhtml+xml"))
              .mediaTypes(MediaType.valueOf("image/png"))
              .mediaTypes(MediaType.valueOf("text/html;q=0.9"))
              .mediaTypes(MediaType.valueOf("text/plain;q=0.8"))
              .mediaTypes(MediaType.valueOf("*/*;q=0.5"))
              .languages("en")
              .languages("en-us").build();

      Variant v = AcceptableVariant.pick(wants, has);
      Assert.assertNotNull(v);
      Assert.assertNotNull(v.getMediaType());
      Assert.assertTrue(MediaType.valueOf("text/xml").equals(v.getMediaType()));
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), "en");

   }

   @Test
   public void testGetComplex3()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              mediaTypes(MediaType.valueOf("image/jpeg")).add().
              mediaTypes(MediaType.valueOf("application/xml")).languages("en-us").add().
              mediaTypes(MediaType.valueOf("text/xml")).languages("en").add().
              mediaTypes(MediaType.valueOf("text/xml")).languages("en-us").add().
              build();

      List<Variant> wants = Variant.VariantListBuilder.newInstance()
              .mediaTypes(MediaType.valueOf("application/xml"))
              .mediaTypes(MediaType.valueOf("text/xml"))
              .mediaTypes(MediaType.valueOf("application/xhtml+xml"))
              .mediaTypes(MediaType.valueOf("image/png"))
              .mediaTypes(MediaType.valueOf("text/html;q=0.9"))
              .mediaTypes(MediaType.valueOf("text/plain;q=0.8"))
              .mediaTypes(MediaType.valueOf("*/*;q=0.5"))
              .languages("en-us")
              .languages("en;q=0.5").build();

      Variant v = AcceptableVariant.pick(wants, has);
      Assert.assertNotNull(v);
      Assert.assertNotNull(v.getMediaType());
      Assert.assertTrue(MediaType.valueOf("application/xml").equals(v.getMediaType()));
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), "en-us");

   }

   @Test
   public void testGetComplexNotAcceptable()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              mediaTypes(MediaType.valueOf("image/jpeg")).add().
              mediaTypes(MediaType.valueOf("application/xml")).languages("en-us").add().
              mediaTypes(MediaType.valueOf("text/xml")).languages("en").add().
              mediaTypes(MediaType.valueOf("text/xml")).languages("en-us").add().
              build();
      {
         List<Variant> wants = Variant.VariantListBuilder.newInstance()
                 .mediaTypes(MediaType.valueOf("application/atom+xml"))
                 .languages("en-us")
                 .languages("en").build();

         Variant v = AcceptableVariant.pick(wants, has);
         Assert.assertNull(v);
      }

      {
         List<Variant> wants = Variant.VariantListBuilder.newInstance()
                 .mediaTypes(MediaType.valueOf("application/xml"))
                 .languages("fr").build();

         Variant v = AcceptableVariant.pick(wants, has);
         Assert.assertNull(v);
      }


   }

   public static void printVariants(List<Variant> variants)
   {
      for (Variant variant : variants)
      {
         System.out.println("Variant: type=" + variant.getMediaType() + " language=" + variant.getLanguage() + " encoding=" + variant.getEncoding());
      }
   }
}