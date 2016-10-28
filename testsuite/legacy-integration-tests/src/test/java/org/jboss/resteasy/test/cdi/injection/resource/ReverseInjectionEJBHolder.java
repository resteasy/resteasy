package org.jboss.resteasy.test.cdi.injection.resource;


import org.jboss.resteasy.test.cdi.util.Utilities;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.logging.Logger;

@Stateful
@RequestScoped
public class ReverseInjectionEJBHolder implements ReverseInjectionEJBHolderRemote, ReverseInjectionEJBHolderLocal {
    public static final String SLE = "sle";
    public static final String SFDE = "sfde";
    public static final String SFRE = "sfre";
    public static final String SFAE = "sfae";
    public static final String SLI = "sli";
    public static final String SFDI = "sfdi";
    public static final String SFRI = "sfri";
    public static final String SFAI = "sfai";
    public static final String SLI_SECRET = "sliSecret";
    public static final String SFDI_SECRET = "sfdiSecret";
    public static final String SFRI_SECRET = "sfriSecret";
    public static final String SFAI_SECRET = "sfaiSecret";

    private static HashMap<String, Object> store = new HashMap<String, Object>();

    @Inject
    private Logger log;
    @Inject
    private Utilities utilities;
    @Inject
    int secret;

    @EJB
    private StatelessEJBwithJaxRsComponentsInterface sle;
    @EJB
    private StatefulDependentScopedEJBwithJaxRsComponentsInterface sfde;
    @EJB
    private StatefulRequestScopedEJBwithJaxRsComponentsInterface sfre;
    @EJB
    private StatefulApplicationScopedEJBwithJaxRsComponentsInterface sfae;

    @Inject
    private StatelessEJBwithJaxRsComponentsInterface sli;
    @Inject
    private StatefulDependentScopedEJBwithJaxRsComponentsInterface sfdi;
    @Inject
    private StatefulRequestScopedEJBwithJaxRsComponentsInterface sfri;
    @Inject
    private StatefulApplicationScopedEJBwithJaxRsComponentsInterface sfai;

    @Inject
    private CDIInjectionBookResource resource;

    @PostConstruct
    public void postConstruct() {
        log.info(this + " secret: " + secret);
    }

    @Override
    public boolean testScopes() {
        log.info("");
        log.info("entering ReverseInjectionEJBHolder.testScopes()");
        log.info("resource scope:                                                 " + utilities.getScope(CDIInjectionBookResource.class));
        log.info("ReverseInjectionEJBHolder scope:                                                " + utilities.getScope(ReverseInjectionEJBHolder.class));
        log.info("ReverseInjectionEJBHolderLocal scope:                                           " + utilities.getScope(ReverseInjectionEJBHolderLocal.class));
        log.info("ReverseInjectionEJBHolderRemote scope:                                          " + utilities.getScope(ReverseInjectionEJBHolderRemote.class));
        log.info("StatelessEJBwithJaxRsComponents scope:                          " + utilities.getScope(StatelessEJBwithJaxRsComponents.class));
        log.info("StatelessEJBwithJaxRsComponentsInterface scope:                 " + utilities.getScope(StatelessEJBwithJaxRsComponentsInterface.class));
        log.info("StatefulDependentScopedEJBwithJaxRsComponents scope:            " + utilities.getScope(StatefulDependentScopedEJBwithJaxRsComponents.class));
        log.info("StatefulDependentScopedEJBwithJaxRsComponentsInterface scope:   " + utilities.getScope(StatefulDependentScopedEJBwithJaxRsComponentsInterface.class));
        log.info("StatefulRequestScopedEJBwithJaxRsComponents scope:              " + utilities.getScope(StatefulRequestScopedEJBwithJaxRsComponents.class));
        log.info("StatefulRequestScopedEJBwithJaxRsComponentsInterface scope:     " + utilities.getScope(StatefulRequestScopedEJBwithJaxRsComponentsInterface.class));
        log.info("StatefulApplicationScopedEJBwithJaxRsComponents scope:          " + utilities.getScope(StatefulApplicationScopedEJBwithJaxRsComponents.class));
        log.info("StatefulApplicationScopedEJBwithJaxRsComponentsInterface scope: " + utilities.getScope(StatefulApplicationScopedEJBwithJaxRsComponentsInterface.class));

        return utilities.isDependentScoped(StatelessEJBwithJaxRsComponentsInterface.class) &&
                utilities.isDependentScoped(StatefulDependentScopedEJBwithJaxRsComponentsInterface.class) &&
                utilities.isRequestScoped(StatefulRequestScopedEJBwithJaxRsComponentsInterface.class) &&
                utilities.isApplicationScoped(StatefulApplicationScopedEJBwithJaxRsComponentsInterface.class);
    }

    @Override
    public void setup() {
        log.info("");
        log.info("entering ReverseInjectionEJBHolder.setup()");
        resource.getSet().add(new CDIInjectionBook("Disappearing Book"));
        store.put("sle", sle);
        store.put("sfde", sfde);
        store.put("sfre", sfre);
        store.put("sfae", sfae);
        store.put("sli", sli);
        store.put("sfdi", sfdi);
        store.put("sfri", sfri);
        store.put("sfai", sfai);
        store.put("sli.secret", sli.theSecret());
        store.put("sfdi.secret", sfdi.theSecret());
        store.put("sfri.secret", sfri.theSecret());
        store.put("sfai.secret", sfai.theSecret());

        sleSetup();
        sfdeSetup();
        sfreSetup();
        sfaeSetup();

        sliSetup();
        sfdiSetup();
        sfriSetup();
        sfaiSetup();
    }

    @Override
    public boolean test() {
        log.info("");
        log.info("entering ReverseInjectionEJBHolder.test()");

        boolean result = true;
        result &= resource.getSet().isEmpty();

        // @TODO inject singleton
        // @TODO inject by setter method
        result &= store.get("sle").equals(sle);    // EJB spec 3.4.7.1
        result &= !store.get("sfde").equals(sfde);  // EJB spec 3.4.7.2, 16.2.1
        result &= !store.get("sfre").equals(sfre);  // EJB spec 3.4.7.2, 16.2.1
        result &= !store.get("sfae").equals(sfae);  // EJB spec 3.4.7.2, 16.2.1
        //
        result &= !(store.get("sle") == sle);       // EJB spec 16.2.1
        result &= !(store.get("sfde") == sfde);     // EJB spec 16.2.1
        result &= !(store.get("sfre") == sfre);     // EJB spec 16.2.1
        result &= !(store.get("sfae") == sfae);     // EJB spec 16.2.1

        // Unlike the EJB spec, the CDI spec does not explicitly specify the semantics of the equality or inequality
        // of injected objects.  In fact, it explicitly forbids calling the java.lang.Object.equals() function on injected
        // objects [CDI spec, section 5.4.2].  It does specify that the first reference to an injectible object in a given
        // context should result in a new contextual reference or a new contextual instance [CDI spec, section 6.5.3],
        // but it doesn't seem to specify the precise semantics of "new".  In the weld reference implementation,
        // there seem to be three variations, at least with respect to injected EJBs:
        //
        // 1. It could be a new proxy for an existing object, or
        // 2. a new proxy for a new object, or
        // 3. a reused proxy for a new object.
        //
        // In this test, we find that
        //
        // 1. An SLSB (which necessarily has dependent pseudo-scope) is treated according to case 1.
        // 2. An SFSB with dependent scope is treated according to case 2.
        // 3. An SFSB with request scope is treated according to case 3.
        //
        // This behavior seems to be consistent with the semantics of EJBs:
        //
        // 1. All instances of a given SLSB class are considered equal, and SLSB target objects are reused (case 1).
        // 2. All instances of a given SFSB class are considered unequal, and SFSBs are always recreated (cases 2 and 3).
        //
        // For this test, we consider inequality, in cases where we expect a new object in a new scope, to mean
        //
        // 1. a new proxy for SLSBs, and
        // 2. a new target object for SFSBs.
        //
        // N.B.  If an SLSB is reused, and it is a contextual object (i.e., created by CDI), then, though some fields might
        //       remain the same, all fields annotated with @Inject should be processed accordingly.
        //
        log.info("sli:  == stored sli:  " + (sli == store.get("sli")));
        log.info("sfdi: == stored sfdi: " + (sfdi == store.get("sfdi")));
        log.info("sfri: == stored sfri: " + (sfri == store.get("sfri")));
        log.info("sfai: == stored sfai: " + (sfai == store.get("sfai")));

        log.info("sli.secret:  == stored sli.secret:  " + (sli.theSecret() == Integer.class.cast(store.get("sli.secret"))));
        log.info("sfdi.secret: == stored sfdi.secret: " + (sfdi.theSecret() == Integer.class.cast(store.get("sfdi.secret"))));
        log.info("sfri.secret: == stored sfri.secret: " + (sfri.theSecret() == Integer.class.cast(store.get("sfri.secret"))));
        log.info("sfai.secret: == stored sfai.secret: " + (sfai.theSecret() == Integer.class.cast(store.get("sfai.secret"))));

        result &= (sli != store.get("sli"));
        result &= (sfdi.theSecret() != Integer.class.cast(store.get("sfdi.secret")));
        result &= (sfri.theSecret() != Integer.class.cast(store.get("sfri.secret")));

        // The CDI spec requires that a single application scoped object of a given class should exist for the
        // lifetime of the application.  It seems reasonable to expect
        //
        // 1. that a proxy for that object obtained by @Inject should be reused throughout the lifetime of the application, and
        // 2. that the target of that proxy should remain the same throughout the lifetime of the application.
        result &= (sfai == store.get("sfai") && sfai.theSecret() == Integer.class.cast(store.get("sfai.secret")));

        result &= sleTest();
        result &= sfdeTest();
        result &= sfreTest();
        result &= sfaeTest();

        result &= sliTest();
        result &= sfdiTest();
        result &= sfriTest();
        result &= sfaiTest();

        return result;
    }

    @Override
    public void sleSetup() {
        sle.setUp(SLE);
    }

    @Override
    public boolean sleTest() {
        return sle.test(SLE);
    }

    @Override
    public void sfdeSetup() {
        sfde.setUp(SFDE);
    }

    @Override
    public boolean sfdeTest() {
        return sfde.test(SFDE);
    }

    @Override
    public void sfreSetup() {
        sfre.setUp(SFRE);
    }

    @Override
    public boolean sfreTest() {
        return sfre.test(SFRE);
    }

    @Override
    public void sfaeSetup() {
        sfae.setUp(SFAE);
    }

    @Override
    public boolean sfaeTest() {
        return sfae.test(SFAE);
    }

    @Override
    public void sliSetup() {
        sli.setUp(SLI);
    }

    @Override
    public boolean sliTest() {
        return sli.test(SLI);
    }

    @Override
    public void sfdiSetup() {
        sfdi.setUp(SFDI);
    }

    @Override
    public boolean sfdiTest() {
        return sfdi.test(SFDI);
    }

    @Override
    public void sfriSetup() {
        sfri.setUp(SFRI);
    }

    @Override
    public boolean sfriTest() {
        return sfri.test(SFRI);
    }

    @Override
    public void sfaiSetup() {
        sfai.setUp(SFAI);
    }

    @Override
    public boolean sfaiTest() {
        return sfai.test(SFAI);
    }

    @Override
    public boolean theSame(ReverseInjectionEJBHolderLocal that) {
        log.info("this secret: " + secret);
        log.info("that secret: " + that.theSecret());
        return this.secret == that.theSecret();
    }

    @Override
    public int theSecret() {
        return secret;
    }
}
