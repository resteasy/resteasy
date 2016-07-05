package org.jboss.resteasy.test.providers.atom;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the mil.navy.tsts.datacollection.parseRS
 * package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DataCollectionPackage_QNAME = new QName(
            "http://www.example.org/DataCollectionPackage", "DataCollectionPackage");
    private final static QName _DataCollectionRecord_QNAME = new QName(
            "http://www.example.org/DataCollectionPackage", "DataCollectionRecord");
    private final static QName _SourceID_QNAME = new QName(
            "http://www.example.org/DataCollectionPackage", "sourceID");
    private final static QName _Timestamp_QNAME = new QName(
            "http://www.example.org/DataCollectionPackage", "timestamp");
    private final static QName _EventID_QNAME = new QName(
            "http://www.example.org/DataCollectionPackage", "eventID");
    private final static QName _CollectedData_QNAME = new QName(
            "http://www.example.org/DataCollectionPackage", "collectedData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: mil.navy.tsts.datacollection.parseRS
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DataCollectionPackage }
     */
    public DataCollectionPackage createDataCollectionPackage() {
        return new DataCollectionPackage();
    }

    /**
     * Create an instance of {@link DataCollectionPackage.DataRecords }
     */
    public DataCollectionPackage.DataRecords createDataCollectionPackageDataRecords() {
        return new DataCollectionPackage.DataRecords();
    }

    /**
     * Create an instance of {@link DataCollectionRecord }
     */
    public DataCollectionRecord createDataCollectionRecord() {
        return new DataCollectionRecord();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link DataCollectionPackage }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.example.org/DataCollectionPackage", name = "DataCollectionPackage")
    public JAXBElement<DataCollectionPackage> createDataCollectionPackage(
            DataCollectionPackage value) {
        return new JAXBElement<DataCollectionPackage>(_DataCollectionPackage_QNAME,
                DataCollectionPackage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link DataCollectionRecord }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.example.org/DataCollectionPackage", name = "DataCollectionRecord")
    public JAXBElement<DataCollectionRecord> createDataCollectionRecord(DataCollectionRecord value) {
        return new JAXBElement<DataCollectionRecord>(_DataCollectionRecord_QNAME,
                DataCollectionRecord.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.example.org/DataCollectionPackage", name = "sourceID")
    public JAXBElement<String> createSourceID(String value) {
        return new JAXBElement<String>(_SourceID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.example.org/DataCollectionPackage", name = "timestamp")
    public JAXBElement<XMLGregorianCalendar> createTimestamp(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_Timestamp_QNAME, XMLGregorianCalendar.class,
                null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.example.org/DataCollectionPackage", name = "eventID")
    public JAXBElement<String> createEventID(String value) {
        return new JAXBElement<String>(_EventID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.example.org/DataCollectionPackage", name = "collectedData")
    public JAXBElement<String> createCollectedData(String value) {
        return new JAXBElement<String>(_CollectedData_QNAME, String.class, null, value);
    }

}
