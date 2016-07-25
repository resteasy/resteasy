package org.jboss.resteasy.test.providers.namespace.mapping;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This class is used from NamespaceMappingTest
 *
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.example.b.test package.
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

    private static final QName _TestExtends_QNAME = new QName("http://www.example.org/b/Test", "NamespaceMappingTestExtends");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.example.b.test
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NamespaceMappingTestExtends }
     */
    public NamespaceMappingTestExtends createTestExtends() {
        return new NamespaceMappingTestExtends();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamespaceMappingTestExtends }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/b/Test", name = "NamespaceMappingTestExtends")
    public JAXBElement<NamespaceMappingTestExtends> createTestExtends(NamespaceMappingTestExtends value) {
        return new JAXBElement<NamespaceMappingTestExtends>(_TestExtends_QNAME, NamespaceMappingTestExtends.class, null, value);
    }
}
