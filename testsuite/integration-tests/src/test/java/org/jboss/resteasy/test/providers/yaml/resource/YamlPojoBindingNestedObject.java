package org.jboss.resteasy.test.providers.yaml.resource;

public class YamlPojoBindingNestedObject {

    private String moreText;

    public String getMoreText() {
        return moreText;
    }

    public void setMoreText(String moreText) {
        this.moreText = moreText;
    }

    @Override
    public String toString() {
        return "YamlPojoBindingNestedObject[" + moreText + "]";
    }

}
