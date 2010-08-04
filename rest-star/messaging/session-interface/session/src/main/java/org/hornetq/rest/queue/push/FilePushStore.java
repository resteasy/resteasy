package org.hornetq.rest.queue.push;

import org.hornetq.core.logging.Logger;
import org.hornetq.rest.queue.push.xml.PushRegistration;
import org.hornetq.rest.topic.PushTopicRegistration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
   private static final Logger log = Logger.getLogger(FilePushStore.class);
   protected Map<String, PushRegistration> map = new HashMap<String, PushRegistration>();
   protected File dir;
   protected JAXBContext ctx;

   public FilePushStore(String dirname) throws Exception
   {
      this.dir = new File(dirname);
      this.ctx = JAXBContext.newInstance(PushRegistration.class, PushTopicRegistration.class);
      if (this.dir.exists())
      {
         log.info("Loading REST push store from: " + this.dir.getAbsolutePath());
         for (File file : this.dir.listFiles())
         {
            if (!file.isFile()) continue;
            PushRegistration reg = null;
            try
            {
               reg = (PushRegistration) ctx.createUnmarshaller().unmarshal(file);
               reg.setLoadedFrom(file);
               log.info("adding REST push registration: " + reg.getId());
               map.put(reg.getId(), reg);
            }
            catch (Exception e)
            {
               log.error("Failed to load push store" + file.getName() + " , it is probably corrupted", e);
            }
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
   public synchronized void update(PushRegistration reg) throws Exception
   {
      if (reg.getLoadedFrom() == null) return;
      save(reg);
   }

   protected void save(PushRegistration reg)
           throws JAXBException
   {
      Marshaller marshaller = ctx.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(reg, (File) reg.getLoadedFrom());
   }

   @Override
   public synchronized void add(PushRegistration reg) throws Exception
   {
      map.put(reg.getId(), reg);
      if (!this.dir.exists()) this.dir.mkdirs();
      File fp = new File(dir, "reg-" + reg.getId() + ".xml");
      reg.setLoadedFrom(fp);
      //System.out.println("******* Saving: " + fp.getAbsolutePath());
      save(reg);
   }

   @Override
   public synchronized void remove(PushRegistration reg) throws Exception
   {
      map.remove(reg.getId());
      if (reg.getLoadedFrom() == null) return;
      File fp = (File) reg.getLoadedFrom();
      fp.delete();
   }

   @Override
   public synchronized void removeAll() throws Exception
   {
      ArrayList<PushRegistration> copy = new ArrayList<PushRegistration>();
      copy.addAll(map.values());
      for (PushRegistration reg : copy) remove(reg);
      this.dir.delete();
   }
}
