package org.jboss.resteasy.jsapi.testing;
/**
 * 11 01 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class MinusImpl implements Minus {

    @Override
    public Integer operate(Integer operand1, Integer operand2) {
        return operand1 - operand2;
    }
}
