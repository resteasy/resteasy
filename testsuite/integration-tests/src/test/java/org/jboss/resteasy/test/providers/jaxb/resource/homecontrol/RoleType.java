package org.jboss.resteasy.test.providers.jaxb.resource.homecontrol;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RoleType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="RoleType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="USER"/&gt;
 *     &lt;enumeration value="ADMIN"/&gt;
 *     &lt;enumeration value="BACKUP_USER"/&gt;
 *     &lt;enumeration value="BACKUP_ADMIN"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RoleType")
@XmlEnum
public enum RoleType {


   /**
    * standard role which is needed to access the homecontrol application
    *
    *
    */
   USER,

   /**
    * Administration user for administrate the whole homecontrol application
    *
    *
    */
   ADMIN,

   /**
    * User for the backup module of homecontrol
    *
    *
    */
   BACKUP_USER,

   /**
    * Admin user for administrating the backup module of homecontrol
    *
    */
   BACKUP_ADMIN;

   public String value() {
      return name();
   }

   public static RoleType fromValue(String v) {
      return valueOf(v);
   }

}
