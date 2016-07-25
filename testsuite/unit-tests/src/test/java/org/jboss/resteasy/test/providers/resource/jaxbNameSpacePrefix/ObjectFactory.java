package org.jboss.resteasy.test.providers.resource.jaxbNameSpacePrefix;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _Comment_QNAME = new QName("http://jboss.org/resteasy/test/providers/resource/jaxbNameSpacePrefix", "comment");
    private static final QName _PurchaseOrder_QNAME = new QName("http://jboss.org/resteasy/test/providers/resource/jaxbNameSpacePrefix", "purchaseOrder");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.resteasy.test.providers.jaxb.generated.po
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JaxbNameSpacePrefixUSAddress }
     */
    public JaxbNameSpacePrefixUSAddress createUSAddress() {
        return new JaxbNameSpacePrefixUSAddress();
    }

    /**
     * Create an instance of {@link JaxbNameSpacePrefixPurchaseOrderType }
     */
    public JaxbNameSpacePrefixPurchaseOrderType createPurchaseOrderType() {
        return new JaxbNameSpacePrefixPurchaseOrderType();
    }

    /**
     * Create an instance of {@link JaxbNameSpacePrefixItem }
     */
    public JaxbNameSpacePrefixItem createItem() {
        return new JaxbNameSpacePrefixItem();
    }

    /**
     * Create an instance of {@link JaxbNameSpacePrefixItems }
     */
    public JaxbNameSpacePrefixItems createItems() {
        return new JaxbNameSpacePrefixItems();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://jboss.org/resteasy/test/providers/resource/jaxbNameSpacePrefix", name = "comment")
    public JAXBElement<String> createComment(String value) {
        return new JAXBElement<String>(_Comment_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JaxbNameSpacePrefixPurchaseOrderType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://jboss.org/resteasy/test/providers/resource/jaxbNameSpacePrefix", name = "purchaseOrder")
    public JAXBElement<JaxbNameSpacePrefixPurchaseOrderType> createPurchaseOrder(JaxbNameSpacePrefixPurchaseOrderType value) {
        return new JAXBElement<JaxbNameSpacePrefixPurchaseOrderType>(_PurchaseOrder_QNAME, JaxbNameSpacePrefixPurchaseOrderType.class, null, value);
    }

}
