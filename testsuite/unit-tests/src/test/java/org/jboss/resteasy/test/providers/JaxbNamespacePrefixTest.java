package org.jboss.resteasy.test.providers;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.providers.jaxb.XmlNamespacePrefixMapper;
import org.jboss.resteasy.test.providers.resource.jaxbNameSpacePrefix.JaxbNameSpacePrefixItems;
import org.jboss.resteasy.test.providers.resource.jaxbNameSpacePrefix.JaxbNameSpacePrefixPurchaseOrderType;
import org.jboss.resteasy.test.providers.resource.jaxbNameSpacePrefix.JaxbNameSpacePrefixItem;
import org.jboss.resteasy.test.providers.resource.jaxbNameSpacePrefix.ObjectFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlSchema;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

/**
 * @tpSubChapter Providers - jaxb
 * @tpChapter Unit tests
 * @tpSince RESTEasy 3.0.16
 */
public class JaxbNamespacePrefixTest {

    private static final Logger logger = Logger.getLogger(JaxbNamespacePrefixTest.class);

    /**
     * @tpTestDetails Create xml schema from provided class and set "namespacePrefixMapper" for the Marshaller
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacePrefix() throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(JaxbNameSpacePrefixPurchaseOrderType.class);
        JaxbNameSpacePrefixPurchaseOrderType po = new JaxbNameSpacePrefixPurchaseOrderType();
        JaxbNameSpacePrefixItems jaxbNameSpacePrefixItems = new JaxbNameSpacePrefixItems();
        JaxbNameSpacePrefixItem jaxbNameSpacePrefixItem = new JaxbNameSpacePrefixItem();
        jaxbNameSpacePrefixItem.setComment("Tetsing");
        jaxbNameSpacePrefixItem.setPartNum("242-GZ");
        jaxbNameSpacePrefixItem.setProductName("My Thing");
        jaxbNameSpacePrefixItem.setQuantity(6);
        jaxbNameSpacePrefixItem.setUSPrice(new BigDecimal(13.99));
        jaxbNameSpacePrefixItems.getJaxbNameSpacePrefixItem().add(jaxbNameSpacePrefixItem);
        po.setJaxbNameSpacePrefixItems(jaxbNameSpacePrefixItems);
        Marshaller marshaller = ctx.createMarshaller();
        XmlSchema xmlSchema = JaxbNameSpacePrefixPurchaseOrderType.class.getPackage().getAnnotation(XmlSchema.class);
        Assert.assertNotNull("Couldn't create xml schema for JaxbNameSpacePrefixPurchaseOrderType class", xmlSchema);
        XmlNamespacePrefixMapper mapper = new XmlNamespacePrefixMapper(xmlSchema.xmlns());
        try {
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
        } catch (PropertyException e) {
            logger.error(e.getMessage(), e);
        }
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ObjectFactory factory = new ObjectFactory();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(factory.createPurchaseOrder(po), out);
    }
}
