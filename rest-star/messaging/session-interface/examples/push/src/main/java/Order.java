import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name="order")
public class Order implements Serializable
{
   private String name;
   private String amount;
   private String item;

   public Order()
   {
   }

   public Order(String name, String amount, String item)
   {
      this.name = name;
      this.amount = amount;
      this.item = item;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getAmount()
   {
      return amount;
   }

   public void setAmount(String amount)
   {
      this.amount = amount;
   }

   public String getItem()
   {
      return item;
   }

   public void setItem(String item)
   {
      this.item = item;
   }

   @Override
   public String toString()
   {
      return "Order{" +
              "name='" + name + '\'' +
              ", amount='" + amount + '\'' +
              ", item='" + item + '\'' +
              '}';
   }
}
