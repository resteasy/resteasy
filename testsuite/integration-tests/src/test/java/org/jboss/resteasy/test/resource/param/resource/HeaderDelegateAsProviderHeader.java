package org.jboss.resteasy.test.resource.param.resource;

public class HeaderDelegateAsProviderHeader {

   private String major;
   private String minor;

   public HeaderDelegateAsProviderHeader(final String major, final String minor) {
      this.major = major;
      this.minor = minor;
   }

   public String getMajor() {
      return major;
   }

   public void setMajor(String major) {
      this.major = major;
   }

   public String getMinor() {
      return minor;
   }

   public void setMinor(String minor) {
      this.minor = minor;
   }
}
