
package javax.ws.rs.core;

/*
import com.sun.localization.Localizable;
import com.sun.localization.LocalizableMessageFactory;
import com.sun.localization.Localizer;*/


/**
 * Defines string formatting method for each constant in the resource file
 * 
 */
public final class ApiMessages {
   /*

    private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("javax.ws.rs.core.api");
    private final static Localizer localizer = new Localizer();

    public static Localizable localizableILLEGAL_CONFIG_SYNTAX() {
        return messageFactory.getMessage("illegal.config.syntax");
    } */

    /**
     * Illegal configuration-file syntax.
     * 
     */
    public static String ILLEGAL_CONFIG_SYNTAX() {
        return "Illegal config syntax"; //localizer.localize(localizableILLEGAL_CONFIG_SYNTAX());
    }

   /*
    public static Localizable localizablePROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1) {
        return messageFactory.getMessage("provider.could.not.be.created", arg0, arg1);
    }  */

    /**
     * Provider {0} could not be instantiated: {1}
     * 
     */
    public static String PROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1) {
        return "Provider could not be created";//localizer.localize(localizablePROVIDER_COULD_NOT_BE_CREATED(arg0, arg1));
    }

   /*
    public static Localizable localizableNO_BUILDER_IMPL() {
        return messageFactory.getMessage("no.builder.impl");
    } */

    /**
     * No Builder implementation found.
     * 
     */
    public static String NO_BUILDER_IMPL() {
        return "No Builder IMPL";//localizer.localize(localizableNO_BUILDER_IMPL());
    }

   /*
    public static Localizable localizableCOOKIE_INVALID(Object arg0) {
        return messageFactory.getMessage("cookie.invalid", arg0);
    }*/

    /**
     * Invalid cookie: {0}
     * 
     */
    public static String COOKIE_INVALID(Object arg0) {
        return "Cookie Invalid"; //localizer.localize(localizableCOOKIE_INVALID(arg0));
    }
   /*

    public static Localizable localizableCACHE_CONTROL_INVALID(Object arg0) {
        return messageFactory.getMessage("cache.control.invalid", arg0);
    } */

    /**
     * Invalid cache control: {0}
     * 
     */
    public static String CACHE_CONTROL_INVALID(Object arg0) {
        return "Cache control invalid";//localizer.localize(localizableCACHE_CONTROL_INVALID(arg0));

    }
   /*
    public static Localizable localizableILLEGAL_PROVIDER_CLASS_NAME(Object arg0) {
        return messageFactory.getMessage("illegal.provider.class.name", arg0);
    }*/

    /**
     * Illegal provider-class name: {0}.
     * 
     */
    public static String ILLEGAL_PROVIDER_CLASS_NAME(Object arg0) {
        return "Illegal Provider Class Name"; //localizer.localize(localizableILLEGAL_PROVIDER_CLASS_NAME(arg0));
    }

   /*
    public static Localizable localizableMEDIA_TYPE_CANNOT_BE_NULL() {
        return messageFactory.getMessage("media.type.cannot.be.null");
    }*/

    /**
     * mediaType parameter cannot be null.
     * 
     */
    public static String MEDIA_TYPE_CANNOT_BE_NULL() {
        return "Media Type Cannot Be Null"; //localizer.localize(localizableMEDIA_TYPE_CANNOT_BE_NULL());
    }
   /*

    public static Localizable localizablePROVIDER_NOT_FOUND(Object arg0) {
        return messageFactory.getMessage("provider.not.found", arg0);
    } */

    /**
     * Provider {0} not found.
     * 
     */
    public static String PROVIDER_NOT_FOUND(Object arg0) {
        return "Provider Not Found";//localizer.localize(localizablePROVIDER_NOT_FOUND(arg0));
    }

   /*
    public static Localizable localizableCONTENT_CANNOT_BE_NULL() {
        return messageFactory.getMessage("content.cannot.be.null");
    }*/

    /**
     * content parameter cannot be null.
     * 
     */
    public static String CONTENT_CANNOT_BE_NULL() {
        return "Content cannot be null";//localizer.localize(localizableCONTENT_CANNOT_BE_NULL());
    }

   /*
    public static Localizable localizableMEDIA_TYPE_INVALID(Object arg0) {
        return messageFactory.getMessage("media.type.invalid", arg0);
    }*/

    /**
     * Invalid media type: {0}
     * 
     */
    public static String MEDIA_TYPE_INVALID(Object arg0) {
        return "Media type invalid"; //localizer.localize(localizableMEDIA_TYPE_INVALID(arg0));
    }
    /*
    public static Localizable localizableETAG_INVALID(Object arg0) {
        return messageFactory.getMessage("etag.invalid", arg0);
    }*/

    /**
     * Invalid entity tag: {0}
     * 
     */
    public static String ETAG_INVALID(Object arg0) {
        return "ETAG Invalid";//localizer.localize(localizableETAG_INVALID(arg0));
    }

}
