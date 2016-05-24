package org.jboss.resteasy.test.providers.jaxb.regression;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.YEAR;

@XmlRootElement
@XmlSeeAlso({
        Firmenkunde.class,
        Privatkunde.class
})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Kunde implements java.io.Serializable
{
   private static final long serialVersionUID = 8488010636885492122L;

   public static final int NACHNAME_LENGTH_MIN = 2;
   public static final int NACHNAME_LENGTH_MAX = 32;
   public static final int VORNAME_LENGTH_MAX = 32;
   public static final int KUNDENNR_LENGTH_MAX = 32;
   public static final int DETAILS_LENGTH_MAX = 128 * 1024;
   public static final int PASSWORD_LENGTH_MAX = 256;

   public static final String PRIVATKUNDE = "P";
   public static final String FIRMENKUNDE = "F";

   static final String FIND_KUNDEN = "findKunden";
   static final String FIND_KUNDEN_BY_NACHNAME = "findKundenByNachname";
   static final String FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN = "findKundenByNachnameFetchBestellungen";
   static final String FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN = "findKundeByIdFetchBestellungen";
   static final String FIND_KUNDEN_BY_PLZ = "findKundenByPlz";

   static final String PARAM_KUNDE_ID = "kundeId";
   static final String PARAM_KUNDE_NACHNAME = "nachname";
   static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";

   // Alternativen: TABLE, SEQUENCE, IDENTITY, AUTO, NONE (=default)
   @XmlAttribute(name = "id", required = true)
   protected Long id = -1L;

   @XmlTransient
   protected int version = 0;

   @XmlElement(required = true)
   protected String nachname = "";

   protected String vorname = "";

   @XmlAttribute(required = true)
   protected String kundennr = "NnVn-001";

   protected Date seit = null;

   @XmlTransient
   protected int anzJahre;

   @XmlElement(name = "betreuer")
   protected String betreuerKey;

   @XmlElementWrapper(name = "bestellungen")
   @XmlElement(name = "bestellung")
   protected List<String> bestellungenKeys;

   protected String details;

   @XmlTransient
   protected String password = "";

   @XmlTransient
   protected Date erzeugt = null;

   @XmlTransient
   protected Date aktualisiert = null;

   public Kunde()
   {
      super();
   }

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public int getVersion()
   {
      return version;
   }

   public void setVersion(int version)
   {
      this.version = version;
   }

   public String getNachname()
   {
      return nachname;
   }

   public void setNachname(String nachname)
   {
      this.nachname = nachname;
   }

   public String getVorname()
   {
      return vorname;
   }

   public void setVorname(String vorname)
   {
      this.vorname = vorname;
   }

   public String getKundennr()
   {
      return kundennr;
   }

   public void setKundennr(String kundennr)
   {
      this.kundennr = kundennr;
   }

   public Date getSeit()
   {
      return seit;
   }

   public void setSeit(Date seit)
   {
      this.seit = seit;
   }

   public int getAnzJahre()
   {
      final GregorianCalendar now = new GregorianCalendar();
      final GregorianCalendar seitCal = new GregorianCalendar();
      Date temp = seit;
      if (temp == null)
         temp = new Date();
      seitCal.setTime(temp);

      anzJahre = now.get(YEAR) - seitCal.get(YEAR);

      return anzJahre;
   }

   // Parameter, z.B. DateFormat.MEDIUM, Locale.GERMANY
   // MEDIUM fuer Format dd.MM.yyyy
   public String getSeitAsString(int style, Locale locale)
   {
      Date temp = seit;
      if (temp == null)
         temp = new Date();
      final DateFormat f = DateFormat.getDateInstance(style, locale);
      return f.format(temp);
   }

   // Parameter, z.B. DateFormat.MEDIUM, Locale.GERMANY
   // MEDIUM fuer Format dd.MM.yyyy
   public void setSeit(String seit, int style, Locale locale)
   {
      final DateFormat f = DateFormat.getDateInstance(style, locale);
      try
      {
         this.seit = f.parse(seit);
      }
      catch (ParseException e)
      {
      }
   }

   public String getBetreuerKey()
   {
      return betreuerKey;
   }

   public void setBetreuerKey(String betreuerKey)
   {
      this.betreuerKey = betreuerKey;
   }

   public List<String> getBestellungenKeys()
   {
      return bestellungenKeys;
   }

   public void setBestellungenKeys(List<String> bestellungenKeys)
   {
      this.bestellungenKeys = bestellungenKeys;
   }

   abstract public String getArt();

   public String getDetails()
   {
      return details;
   }

   public void setDetails(String details)
   {
      this.details = details;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String passwort)
   {
      this.password = passwort;
   }

   public Date getAktualisiert()
   {
      return aktualisiert;
   }

   public void setAktualisiert(Date aktualisiert)
   {
      this.aktualisiert = aktualisiert;
   }

   public Date getErzeugt()
   {
      return erzeugt;
   }

   public void setErzeugt(Date erzeugt)
   {
      this.erzeugt = erzeugt;
   }

   @Override
   public String toString()
   {
      return "id=" + id + ", version=" + version +
              ", nachname=" + nachname + ", vorname=" + vorname +
              ", nr=" + kundennr +
              ", seit=" + getSeitAsString(DateFormat.MEDIUM, Locale.GERMANY) +
              ", anzJahre=" + getAnzJahre() +
              ", password=" + password +
              ", erzeugt=" + erzeugt +
              ", aktualisiert=" + aktualisiert;
   }

   @Override
   public int hashCode()
   {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((nachname == null) ? 0 : nachname.hashCode());
      result = PRIME * result + ((seit == null) ? 0 : seit.hashCode());
      result = PRIME * result + ((vorname == null) ? 0 : vorname.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final Kunde other = (Kunde) obj;
      if (nachname == null)
      {
         if (other.nachname != null)
            return false;
      }
      else if (!nachname.equals(other.nachname))
         return false;
      if (seit == null)
      {
         if (other.seit != null)
            return false;
      }
      else if (!seit.equals(other.seit))
         return false;
      if (vorname == null)
      {
         if (other.vorname != null)
            return false;
      }
      else if (!vorname.equals(other.vorname))
         return false;
      return true;
   }
}