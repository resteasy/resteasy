package org.jboss.resteasy.test.providers.jaxb.resource.parsing;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

@Path("/")
public class ParsingStoreResource {
    // this unitName matches the one defined in persistence.xml file
    //@PersistenceContext(unitName="DataCollectionPU")
    //EntityManager em;

    @POST
    @Consumes("application/xml")
    @Path("storeXML/abstract")
    public Response storeXMLAbstract(ParsingAbstractData dataCollectionPackage) {
        return storeXML((ParsingDataCollectionPackage) dataCollectionPackage);
    }

    @POST
    @Consumes("application/xml")
    @Path("storeXML")
    public Response storeXML(ParsingDataCollectionPackage dataCollectionPackage) {
        String sourceID = dataCollectionPackage.getSourceID();
        String eventID = dataCollectionPackage.getEventID();

        if (dataCollectionPackage.getDataRecords() == null ||
                dataCollectionPackage.getDataRecords().getDataCollectionRecord() == null) {
            ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR);
            builder.type("text/html");
            builder.entity("<h3>No records included</h3>");
            throw new WebApplicationException(builder.build());
        }

        for (ParsingDataCollectionRecord dr : dataCollectionPackage.getDataRecords().getDataCollectionRecord()) {

            // JAXB datatype for dateTime is XMLGregorianCalendar, need to convert to the java.sql.Timestamp
            XMLGregorianCalendar cal = dr.getTimestamp();
            GregorianCalendar gregorianCalendar = cal.toGregorianCalendar();
            long timeAsMillis = gregorianCalendar.getTimeInMillis();
            Timestamp timestamp = new Timestamp(timeAsMillis);

            // persist data here
        }

        URI createdURI = null;
        try {
            // Create a relative URI
            createdURI = new URI("records");
        } catch (URISyntaxException e) {
            throw new WebApplicationException(Response.serverError().build());
        }
        // For a successful POST send a 201 CREATED with the URI of where to find the records
        // The relative URI will be turned into an absolute URI based on the URI used to access
        // this method.
        //return Response.created(createdURI).build();
        ResponseBuilder builder = Response.created(createdURI);
        builder.type("text/plain");
        // SoapUI considers a 0 length response an error
        builder.entity("storeXML");
        return builder.build();
    }
}
