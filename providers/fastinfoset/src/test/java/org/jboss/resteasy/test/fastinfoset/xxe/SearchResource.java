package org.jboss.resteasy.test.fastinfoset.xxe;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBElement;

import org.jboss.resteasy.test.fastinfoset.xxe.generated.SearchType;

@Path("/")
   public class SearchResource
   {
     @POST
     @Path("xmlRootElement")
     @Consumes({"application/*+fastinfoset"})
     public String addSearch(Search search)
     {
        System.out.println("SearchResource(xmlRootElment): search = " + search.getSearch());
        return concat("a", search.getSearch(), "b");
     }

     @POST
     @Path("xmlType")
     @Consumes({"application/*+fastinfoset"})
     public String addSearch(SearchType search)
     {
        System.out.println("SearchResource(xmlType): id = " + search.getId());
        return concat("a", search.getId(), "b");
     }

     @POST
     @Path("JAXBElement")
     @Consumes("application/*+fastinfoset")
     public String addSearch(JAXBElement<Search> value)
     {
        System.out.println("SearchResource(JAXBElement): search = " + value.getValue().getSearch());
        return concat("a", value.getValue().getSearch(), "b");
     }

	 String concat(String s1, Object o, String s2)
	 {
	    return s1 + (o == null ? "" : o.toString()) + s2;
	 }
   }