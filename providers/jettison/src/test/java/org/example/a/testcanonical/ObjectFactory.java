//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.25 at 10:14:22 AM EDT 
//


package org.example.a.testcanonical;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.example.a.testcanonical package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TestBase_QNAME = new QName("http://www.example.org/a/TestCanonical", "TestBase");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.example.a.testcanonical
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TestBase }
     * 
     */
    public TestBase createTestBase() {
        return new TestBase();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.org/a/TestCanonical", name = "TestBase")
    public JAXBElement<TestBase> createTestBase(TestBase value) {
        return new JAXBElement<TestBase>(_TestBase_QNAME, TestBase.class, null, value);
    }

}
