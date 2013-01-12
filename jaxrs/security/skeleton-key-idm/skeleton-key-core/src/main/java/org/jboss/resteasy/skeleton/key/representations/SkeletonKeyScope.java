package org.jboss.resteasy.skeleton.key.representations;

import org.codehaus.jackson.annotate.JsonValue;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Key is resource desired.  Values are roles desired for that resource
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyScope extends MultivaluedHashMap<String, String>
{
}
