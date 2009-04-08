package org.jboss.resteasy.examples.twitter;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jboss.resteasy.util.DateUtil;

public class DateAdapter extends XmlAdapter<String, Date> {

   @Override
   public String marshal(Date date) throws Exception {
       return DateUtil.formatDate(date, "EEE MMM dd HH:mm:ss Z yyyy");
   }

   @Override
   public Date unmarshal(String string) throws Exception {
       try {
           return DateUtil.parseDate(string);
       } catch (IllegalArgumentException e) {
           System.err.println(String.format(
                   "Could not parse date string '%s'", string));
           return null;
       }
   }
}
