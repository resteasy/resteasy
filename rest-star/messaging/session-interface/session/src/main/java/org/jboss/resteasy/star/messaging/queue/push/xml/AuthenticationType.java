package org.jboss.resteasy.star.messaging.queue.push.xml;

import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlSeeAlso({BasicAuth.class, DigestAuth.class})
public class AuthenticationType implements Serializable
{
}
