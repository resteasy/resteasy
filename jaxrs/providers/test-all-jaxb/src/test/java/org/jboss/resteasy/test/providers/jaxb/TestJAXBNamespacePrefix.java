/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.providers.jaxb.XmlNamespacePrefixMapper;
import org.jboss.resteasy.test.providers.jaxb.generated.po.Item;
import org.jboss.resteasy.test.providers.jaxb.generated.po.Items;
import org.jboss.resteasy.test.providers.jaxb.generated.po.ObjectFactory;
import org.jboss.resteasy.test.providers.jaxb.generated.po.PurchaseOrderType;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlSchema;
import java.math.BigDecimal;

/**
 * A TestJAXBNamespacePrefix.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestJAXBNamespacePrefix
{

   private static final Logger logger = Logger.getLogger(TestJAXBNamespacePrefix.class);

   @Test
   public void testNamespacePrefix() throws Exception
   {
      JAXBContext ctx = JAXBContext.newInstance(PurchaseOrderType.class);
      PurchaseOrderType po = new PurchaseOrderType();
      Items items = new Items();
      Item item = new Item();
      item.setComment("Tetsing");
      item.setPartNum("242-GZ");
      item.setProductName("My Thing");
      item.setQuantity(6);
      item.setUSPrice(new BigDecimal(13.99));
      items.getItem().add(item);
      po.setItems(items);
      Marshaller marshaller = ctx.createMarshaller();
      XmlSchema xmlSchema = PurchaseOrderType.class.getPackage().getAnnotation(XmlSchema.class);
      XmlNamespacePrefixMapper mapper = new XmlNamespacePrefixMapper(xmlSchema.xmlns());
      try
      {
         marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
      }
      catch (PropertyException e)
      {
         logger.warn(e.getMessage(), e);
      }
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      ObjectFactory factory = new ObjectFactory();
      marshaller.marshal(factory.createPurchaseOrder(po), System.out);
   }
}
