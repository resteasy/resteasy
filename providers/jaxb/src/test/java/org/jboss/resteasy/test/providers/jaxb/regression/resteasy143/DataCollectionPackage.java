package org.jboss.resteasy.test.providers.jaxb.regression.resteasy143;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for DataCollectionPackage complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="DataCollectionPackage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sourceID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="eventID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dataRecords">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DataCollectionRecord" type="{http://www.example.org/DataCollectionPackage}DataCollectionRecord" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataCollectionPackage",
        propOrder = {
                "sourceID",
                "eventID",
                "dataRecords"
        })
//@XmlRootElement
public class DataCollectionPackage extends AbstractData
{

   @XmlElement(name = "sourceID", required = true)
   protected String sourceID;
   @XmlElement(required = true)
   protected String eventID;
   @XmlElement(required = true)
   protected DataCollectionPackage.DataRecords dataRecords;

   /**
    * Gets the value of the sourceID property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getSourceID()
   {
      return sourceID;
   }

   /**
    * Sets the value of the sourceID property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setSourceID(String value)
   {
      this.sourceID = value;
   }

   /**
    * Gets the value of the eventID property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getEventID()
   {
      return eventID;
   }

   /**
    * Sets the value of the eventID property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setEventID(String value)
   {
      this.eventID = value;
   }

   /**
    * Gets the value of the dataRecords property.
    *
    * @return possible object is
    *         {@link DataCollectionPackage.DataRecords }
    */
   public DataCollectionPackage.DataRecords getDataRecords()
   {
      return dataRecords;
   }

   /**
    * Sets the value of the dataRecords property.
    *
    * @param value allowed object is
    *              {@link DataCollectionPackage.DataRecords }
    */
   public void setDataRecords(DataCollectionPackage.DataRecords value)
   {
      this.dataRecords = value;
   }


   /**
    * <p>Java class for anonymous complex type.
    * <p/>
    * <p>The following schema fragment specifies the expected content contained within this class.
    * <p/>
    * <pre>
    * &lt;complexType>
    *   &lt;complexContent>
    *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
    *       &lt;sequence>
    *         &lt;element name="DataCollectionRecord" type="{http://www.example.org/DataCollectionPackage}DataCollectionRecord" maxOccurs="unbounded"/>
    *       &lt;/sequence>
    *     &lt;/restriction>
    *   &lt;/complexContent>
    * &lt;/complexType>
    * </pre>
    */
   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlType(name = "", propOrder = {
           "dataCollectionRecord"
   })
   public static class DataRecords
   {

      @XmlElement(name = "DataCollectionRecord", required = true)
      protected List<DataCollectionRecord> dataCollectionRecord;

      /**
       * Gets the value of the dataCollectionRecord property.
       * <p/>
       * <p/>
       * This accessor method returns a reference to the live list,
       * not a snapshot. Therefore any modification you make to the
       * returned list will be present inside the JAXB object.
       * This is why there is not a <CODE>set</CODE> method for the dataCollectionRecord property.
       * <p/>
       * <p/>
       * For example, to add a new item, do as follows:
       * <pre>
       *    getDataCollectionRecord().add(newItem);
       * </pre>
       * <p/>
       * <p/>
       * <p/>
       * Objects of the following type(s) are allowed in the list
       * {@link DataCollectionRecord }
       */
      public List<DataCollectionRecord> getDataCollectionRecord()
      {
         if (dataCollectionRecord == null)
         {
            dataCollectionRecord = new ArrayList<DataCollectionRecord>();
         }
         return this.dataCollectionRecord;
      }


   }

}
