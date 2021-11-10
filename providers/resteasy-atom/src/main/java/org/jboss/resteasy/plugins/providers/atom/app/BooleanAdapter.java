package org.jboss.resteasy.plugins.providers.atom.app;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanAdapter extends XmlAdapter<String, Boolean>
{
   @Override
   public Boolean unmarshal( String yesno )
   {
      return yesno == null ? null : yesno.toLowerCase().equals("yes");
   }

   @Override
   public String marshal( Boolean c )
   {
      return c == null ? null : c ? "yes" : "no";
   }
}
