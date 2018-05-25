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
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Per RFC 5023:
 * </p>
 * 
 * <pre>
 * The root of a Service Document is the "app:service" element.
 * 
 * The app:service element is the container for service information
 * associated with one or more Workspaces.  An app:service element MUST
 * contain one or more app:workspace elements.
 * 
 * namespace app = "http://www.w3.org/2007/app"
 * start = appService
 * 
 * appService =
 *    element app:service {
 *        appCommonAttributes,
 *       ( appWorkspace+
 *         {@literal &} extensionElement* )
 *    }
 *    
 * For authoring to commence, a client needs to discover the
 * capabilities and locations of the available Collections.  Service
 * Documents are designed to support this discovery process.
 * 
 * How Service Documents are discovered is not defined in this
 * specification. 
 * 
 * Service Documents are identified with the "application/atomsvc+xml"
 * media type
 * </pre>
 * 
 * @author <a href="mailto:kurt.stam@gmail.com">Kurt Stam</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "service")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppService extends AppCommonAttributes {
    
    private static final long serialVersionUID = 1090747778031855442L;
    private List<AppWorkspace> workspace = new ArrayList<AppWorkspace>();

    public List<AppWorkspace> getWorkspace() {
        return workspace;
    }

}
