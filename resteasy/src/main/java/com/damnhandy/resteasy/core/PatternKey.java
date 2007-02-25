package com.damnhandy.resteasy.core;

import java.util.regex.Pattern;

/**
 * 
 * @author Ryan J. McDonough
 * Jan 29, 2007
 *
 */
class PatternKey  {
    private final Pattern pattern;
    
    /**
     * 
     * @param expression
     */
    public PatternKey(final String expression) {
        this.pattern = Pattern.compile(expression);
    }
    /**
     * 
     * @return
     */
    public final Pattern getPattern() {
        return pattern;
    }
    
    /**
     * 
     * @param string
     * @return
     */
    protected boolean matches(String string) {
        return pattern.matcher(string).matches();
    }
    
    /**
     * 
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return pattern.toString().hashCode();
    }
    
    /**
     * 
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(object == null) {
            return false;
        }
        
        if(object instanceof PatternKey) {
            PatternKey key = (PatternKey) object;
            return key.toString().equals(this.toString());
        }
        return false;
    }
}