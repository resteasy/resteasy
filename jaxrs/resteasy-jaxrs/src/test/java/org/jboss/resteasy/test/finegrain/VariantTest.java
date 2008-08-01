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
import java.util.Locale;

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

      builder.languages(new Locale("en"), new Locale("fr"));

      builder.add();
      List<Variant> variants = builder.build();

      Assert.assertEquals(2, variants.size());

      printVariants(variants);

      System.out.println("--------");

      builder.languages(new Locale("en")).encodings("gzip", "octet").mediaTypes(applicationXml);
      variants = builder.build();

      Assert.assertEquals(2, variants.size());

      printVariants(variants);

      System.out.println("--------");

      builder.languages(new Locale("en"), new Locale("es")).mediaTypes(applicationXml, textPlain, textHtml);
      variants = builder.build();

      Assert.assertEquals(6, variants.size());
      printVariants(variants);

      System.out.println("--------");

      builder.languages(new Locale("en"), new Locale("es")).mediaTypes(applicationXml, textPlain, textHtml).encodings("zip");
      variants = builder.build();

      Assert.assertEquals(6, variants.size());
      printVariants(variants);
   }

   @Test
   public void testVariantSorting()
   {
      List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();


      AcceptableVariant variant1 = new AcceptableVariant(MediaType.valueOf("text/plain"), "en;q=0.3", null);
      AcceptableVariant variant2 = new AcceptableVariant(MediaType.valueOf("text/plain"), "fr", null);
      AcceptableVariant variant3 = new AcceptableVariant(MediaType.valueOf("text/plain"), "zh;q=0.6", null);

      acceptable.add(variant1);
      acceptable.add(variant2);
      acceptable.add(variant3);

      List<Variant> variants = AcceptableVariant.sort(acceptable);
      VariantTest.printVariants(variants);

      Assert.assertTrue(acceptable.get(0) == variant2);
      Assert.assertTrue(acceptable.get(1) == variant3);
      Assert.assertTrue(acceptable.get(2) == variant1);


      System.out.println("--------");
   }

   @Test
   public void testVariantSorting2()
   {
      List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();


      AcceptableVariant variant1 = new AcceptableVariant(MediaType.valueOf("text/plain"), "en;q=0.3", null);
      AcceptableVariant variant2 = new AcceptableVariant(MediaType.valueOf("text/html;q=0.4"), "fr", null);
      AcceptableVariant variant3 = new AcceptableVariant(MediaType.valueOf("text/html"), "es", null);
      AcceptableVariant variant4 = new AcceptableVariant(MediaType.valueOf("text/plain"), "zh;q=0.6", null);

      acceptable.add(variant1);
      acceptable.add(variant2);
      acceptable.add(variant3);
      acceptable.add(variant4);

      List<Variant> variants = AcceptableVariant.sort(acceptable);
      VariantTest.printVariants(variants);

      Assert.assertTrue(acceptable.get(0) == variant3);
      Assert.assertTrue(acceptable.get(1) == variant4);
      Assert.assertTrue(acceptable.get(2) == variant1);
      Assert.assertTrue(acceptable.get(3) == variant2);

      System.out.println("--------");
   }

   @Test
   public void testVariantSorting3()
   {
      List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();


      AcceptableVariant variant1 = new AcceptableVariant(MediaType.valueOf("text/plain"), "en;q=0.3", null);
      AcceptableVariant variant2 = new AcceptableVariant(MediaType.valueOf("text/html;q=0.4"), "fr", null);
      AcceptableVariant variant3 = new AcceptableVariant(MediaType.valueOf("text/html"), "es", null);
      AcceptableVariant variant4 = new AcceptableVariant(null, "zh;q=0.6", null);
      AcceptableVariant variant5 = new AcceptableVariant(MediaType.valueOf("application/xml"), "es", "gzip");


      acceptable.add(variant1);
      acceptable.add(variant2);
      acceptable.add(variant3);
      acceptable.add(variant4);
      acceptable.add(variant5);

      List<Variant> variants = AcceptableVariant.sort(acceptable);
      VariantTest.printVariants(variants);

      Assert.assertTrue(acceptable.get(0) == variant5);
      Assert.assertTrue(acceptable.get(1) == variant3);
      Assert.assertTrue(acceptable.get(2) == variant1);
      Assert.assertTrue(acceptable.get(3) == variant2);
      Assert.assertTrue(acceptable.get(4) == variant4);

      System.out.println("--------");
   }

   @Test
   public void testGetLanguageEn()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              languages(new Locale("zh")).
              languages(new Locale("fr")).
              languages(new Locale("en")).add().
              build();

      List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
      acceptable.add(new AcceptableVariant(null, "en", null));

      Variant v = AcceptableVariant.pick(has, acceptable);
      Assert.assertNotNull(v);
      Assert.assertNull(v.getMediaType());
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), new Locale("en"));
   }

   @Test
   public void testGetLanguageZh()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              languages(new Locale("zh")).
              languages(new Locale("fr")).
              languages(new Locale("en")).add().
              build();

      List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
      acceptable.add(new AcceptableVariant(null, "zh", null));

      Variant v = AcceptableVariant.pick(has, acceptable);
      Assert.assertNotNull(v);
      Assert.assertNull(v.getMediaType());
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), new Locale("zh"));
   }

   @Test
   public void testGetLanguageMultiple()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              languages(new Locale("zh")).
              languages(new Locale("fr")).
              languages(new Locale("en")).add().
              build();

      List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
      acceptable.add(new AcceptableVariant(null, "zh;q=0.4", null));
      acceptable.add(new AcceptableVariant(null, "en;q=0.3", null));
      acceptable.add(new AcceptableVariant(null, "fr", null));

      Variant v = AcceptableVariant.pick(has, acceptable);
      Assert.assertNotNull(v);
      Assert.assertNull(v.getMediaType());
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), new Locale("fr"));

   }

   @Test
   public void testGetComplex1()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              mediaTypes(MediaType.valueOf("image/jpeg")).add().
              mediaTypes(MediaType.valueOf("application/xml")).languages(new Locale("en", "us")).add().
              mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en")).add().
              mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en", "us")).add().
              build();

      List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/xml"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xml"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xhtml+xml"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("image/png"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/html;q=0.9"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/plain;q=0.8"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("*/*;q=0.5"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/xml"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xml"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xhtml+xml"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("image/png"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/html;q=0.9"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/plain;q=0.8"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("*/*;q=0.5"), "en;q=0.5", null));


      Variant v = AcceptableVariant.pick(has, acceptable);
      Assert.assertNotNull(v);
      Assert.assertNotNull(v.getMediaType());
      Assert.assertTrue(MediaType.valueOf("text/xml").equals(v.getMediaType()));
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), new Locale("en", "us"));

   }

   @Test
   public void testGetComplex2()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              mediaTypes(MediaType.valueOf("image/jpeg")).add().
              mediaTypes(MediaType.valueOf("application/xml")).languages(new Locale("en", "us")).add().
              mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en")).add().
              mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en", "us")).add().
              build();

      List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/xml"), "en", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xml"), "en", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xhtml+xml"), "en", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("image/png"), "en", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/html;q=0.9"), "en", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/plain;q=0.8"), "en", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("*/*;q=0.5"), "en", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/xml"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xml"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xhtml+xml"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("image/png"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/html;q=0.9"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/plain;q=0.8"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("*/*;q=0.5"), "en-us", null));

      Variant v = AcceptableVariant.pick(has, acceptable);
      Assert.assertNotNull(v);
      Assert.assertNotNull(v.getMediaType());
      Assert.assertTrue(MediaType.valueOf("text/xml").equals(v.getMediaType()));
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), new Locale("en"));

   }

   @Test
   public void testGetComplex3()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              mediaTypes(MediaType.valueOf("image/jpeg")).add().
              mediaTypes(MediaType.valueOf("application/xml")).languages(new Locale("en", "us")).add().
              mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en")).add().
              mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en", "us")).add().
              build();

      List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xml"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/xml"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xhtml+xml"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("image/png"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/html;q=0.9"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/plain;q=0.8"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("*/*;q=0.5"), "en-us", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xml"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/xml"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xhtml+xml"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("image/png"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/html;q=0.9"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("text/plain;q=0.8"), "en;q=0.5", null));
      acceptable.add(new AcceptableVariant(MediaType.valueOf("*/*;q=0.5"), "en;q=0.5", null));

      Variant v = AcceptableVariant.pick(has, acceptable);
      Assert.assertNotNull(v);
      Assert.assertNotNull(v.getMediaType());
      Assert.assertEquals(MediaType.valueOf("application/xml"), v.getMediaType());
      Assert.assertNull(v.getEncoding());
      Assert.assertEquals(v.getLanguage(), new Locale("en", "us"));

   }

   @Test
   public void testGetComplexNotAcceptable()
   {
      List<Variant> has = Variant.VariantListBuilder.newInstance().
              mediaTypes(MediaType.valueOf("image/jpeg")).add().
              mediaTypes(MediaType.valueOf("application/xml")).languages(new Locale("en", "us")).add().
              mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en")).add().
              mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en", "us")).add().
              build();
      {
         List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
         acceptable.add(new AcceptableVariant(MediaType.valueOf("application/atom+xml"), "en-us", null));
         acceptable.add(new AcceptableVariant(MediaType.valueOf("application/atom+xml"), "en", null));

         Variant v = AcceptableVariant.pick(has, acceptable);
         Assert.assertNull(v);
      }

      {
         List<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
         acceptable.add(new AcceptableVariant(MediaType.valueOf("application/xml"), "fr", null));
         Variant v = AcceptableVariant.pick(has, acceptable);
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