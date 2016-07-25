package org.jboss.resteasy.test.xxe.resource;

import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public class XXEBasicResource {
    private static Logger logger = Logger.getLogger(XXEBasicResource.class);
    @Consumes("application/xml")
    @POST
    public String doPost(Document doc) {
        Node node = doc.getDocumentElement();
        logger.info("name: " + node.getNodeName());
        NodeList children = doc.getDocumentElement().getChildNodes();

        node = children.item(0);
        logger.info("name: " + node.getNodeName());
        children = node.getChildNodes();

        node = children.item(0);
        logger.info("name: " + node.getNodeName());
        children = node.getChildNodes();

        logger.info(node.getNodeValue());
        return node.getNodeValue();
    }
}
