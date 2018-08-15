package org.jboss.resteasy.test.providers.jaxb.resource.homecontrol;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für BinaryType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="BinaryType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;restriction base="&lt;http://www.w3.org/2005/05/xmlmime&gt;base64Binary"&gt;
 *       &lt;attribute ref="{http://www.w3.org/2005/05/xmlmime}contentType use="required""/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryType")
public class BinaryType
        extends Base64Binary {


}
