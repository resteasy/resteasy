package org.hornetq.rest.queue.push;

import org.hornetq.rest.queue.push.xml.PushRegistration;
import org.hornetq.rest.topic.PushTopicRegistration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FilePushStore implements PushStore
{
   @XmlRootElement(name = "push-store")
   @XmlAccessorType(XmlAccessType.PROPERTY)
   protected static class Store
   {
      private List<PushRegistration> list = new ArrayList<PushRegistration>();

      @XmlElementRef
      public List<PushRegistration> getList()
      {
         return list;
      }

      public void setList(List<PushRegistration> list)
      {
         this.list = list;
      }
   }

   protected Map<String, PushRegistration> map = new HashMap<String, PushRegistration>();
   protected File file;
   protected JAXBContext ctx;

   public FilePushStore(String filename) throws Exception
   {
      file = new File(filename);
      this.ctx = JAXBContext.newInstance(Store.class, PushRegistration.class, PushTopicRegistration.class);
      if (file.exists())
      {
         Store store = null;
         try
         {
            store = (Store) ctx.createUnmarshaller().unmarshal(file);
         }
         catch (Exception e)
         {
            System.err.println("Failed to load push store" + filename + " , it is probably corrupted");
         }
         for (PushRegistration reg : store.getList())
         {
            System.out.println("adding registration: " + reg.getId());
            map.put(reg.getId(), reg);
         }
      }
   }

   public synchronized List<PushRegistration> getRegistrations()
   {
      List<PushRegistration> list = new ArrayList<PushRegistration>();
      list.addAll(map.values());
      return list;
   }

   @Override
   public synchronized List<PushRegistration> getByDestination(String destination)
   {
      List<PushRegistration> list = new ArrayList<PushRegistration>();
      for (PushRegistration reg : map.values())
      {
         if (reg.getDestination().equals(destination))
         {
            list.add(reg);
         }
      }
      return list;
   }

   @Override
   public synchronized void add(PushRegistration reg) throws Exception
   {
      map.put(reg.getId(), reg);
      save();
   }

   @Override
   public synchronized void remove(PushRegistration reg) throws Exception
   {
      map.remove(reg.getId());
      save();
   }

   protected void save()
           throws JAXBException
   {
      Store store = new Store();
      store.getList().addAll(map.values());
      ctx.createMarshaller().marshal(store, System.out);
      ctx.createMarshaller().marshal(store, file);
   }


}
