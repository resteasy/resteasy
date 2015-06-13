package org.jboss.resteasy.resteasy1119;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 10, 2015
 */
public class CustomerForm
{
   @FormParam("customer")
   @PartType("application/xml")
   private Customer customer;

   public Customer getCustomer() { return customer; }
   public void setCustomer(Customer cust) { this.customer = cust; }
}
