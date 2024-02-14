package org.jboss.resteasy.test.resource.param;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterConstructorClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterCookieResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterCookieResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterFromStringClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderDelegate;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderDelegateClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMatrixResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMatrixResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMiscResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMiscResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverterClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterPathResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterPathResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterQueryResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterQueryResourceIntf;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterValueOfClass;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy extended support for multivalue @*Param (RESTEASY-1966)
 *                    org.jboss.resteasy.plugins.providers.MultiValuedArrayParamConverter and
 *                    org.jboss.resteasy.plugins.providers.MultiValuedCollectionParamConverter are used
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MultiValuedParamDefaultParamConverterProxyTest {

    private static ResteasyClient client;
    private static MultiValuedParamDefaultParamConverterCookieResourceIntf cookieProxy;
    private static MultiValuedParamDefaultParamConverterHeaderResourceIntf headerProxy;
    private static MultiValuedParamDefaultParamConverterMatrixResourceIntf matrixProxy;
    private static MultiValuedParamDefaultParamConverterMiscResourceIntf miscProxy;
    private static MultiValuedParamDefaultParamConverterPathResourceIntf pathProxy;
    private static MultiValuedParamDefaultParamConverterQueryResourceIntf queryProxy;

    private static List<MultiValuedParamDefaultParamConverterConstructorClass> list_constructor = new ArrayList<MultiValuedParamDefaultParamConverterConstructorClass>();
    private static Set<MultiValuedParamDefaultParamConverterConstructorClass> set_constructor = new HashSet<MultiValuedParamDefaultParamConverterConstructorClass>();
    private static SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> sortedSet_constructor = new TreeSet<MultiValuedParamDefaultParamConverterConstructorClass>();
    private static MultiValuedParamDefaultParamConverterConstructorClass[] array_constructor = new MultiValuedParamDefaultParamConverterConstructorClass[2];
    private static List<MultiValuedParamDefaultParamConverterValueOfClass> list_valueOf = new ArrayList<MultiValuedParamDefaultParamConverterValueOfClass>();
    private static Set<MultiValuedParamDefaultParamConverterValueOfClass> set_valueOf = new HashSet<MultiValuedParamDefaultParamConverterValueOfClass>();
    private static SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> sortedSet_valueOf = new TreeSet<MultiValuedParamDefaultParamConverterValueOfClass>();
    private static MultiValuedParamDefaultParamConverterValueOfClass[] array_valueOf = new MultiValuedParamDefaultParamConverterValueOfClass[2];
    private static List<MultiValuedParamDefaultParamConverterFromStringClass> list_fromString = new ArrayList<MultiValuedParamDefaultParamConverterFromStringClass>();
    private static Set<MultiValuedParamDefaultParamConverterFromStringClass> set_fromString = new HashSet<MultiValuedParamDefaultParamConverterFromStringClass>();
    private static SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> sortedSet_fromString = new TreeSet<MultiValuedParamDefaultParamConverterFromStringClass>();
    private static MultiValuedParamDefaultParamConverterFromStringClass[] array_fromString = new MultiValuedParamDefaultParamConverterFromStringClass[2];

    private static List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list_headerDelegate = new ArrayList<MultiValuedParamDefaultParamConverterHeaderDelegateClass>();
    private static Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set_headerDelegate = new HashSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass>();
    private static SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> sortedSet_headerDelegate = new TreeSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass>();
    private static MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array_headerDelegate = new MultiValuedParamDefaultParamConverterHeaderDelegateClass[2];

    private static List<MultiValuedParamDefaultParamConverterParamConverterClass> list_paramConverter = new ArrayList<MultiValuedParamDefaultParamConverterParamConverterClass>();
    private static Set<MultiValuedParamDefaultParamConverterParamConverterClass> set_paramConverter = new HashSet<MultiValuedParamDefaultParamConverterParamConverterClass>();
    private static SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> sortedSet_paramConverter = new TreeSet<MultiValuedParamDefaultParamConverterParamConverterClass>();
    private static MultiValuedParamDefaultParamConverterParamConverterClass[] array_paramConverter = new MultiValuedParamDefaultParamConverterParamConverterClass[2];

    private static boolean[] booleanArray = new boolean[2];
    private static byte[] byteArray = new byte[2];
    private static char[] charArray = new char[2];
    private static short[] shortArray = new short[2];
    private static int[] intArray = new int[2];
    private static long[] longArray = new long[2];
    private static float[] floatArray = new float[2];
    private static double[] doubleArray = new double[2];

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiValuedParamDefaultParamConverterProxyTest.class.getSimpleName());
        war.addClass(MultiValuedParamDefaultParamConverterConstructorClass.class);
        war.addClass(MultiValuedParamDefaultParamConverterFromStringClass.class);
        war.addClass(MultiValuedParamDefaultParamConverterParamConverterClass.class);
        war.addClass(MultiValuedParamDefaultParamConverterValueOfClass.class);
        war.addClass(MultiValuedParamDefaultParamConverterParamConverter.class);
        war.addClass(MultiValuedParamDefaultParamConverterCookieResourceIntf.class);
        war.addClass(MultiValuedParamDefaultParamConverterHeaderResourceIntf.class);
        war.addClass(MultiValuedParamDefaultParamConverterMatrixResourceIntf.class);
        war.addClass(MultiValuedParamDefaultParamConverterMiscResourceIntf.class);
        war.addClass(MultiValuedParamDefaultParamConverterPathResourceIntf.class);
        war.addClass(MultiValuedParamDefaultParamConverterQueryResourceIntf.class);
        war.addClass(MultiValuedParamDefaultParamConverterHeaderDelegateClass.class);
        war.addClass(MultiValuedParamDefaultParamConverterHeaderDelegate.class);
        return TestUtil.finishContainerPrepare(war, null, MultiValuedParamDefaultParamConverterParamConverterProvider.class,
                MultiValuedParamDefaultParamConverterCookieResource.class,
                MultiValuedParamDefaultParamConverterHeaderResource.class,
                MultiValuedParamDefaultParamConverterMatrixResource.class,
                MultiValuedParamDefaultParamConverterMiscResource.class,
                MultiValuedParamDefaultParamConverterPathResource.class,
                MultiValuedParamDefaultParamConverterQueryResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MultiValuedParamDefaultParamConverterProxyTest.class.getSimpleName());
    }

    @BeforeAll
    public static void beforeClass() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
        client.register(MultiValuedParamDefaultParamConverterParamConverterProvider.class);
        ClientConfiguration config = ((ClientConfiguration) client.getConfiguration());
        config.addHeaderDelegate(MultiValuedParamDefaultParamConverterHeaderDelegateClass.class,
                new MultiValuedParamDefaultParamConverterHeaderDelegate());
        ResteasyWebTarget target = client.target(generateURL(""));
        cookieProxy = target.proxy(MultiValuedParamDefaultParamConverterCookieResourceIntf.class);
        headerProxy = target.proxy(MultiValuedParamDefaultParamConverterHeaderResourceIntf.class);
        matrixProxy = target.proxy(MultiValuedParamDefaultParamConverterMatrixResourceIntf.class);
        miscProxy = target.proxy(MultiValuedParamDefaultParamConverterMiscResourceIntf.class);
        pathProxy = target.proxy(MultiValuedParamDefaultParamConverterPathResourceIntf.class);
        queryProxy = target.proxy(MultiValuedParamDefaultParamConverterQueryResourceIntf.class);

        MultiValuedParamDefaultParamConverterConstructorClass c1_constructor = new MultiValuedParamDefaultParamConverterConstructorClass(
                "c1");
        MultiValuedParamDefaultParamConverterConstructorClass c2_constructor = new MultiValuedParamDefaultParamConverterConstructorClass(
                "c2");
        list_constructor.add(c1_constructor);
        list_constructor.add(c2_constructor);
        set_constructor.add(c1_constructor);
        set_constructor.add(c2_constructor);
        sortedSet_constructor.add(c1_constructor);
        sortedSet_constructor.add(c2_constructor);
        array_constructor[0] = c1_constructor;
        array_constructor[1] = c2_constructor;

        MultiValuedParamDefaultParamConverterValueOfClass c1_valueOf = MultiValuedParamDefaultParamConverterValueOfClass
                .valueOf("c1");
        MultiValuedParamDefaultParamConverterValueOfClass c2_valueOf = MultiValuedParamDefaultParamConverterValueOfClass
                .valueOf("c2");
        list_valueOf.add(c1_valueOf);
        list_valueOf.add(c2_valueOf);
        set_valueOf.add(c1_valueOf);
        set_valueOf.add(c2_valueOf);
        sortedSet_valueOf.add(c1_valueOf);
        sortedSet_valueOf.add(c2_valueOf);
        array_valueOf[0] = c1_valueOf;
        array_valueOf[1] = c2_valueOf;

        MultiValuedParamDefaultParamConverterFromStringClass c1_fromString = MultiValuedParamDefaultParamConverterFromStringClass
                .fromString("c1");
        MultiValuedParamDefaultParamConverterFromStringClass c2_fromString = MultiValuedParamDefaultParamConverterFromStringClass
                .fromString("c2");
        list_fromString.add(c1_fromString);
        list_fromString.add(c2_fromString);
        set_fromString.add(c1_fromString);
        set_fromString.add(c2_fromString);
        sortedSet_fromString.add(c1_fromString);
        sortedSet_fromString.add(c2_fromString);
        array_fromString[0] = c1_fromString;
        array_fromString[1] = c2_fromString;

        MultiValuedParamDefaultParamConverterHeaderDelegateClass c1_headerDelegate = new MultiValuedParamDefaultParamConverterHeaderDelegateClass();
        c1_headerDelegate.setS("c1");
        MultiValuedParamDefaultParamConverterHeaderDelegateClass c2_headerDelegate = new MultiValuedParamDefaultParamConverterHeaderDelegateClass();
        c2_headerDelegate.setS("c2");
        list_headerDelegate.add(c1_headerDelegate);
        list_headerDelegate.add(c2_headerDelegate);
        set_headerDelegate.add(c1_headerDelegate);
        set_headerDelegate.add(c2_headerDelegate);
        sortedSet_headerDelegate.add(c1_headerDelegate);
        sortedSet_headerDelegate.add(c2_headerDelegate);
        array_headerDelegate[0] = c1_headerDelegate;
        array_headerDelegate[1] = c2_headerDelegate;

        MultiValuedParamDefaultParamConverterParamConverterClass c1_paramConverter = new MultiValuedParamDefaultParamConverterParamConverterClass();
        c1_paramConverter.setS("c1");
        MultiValuedParamDefaultParamConverterParamConverterClass c2_paramConverter = new MultiValuedParamDefaultParamConverterParamConverterClass();
        c2_paramConverter.setS("c2");
        list_paramConverter.add(c1_paramConverter);
        list_paramConverter.add(c2_paramConverter);
        set_paramConverter.add(c1_paramConverter);
        set_paramConverter.add(c2_paramConverter);
        sortedSet_paramConverter.add(c1_paramConverter);
        sortedSet_paramConverter.add(c2_paramConverter);
        array_paramConverter[0] = c1_paramConverter;
        array_paramConverter[1] = c2_paramConverter;

        booleanArray[0] = false;
        booleanArray[1] = true;
        byteArray[0] = 0;
        byteArray[1] = 1;
        charArray[0] = 'a';
        charArray[1] = 'z';
        shortArray[0] = 3;
        shortArray[1] = 7;
        intArray[0] = 11;
        intArray[1] = 13;
        longArray[0] = 17;
        longArray[1] = 19;
        floatArray[0] = 23.0f;
        floatArray[1] = 29.0f;
        doubleArray[0] = 31.0d;
        doubleArray[1] = 37.0d;
    }

    @AfterAll
    public static void afterClass() throws Exception {
        client.close();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @tpTestDetails CookieParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testCookie() {

        Assertions.assertEquals("cscc1|cscc2|", cookieProxy.cookieConstructorSeparatorList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", cookieProxy.cookieConstructorSeparatorSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", cookieProxy.cookieConstructorSeparatorSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", cookieProxy.cookieConstructorSeparatorArray(array_constructor));

        Assertions.assertEquals("cscc1|cscc2|", cookieProxy.cookieConstructorDefaultList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", cookieProxy.cookieConstructorDefaultSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", cookieProxy.cookieConstructorDefaultSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", cookieProxy.cookieConstructorDefaultArray(array_constructor));

        Assertions.assertEquals("vsvc1|vsvc2|", cookieProxy.cookieValueOfSeparatorList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", cookieProxy.cookieValueOfSeparatorSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", cookieProxy.cookieValueOfSeparatorSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", cookieProxy.cookieValueOfSeparatorArray(array_valueOf));

        Assertions.assertEquals("vsvc1|vsvc2|", cookieProxy.cookieValueOfDefaultList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", cookieProxy.cookieValueOfDefaultSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", cookieProxy.cookieValueOfDefaultSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", cookieProxy.cookieValueOfDefaultArray(array_valueOf));

        Assertions.assertEquals("fsfc1|fsfc2|", cookieProxy.cookieFromStringSeparatorList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", cookieProxy.cookieFromStringSeparatorSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", cookieProxy.cookieFromStringSeparatorSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", cookieProxy.cookieFromStringSeparatorArray(array_fromString));

        Assertions.assertEquals("fsfc1|fsfc2|", cookieProxy.cookieFromStringDefaultList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", cookieProxy.cookieFromStringDefaultSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", cookieProxy.cookieFromStringDefaultSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", cookieProxy.cookieFromStringDefaultArray(array_fromString));

        Assertions.assertEquals("ppc1|ppc2|", cookieProxy.cookieParamConverterSeparatorList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", cookieProxy.cookieParamConverterSeparatorSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", cookieProxy.cookieParamConverterSeparatorSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", cookieProxy.cookieParamConverterSeparatorArray(array_paramConverter));

        Assertions.assertEquals("ppc1|ppc2|", cookieProxy.cookieParamConverterDefaultList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", cookieProxy.cookieParamConverterDefaultSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", cookieProxy.cookieParamConverterDefaultSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", cookieProxy.cookieParamConverterDefaultArray(array_paramConverter));

        Assertions.assertEquals("false|true|", cookieProxy.cookieBoolean(booleanArray));
        Assertions.assertEquals("0|1|", cookieProxy.cookieByte(byteArray));
        Assertions.assertEquals("a|z|", cookieProxy.cookieChar(charArray));
        Assertions.assertEquals("3|7|", cookieProxy.cookieShort(shortArray));
        Assertions.assertEquals("11|13|", cookieProxy.cookieInt(intArray));
        Assertions.assertEquals("17|19|", cookieProxy.cookieLong(longArray));
        Assertions.assertEquals("23.0|29.0|", cookieProxy.cookieFloat(floatArray));
        Assertions.assertEquals("31.0|37.0|", cookieProxy.cookieDouble(doubleArray));
    }

    /**
     * @tpTestDetails HeaderParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testHeader() {

        Assertions.assertEquals("cscc1|cscc2|", headerProxy.headerConstructorSeparatorList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", headerProxy.headerConstructorSeparatorSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", headerProxy.headerConstructorSeparatorSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", headerProxy.headerConstructorSeparatorArray(array_constructor));

        Assertions.assertEquals("cscc1|cscc2|", headerProxy.headerConstructorDefaultList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", headerProxy.headerConstructorDefaultSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", headerProxy.headerConstructorDefaultSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", headerProxy.headerConstructorDefaultArray(array_constructor));

        Assertions.assertEquals("vsvc1|vsvc2|", headerProxy.headerValueOfSeparatorList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", headerProxy.headerValueOfSeparatorSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", headerProxy.headerValueOfSeparatorSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", headerProxy.headerValueOfSeparatorArray(array_valueOf));

        Assertions.assertEquals("vsvc1|vsvc2|", headerProxy.headerValueOfDefaultList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", headerProxy.headerValueOfDefaultSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", headerProxy.headerValueOfDefaultSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", headerProxy.headerValueOfDefaultArray(array_valueOf));

        Assertions.assertEquals("fsfc1|fsfc2|", headerProxy.headerFromStringSeparatorList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", headerProxy.headerFromStringSeparatorSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", headerProxy.headerFromStringSeparatorSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", headerProxy.headerFromStringSeparatorArray(array_fromString));

        Assertions.assertEquals("fsfc1|fsfc2|", headerProxy.headerFromStringDefaultList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", headerProxy.headerFromStringDefaultSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", headerProxy.headerFromStringDefaultSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", headerProxy.headerFromStringDefaultArray(array_fromString));

        Assertions.assertEquals("hhc1|hhc2|", headerProxy.headerHeaderDelegateSeparatorList(list_headerDelegate));
        Assertions.assertEquals("hhc1|hhc2|", headerProxy.headerHeaderDelegateSeparatorSet(set_headerDelegate));
        Assertions.assertEquals("hhc1|hhc2|", headerProxy.headerHeaderDelegateSeparatorSortedSet(sortedSet_headerDelegate));
        Assertions.assertEquals("hhc1|hhc2|", headerProxy.headerHeaderDelegateSeparatorArray(array_headerDelegate));

        Assertions.assertEquals("hhc1|hhc2|", headerProxy.headerHeaderDelegateDefaultList(list_headerDelegate));
        Assertions.assertEquals("hhc1|hhc2|", headerProxy.headerHeaderDelegateDefaultSet(set_headerDelegate));
        Assertions.assertEquals("hhc1|hhc2|", headerProxy.headerHeaderDelegateDefaultSortedSet(sortedSet_headerDelegate));
        Assertions.assertEquals("hhc1|hhc2|", headerProxy.headerHeaderDelegateDefaultArray(array_headerDelegate));

        Assertions.assertEquals("ppc1|ppc2|", headerProxy.headerParamConverterSeparatorList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", headerProxy.headerParamConverterSeparatorSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", headerProxy.headerParamConverterSeparatorSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", headerProxy.headerParamConverterSeparatorArray(array_paramConverter));

        Assertions.assertEquals("ppc1|ppc2|", headerProxy.headerParamConverterDefaultList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", headerProxy.headerParamConverterDefaultSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", headerProxy.headerParamConverterDefaultSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", headerProxy.headerParamConverterDefaultArray(array_paramConverter));

        Assertions.assertEquals("false|true|", headerProxy.headerBoolean(booleanArray));
        Assertions.assertEquals("0|1|", headerProxy.headerByte(byteArray));
        Assertions.assertEquals("a|z|", headerProxy.headerChar(charArray));
        Assertions.assertEquals("3|7|", headerProxy.headerShort(shortArray));
        Assertions.assertEquals("11|13|", headerProxy.headerInt(intArray));
        Assertions.assertEquals("17|19|", headerProxy.headerLong(longArray));
        Assertions.assertEquals("23.0|29.0|", headerProxy.headerFloat(floatArray));
        Assertions.assertEquals("31.0|37.0|", headerProxy.headerDouble(doubleArray));
    }

    /**
     * @tpTestDetails MatrixParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testMatrix() {

        Assertions.assertEquals("cscc1|cscc2|", matrixProxy.matrixConstructorSeparatorList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", matrixProxy.matrixConstructorSeparatorSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", matrixProxy.matrixConstructorSeparatorSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", matrixProxy.matrixConstructorSeparatorArray(array_constructor));

        Assertions.assertEquals("cscc1|cscc2|", matrixProxy.matrixConstructorDefaultList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", matrixProxy.matrixConstructorDefaultSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", matrixProxy.matrixConstructorDefaultSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", matrixProxy.matrixConstructorDefaultArray(array_constructor));

        Assertions.assertEquals("vsvc1|vsvc2|", matrixProxy.matrixValueOfSeparatorList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", matrixProxy.matrixValueOfSeparatorSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", matrixProxy.matrixValueOfSeparatorSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", matrixProxy.matrixValueOfSeparatorArray(array_valueOf));

        Assertions.assertEquals("vsvc1|vsvc2|", matrixProxy.matrixValueOfDefaultList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", matrixProxy.matrixValueOfDefaultSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", matrixProxy.matrixValueOfDefaultSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", matrixProxy.matrixValueOfDefaultArray(array_valueOf));

        Assertions.assertEquals("fsfc1|fsfc2|", matrixProxy.matrixFromStringSeparatorList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", matrixProxy.matrixFromStringSeparatorSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", matrixProxy.matrixFromStringSeparatorSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", matrixProxy.matrixFromStringSeparatorArray(array_fromString));

        Assertions.assertEquals("fsfc1|fsfc2|", matrixProxy.matrixFromStringDefaultList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", matrixProxy.matrixFromStringDefaultSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", matrixProxy.matrixFromStringDefaultSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", matrixProxy.matrixFromStringDefaultArray(array_fromString));

        Assertions.assertEquals("ppc1|ppc2|", matrixProxy.matrixParamConverterSeparatorList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", matrixProxy.matrixParamConverterSeparatorSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", matrixProxy.matrixParamConverterSeparatorSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", matrixProxy.matrixParamConverterSeparatorArray(array_paramConverter));

        Assertions.assertEquals("ppc1|ppc2|", matrixProxy.matrixParamConverterDefaultList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", matrixProxy.matrixParamConverterDefaultSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", matrixProxy.matrixParamConverterDefaultSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", matrixProxy.matrixParamConverterDefaultArray(array_paramConverter));

        Assertions.assertEquals("false|true|", matrixProxy.matrixBoolean(booleanArray));
        Assertions.assertEquals("0|1|", matrixProxy.matrixByte(byteArray));
        Assertions.assertEquals("a|z|", matrixProxy.matrixChar(charArray));
        Assertions.assertEquals("3|7|", matrixProxy.matrixShort(shortArray));
        Assertions.assertEquals("11|13|", matrixProxy.matrixInt(intArray));
        Assertions.assertEquals("17|19|", matrixProxy.matrixLong(longArray));
        Assertions.assertEquals("23.0|29.0|", matrixProxy.matrixFloat(floatArray));
        Assertions.assertEquals("31.0|37.0|", matrixProxy.matrixDouble(doubleArray));
    }

    /**
     * @tpTestDetails PathParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testPath() {

        Assertions.assertEquals("cscc1|cscc2|", pathProxy.pathConstructorSeparatorList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", pathProxy.pathConstructorSeparatorSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", pathProxy.pathConstructorSeparatorSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", pathProxy.pathConstructorSeparatorArray(array_constructor));

        Assertions.assertEquals("cscc1|cscc2|", pathProxy.pathConstructorDefaultList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", pathProxy.pathConstructorDefaultSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", pathProxy.pathConstructorDefaultSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", pathProxy.pathConstructorDefaultArray(array_constructor));

        Assertions.assertEquals("vsvc1|vsvc2|", pathProxy.pathValueOfSeparatorList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", pathProxy.pathValueOfSeparatorSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", pathProxy.pathValueOfSeparatorSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", pathProxy.pathValueOfSeparatorArray(array_valueOf));

        Assertions.assertEquals("vsvc1|vsvc2|", pathProxy.pathValueOfDefaultList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", pathProxy.pathValueOfDefaultSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", pathProxy.pathValueOfDefaultSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", pathProxy.pathValueOfDefaultArray(array_valueOf));

        Assertions.assertEquals("fsfc1|fsfc2|", pathProxy.pathFromStringSeparatorList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", pathProxy.pathFromStringSeparatorSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", pathProxy.pathFromStringSeparatorSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", pathProxy.pathFromStringSeparatorArray(array_fromString));

        Assertions.assertEquals("fsfc1|fsfc2|", pathProxy.pathFromStringDefaultList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", pathProxy.pathFromStringDefaultSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", pathProxy.pathFromStringDefaultSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", pathProxy.pathFromStringDefaultArray(array_fromString));

        Assertions.assertEquals("ppc1|ppc2|", pathProxy.pathParamConverterSeparatorList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", pathProxy.pathParamConverterSeparatorSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", pathProxy.pathParamConverterSeparatorSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", pathProxy.pathParamConverterSeparatorArray(array_paramConverter));

        Assertions.assertEquals("ppc1|ppc2|", pathProxy.pathParamConverterDefaultList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", pathProxy.pathParamConverterDefaultSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", pathProxy.pathParamConverterDefaultSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", pathProxy.pathParamConverterDefaultArray(array_paramConverter));

        Assertions.assertEquals("false|true|", pathProxy.pathBoolean(booleanArray));
        Assertions.assertEquals("0|1|", pathProxy.pathByte(byteArray));
        Assertions.assertEquals("a|z|", pathProxy.pathChar(charArray));
        Assertions.assertEquals("3|7|", pathProxy.pathShort(shortArray));
        Assertions.assertEquals("11|13|", pathProxy.pathInt(intArray));
        Assertions.assertEquals("17|19|", pathProxy.pathLong(longArray));
        Assertions.assertEquals("23.0|29.0|", pathProxy.pathFloat(floatArray));
        Assertions.assertEquals("31.0|37.0|", pathProxy.pathDouble(doubleArray));
    }

    /**
     * @tpTestDetails QueryParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testQuery() {

        Assertions.assertEquals("cscc1|cscc2|", queryProxy.queryConstructorSeparatorList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", queryProxy.queryConstructorSeparatorSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", queryProxy.queryConstructorSeparatorSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", queryProxy.queryConstructorSeparatorArray(array_constructor));

        Assertions.assertEquals("cscc1|cscc2|", queryProxy.queryConstructorDefaultList(list_constructor));
        Assertions.assertEquals("cscc1|cscc2|", queryProxy.queryConstructorDefaultSet(set_constructor));
        Assertions.assertEquals("cscc1|cscc2|", queryProxy.queryConstructorDefaultSortedSet(sortedSet_constructor));
        Assertions.assertEquals("cscc1|cscc2|", queryProxy.queryConstructorDefaultArray(array_constructor));

        Assertions.assertEquals("vsvc1|vsvc2|", queryProxy.queryValueOfSeparatorList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", queryProxy.queryValueOfSeparatorSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", queryProxy.queryValueOfSeparatorSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", queryProxy.queryValueOfSeparatorArray(array_valueOf));

        Assertions.assertEquals("vsvc1|vsvc2|", queryProxy.queryValueOfDefaultList(list_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", queryProxy.queryValueOfDefaultSet(set_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", queryProxy.queryValueOfDefaultSortedSet(sortedSet_valueOf));
        Assertions.assertEquals("vsvc1|vsvc2|", queryProxy.queryValueOfDefaultArray(array_valueOf));

        Assertions.assertEquals("fsfc1|fsfc2|", queryProxy.queryFromStringSeparatorList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", queryProxy.queryFromStringSeparatorSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", queryProxy.queryFromStringSeparatorSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", queryProxy.queryFromStringSeparatorArray(array_fromString));

        Assertions.assertEquals("fsfc1|fsfc2|", queryProxy.queryFromStringDefaultList(list_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", queryProxy.queryFromStringDefaultSet(set_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", queryProxy.queryFromStringDefaultSortedSet(sortedSet_fromString));
        Assertions.assertEquals("fsfc1|fsfc2|", queryProxy.queryFromStringDefaultArray(array_fromString));

        Assertions.assertEquals("ppc1|ppc2|", queryProxy.queryParamConverterSeparatorList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", queryProxy.queryParamConverterSeparatorSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", queryProxy.queryParamConverterSeparatorSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", queryProxy.queryParamConverterSeparatorArray(array_paramConverter));

        Assertions.assertEquals("ppc1|ppc2|", queryProxy.queryParamConverterDefaultList(list_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", queryProxy.queryParamConverterDefaultSet(set_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", queryProxy.queryParamConverterDefaultSortedSet(sortedSet_paramConverter));
        Assertions.assertEquals("ppc1|ppc2|", queryProxy.queryParamConverterDefaultArray(array_paramConverter));

        Assertions.assertEquals("false|true|", queryProxy.queryBoolean(booleanArray));
        Assertions.assertEquals("0|1|", queryProxy.queryByte(byteArray));
        Assertions.assertEquals("a|z|", queryProxy.queryChar(charArray));
        Assertions.assertEquals("3|7|", queryProxy.queryShort(shortArray));
        Assertions.assertEquals("11|13|", queryProxy.queryInt(intArray));
        Assertions.assertEquals("17|19|", queryProxy.queryLong(longArray));
        Assertions.assertEquals("23.0|29.0|", queryProxy.queryFloat(floatArray));
        Assertions.assertEquals("31.0|37.0|", queryProxy.queryDouble(doubleArray));
    }

    /**
     * @tpTestDetails
     * @tpSince RESTEasy 4.0.0
     * @tpTestCaseDetails This test verifies that MultiValuedParamConverterProvider does not engage on the
     *                    client side if @Separator has an inappropriate value.
     */
    @Test
    public void testMiscellaneous() {
        Set<String> set = new HashSet<String>();
        set.add("p1");
        set.add("p2");
        Assertions.assertTrue(miscProxy.regexClientCookie(set).contains("[p1, p2]"));
        Assertions.assertTrue(miscProxy.regexClientHeader(set).contains("p1,p2"));
        Assertions.assertTrue(miscProxy.regexClientMatrix(set).contains("p=p1;p=p2"));
        Assertions.assertTrue(miscProxy.regexClientPath(set).contains("%5Bp1,%20p2%5D"));
        Assertions.assertTrue(miscProxy.regexClientQuery(set).contains("p=p1&p=p2"));
    }
}
