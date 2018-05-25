/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.jboss.resteasy.plugins.providers.atom.app;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Per RFC5023:</p>
 * <pre>
 * A Service Document groups Collections into Workspaces.  Operations on
 * Workspaces, such as creation or deletion, are not defined by this
 * specification.  This specification assigns no meaning to Workspaces;
 * that is, a Workspace does not imply any specific processing
 * assumptions.
 * 
 * There is no requirement that a server support multiple Workspaces.
 * In addition, a Collection MAY appear in more than one Workspace.
 * 
 * Workspaces are server-defined groups of Collections.  The "app:
 * workspace" element contains zero or more app:collection elements
 * describing the Collections of Resources available for editing.
 * 
 * appWorkspace =
 *    element app:workspace {
 *       appCommonAttributes,
 *       ( atomTitle
 *         {@literal &} appCollection*
 *         {@literal &} extensionSansTitleElement* )
 *    }
 * 
 * atomTitle = element atom:title { atomTextConstruct }
 *
 * </pre>
 *
 * @author <a href="mailto:kurt.stam@gmail.com">Kurt Stam</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AppWorkspace extends AppCommonAttributes
{
    private static final long serialVersionUID = -2595744438212041512L;
    @XmlElement(namespace = "http://www.w3.org/2005/Atom", required = true)
    protected String title;
    
    protected List<AppCollection> collection;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the collection property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the collection property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCollection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AppCollection }
     * 
     * @return list of {@link AppCollection}
     * 
     */
    public List<AppCollection> getCollection() {
        if (collection == null) {
            collection = new ArrayList<AppCollection>();
        }
        return this.collection;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.w3c.dom.Element }
     * {@link Object }
     * 
     * @return list of objects
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }
}
