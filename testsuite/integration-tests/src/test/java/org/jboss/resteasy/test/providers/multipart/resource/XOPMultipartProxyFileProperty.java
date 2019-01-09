package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class XOPMultipartProxyFileProperty implements Serializable{
   private static final long serialVersionUID = 1L;
   private String propName;
   private String value;

   public XOPMultipartProxyFileProperty(){

   }

   public XOPMultipartProxyFileProperty(final String propName, final String value){
      this.propName=propName;
      this.value=value;
   }

   public XOPMultipartProxyFileProperty(final XOPMultipartProxyEngine prop, final String value) {
      this.propName=prop.toString();
      this.value=value;
   }

   public void setPropName(String key) {
      this.propName = key;
   }
   public String getPropName() {
      return propName;
   }
   public void setValue(String value) {
      this.value = value;
   }
   public String getValue() {
      return value;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((propName == null) ? 0 : propName.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj){
         return true;
      }
      if (obj == null){
         return false;
      }
      if (getClass() != obj.getClass()){
         return false;
      }
      XOPMultipartProxyFileProperty other = (XOPMultipartProxyFileProperty) obj;
      if (propName == null) {
         if (other.propName != null){
            return false;
         }
      } else if (!propName.equals(other.propName)){
         return false;
      }
      if (value == null) {
         if (other.value != null){
            return false;
         }
      } else if (!value.equals(other.value)){
         return false;
      }
      return true;
   }
}
