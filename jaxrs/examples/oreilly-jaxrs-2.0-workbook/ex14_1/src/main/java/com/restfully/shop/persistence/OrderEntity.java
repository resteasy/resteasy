package com.restfully.shop.persistence;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Entity(name = "PurchaseOrder")
public class OrderEntity
{
   protected int id;
   protected boolean cancelled;
   protected List<LineItemEntity> lineItems = new ArrayList<LineItemEntity>();
   protected double total;
   protected String date;
   protected CustomerEntity customer;

   @Id
   @GeneratedValue
   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public boolean isCancelled()
   {
      return cancelled;
   }

   public void setCancelled(boolean cancelled)
   {
      this.cancelled = cancelled;
   }

   @OneToMany(cascade = CascadeType.ALL)
   public List<LineItemEntity> getLineItems()
   {
      return lineItems;
   }

   public void setLineItems(List<LineItemEntity> lineItems)
   {
      this.lineItems = lineItems;
   }

   public String getDate()
   {
      return date;
   }

   public void setDate(String date)
   {
      this.date = date;
   }

   public double getTotal()
   {
      return total;
   }

   public void setTotal(double total)
   {
      this.total = total;
   }

   @ManyToOne
   public CustomerEntity getCustomer()
   {
      return customer;
   }

   public void setCustomer(CustomerEntity customer)
   {
      this.customer = customer;
   }
}