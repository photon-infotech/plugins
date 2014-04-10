/**
 * Phresco Plugin Commons
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.photon.phresco.plugins.params.model;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class PerformanceDetails {

    private List<String> name;
    private List<String> context;
    private List<String> contextType;
    private List<String> contextPostData;
    private List<String> encodingType;
    private List<String> dbPerName;
    private List<String> queryType;
    private List<String> query;
    private int noOfUsers;
    private int rampUpPeriod;
    private int loopCount;

    public PerformanceDetails() {

    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getContext() {
        return context;
    }

    public void setContext(List<String> context) {
        this.context = context;
    }

    public List<String> getContextType() {
        return contextType;
    }

    public void setContextType(List<String> contextType) {
        this.contextType = contextType;
    }

    public List<String> getContextPostData() {
        return contextPostData;
    }

    public void setContextPostData(List<String> contextPostData) {
        this.contextPostData = contextPostData;
    }

    public List<String> getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(List<String> encodingType) {
        this.encodingType = encodingType;
    }

    public List<String> getDbPerName() {
        return dbPerName;
    }

    public void setDbPerName(List<String> dbPerName) {
        this.dbPerName = dbPerName;
    }

    public List<String> getQueryType() {
        return queryType;
    }

    public void setQueryType(List<String> queryType) {
        this.queryType = queryType;
    }

    public List<String> getQuery() {
        return query;
    }

    public void setQuery(List<String> query) {
        this.query = query;
    }

    public int getNoOfUsers() {
        return noOfUsers;
    }

    public void setNoOfUsers(int noOfUsers) {
        this.noOfUsers = noOfUsers;
    }

    public int getRampUpPeriod() {
        return rampUpPeriod;
    }

    public void setRampUpPeriod(int rampUpPeriod) {
        this.rampUpPeriod = rampUpPeriod;
    }

    public int getLoopCount() {
        return loopCount;

    }
    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public String toString() {
        return new ToStringBuilder(this,
                ToStringStyle.DEFAULT_STYLE)
                .append("name", getName())
                .append("context", getContext())
                .append("contextType", getContextType())
                .append("contextPostData", getContextPostData())
                .append("encodingType", getEncodingType())
                .append("dbPerName", getDbPerName())
                .append("queryType", getQueryType())
                .append("query", getQuery())
                .append("noOfUsers", getNoOfUsers())
                .append("rampUpPeriod", getRampUpPeriod())
                .append("loopCount", getLoopCount())
                .toString();
    }
}