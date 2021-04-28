package org.jboss.resteasy.plugins.providers.atom.app;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
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
