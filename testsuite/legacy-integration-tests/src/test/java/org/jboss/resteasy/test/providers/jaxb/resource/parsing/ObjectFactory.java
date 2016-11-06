package org.jboss.resteasy.test.providers.jaxb.resource.parsing;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the mil.navy.tsts.datacollection.parseRS package.
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

    private static final QName _DataCollectionPackage_QNAME = new QName("http://www.example.org/ParsingDataCollectionPackage", "ParsingDataCollectionPackage");
    private static final QName _DataCollectionRecord_QNAME = new QName("http://www.example.org/ParsingDataCollectionPackage", "ParsingDataCollectionRecord");
    private static final QName _SourceID_QNAME = new QName("http://www.example.org/ParsingDataCollectionPackage", "sourceID");
    private static final QName _Timestamp_QNAME = new QName("http://www.example.org/ParsingDataCollectionPackage", "timestamp");
    private static final QName _EventID_QNAME = new QName("http://www.example.org/ParsingDataCollectionPackage", "eventID");
    private static final QName _CollectedData_QNAME = new QName("http://www.example.org/ParsingDataCollectionPackage", "collectedData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mil.navy.tsts.datacollection.parseRS
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ParsingDataCollectionPackage }
     */
    public ParsingDataCollectionPackage createDataCollectionPackage() {
        return new ParsingDataCollectionPackage();
    }

    /**
     * Create an instance of {@link ParsingDataCollectionPackage.DataRecords }
     */
    public ParsingDataCollectionPackage.DataRecords createDataCollectionPackageDataRecords() {
        return new ParsingDataCollectionPackage.DataRecords();
    }

    /**
     * Create an instance of {@link ParsingDataCollectionRecord }
     */
    public ParsingDataCollectionRecord createDataCollectionRecord() {
        return new ParsingDataCollectionRecord();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParsingDataCollectionPackage }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/ParsingDataCollectionPackage", name = "ParsingDataCollectionPackage")
    public JAXBElement<ParsingDataCollectionPackage> createDataCollectionPackage(ParsingDataCollectionPackage value) {
        return new JAXBElement<ParsingDataCollectionPackage>(_DataCollectionPackage_QNAME, ParsingDataCollectionPackage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParsingDataCollectionRecord }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/ParsingDataCollectionPackage", name = "ParsingDataCollectionRecord")
    public JAXBElement<ParsingDataCollectionRecord> createDataCollectionRecord(ParsingDataCollectionRecord value) {
        return new JAXBElement<ParsingDataCollectionRecord>(_DataCollectionRecord_QNAME, ParsingDataCollectionRecord.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/ParsingDataCollectionPackage", name = "sourceID")
    public JAXBElement<String> createSourceID(String value) {
        return new JAXBElement<String>(_SourceID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/ParsingDataCollectionPackage", name = "timestamp")
    public JAXBElement<XMLGregorianCalendar> createTimestamp(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_Timestamp_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/ParsingDataCollectionPackage", name = "eventID")
    public JAXBElement<String> createEventID(String value) {
        return new JAXBElement<String>(_EventID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/ParsingDataCollectionPackage", name = "collectedData")
    public JAXBElement<String> createCollectedData(String value) {
        return new JAXBElement<String>(_CollectedData_QNAME, String.class, null, value);
    }

}
