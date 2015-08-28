package org.jboss.resteasy.spring.beanprocessor;

/**
 * Created with IntelliJ IDEA.
 * User: sgv
 * Date: 23/10/14
 * Time: 16:16
 */
public class MyBean {

    public MyInnerBean getMyInnerBean() {
        return new MyInnerBeanImpl();
    }
}
