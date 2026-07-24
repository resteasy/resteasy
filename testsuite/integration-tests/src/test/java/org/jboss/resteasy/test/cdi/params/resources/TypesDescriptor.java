/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class TypesDescriptor {
    private List<String> listParam;
    private Set<String> setParam;
    private SortedSet<String> sortedSetParam;
    private String[] arrayParam;
    private Integer wrapperParam;
    private Boolean booleanParam;
    private ParamEnum enumParam;
    private String defaultParam;
    private int defaultIntParam;
    private List<String> listPaths;
    private String singlePath;

    public static TypesDescriptor of() {
        return new TypesDescriptor();
    }

    public List<String> getListParam() {
        return listParam;
    }

    public TypesDescriptor setListParam(final List<String> listParam) {
        this.listParam = listParam;
        return this;
    }

    public Set<String> getSetParam() {
        return setParam;
    }

    public TypesDescriptor setSetParam(final Set<String> setParam) {
        this.setParam = setParam;
        return this;
    }

    public SortedSet<String> getSortedSetParam() {
        return sortedSetParam;
    }

    public TypesDescriptor setSortedSetParam(final SortedSet<String> sortedSetParam) {
        this.sortedSetParam = sortedSetParam;
        return this;
    }

    public String[] getArrayParam() {
        return arrayParam;
    }

    public TypesDescriptor setArrayParam(final String[] arrayParam) {
        this.arrayParam = arrayParam;
        return this;
    }

    public Integer getWrapperParam() {
        return wrapperParam;
    }

    public TypesDescriptor setWrapperParam(final Integer wrapperParam) {
        this.wrapperParam = wrapperParam;
        return this;
    }

    public Boolean getBooleanParam() {
        return booleanParam;
    }

    public TypesDescriptor setBooleanParam(final Boolean booleanParam) {
        this.booleanParam = booleanParam;
        return this;
    }

    public ParamEnum getEnumParam() {
        return enumParam;
    }

    public TypesDescriptor setEnumParam(final ParamEnum enumParam) {
        this.enumParam = enumParam;
        return this;
    }

    public String getDefaultParam() {
        return defaultParam;
    }

    public TypesDescriptor setDefaultParam(final String defaultParam) {
        this.defaultParam = defaultParam;
        return this;
    }

    public int getDefaultIntParam() {
        return defaultIntParam;
    }

    public TypesDescriptor setDefaultIntParam(final int defaultIntParam) {
        this.defaultIntParam = defaultIntParam;
        return this;
    }

    public List<String> getListPaths() {
        return listPaths;
    }

    public TypesDescriptor setListPaths(final List<String> listPaths) {
        this.listPaths = listPaths;
        return this;
    }

    public String getSinglePath() {
        return singlePath;
    }

    public TypesDescriptor setSinglePath(final String singlePath) {
        this.singlePath = singlePath;
        return this;
    }

    @Override
    public String toString() {
        return "TypesDescriptor{" + "listParam=" + listParam +
                ", setParam=" + setParam +
                ", sortedSetParam=" + sortedSetParam +
                ", arrayParam=" + Arrays.toString(arrayParam) +
                ", wrapperParam=" + wrapperParam +
                ", booleanParam=" + booleanParam +
                ", enumParam=" + enumParam +
                ", defaultParam='" + defaultParam + '\'' +
                ", defaultIntParam=" + defaultIntParam +
                ", listPaths=" + listPaths +
                ", singlePath=" + singlePath +
                '}';
    }
}
