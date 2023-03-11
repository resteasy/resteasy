package org.jboss.resteasy.test.xxe.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
