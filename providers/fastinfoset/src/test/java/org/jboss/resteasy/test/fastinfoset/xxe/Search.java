package org.jboss.resteasy.test.fastinfoset.xxe;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="search")
   public class Search {
     private String _search;
     public String getSearch() {
       return _search;
     }
     public void setSearch(String search) {
       _search = search;
     }
   }