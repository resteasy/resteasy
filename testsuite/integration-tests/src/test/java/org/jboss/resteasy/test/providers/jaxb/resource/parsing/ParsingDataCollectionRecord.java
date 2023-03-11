package org.jboss.resteasy.test.providers.jaxb.resource.parsing;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ParsingDataCollectionRecord complex type.
 * <p>
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <p>
 *
 * <pre>
 * &lt;complexType name="ParsingDataCollectionRecord">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="collectedData" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParsingDataCollectionRecord", propOrder = {
        "timestamp",
        "collectedData"
})
public class ParsingDataCollectionRecord {

    @XmlElement(required = true)
    protected XMLGregorianCalendar timestamp;
    @XmlElement(required = true)
    protected String collectedData;

    /**
     * Gets the value of the timestamp property.
     *
     * @return possible object is
     *         {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the collectedData property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getCollectedData() {
        return collectedData;
    }

    /**
     * Sets the value of the collectedData property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCollectedData(String value) {
        this.collectedData = value;
    }

}
