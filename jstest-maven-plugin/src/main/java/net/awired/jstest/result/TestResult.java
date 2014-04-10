/**
 * JsTest Maven Plugin
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
package net.awired.jstest.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.NONE)
public class TestResult {

    @XmlAttribute
    private String classname;
    @XmlAttribute
    private String name;
    @XmlElement(name = "system-out")
    private String sysout;
    @XmlElement(name = "system-err")
    private String syserr;
    @XmlElement
    private Boolean skipped;
    @XmlElement
    private ErrorDescription error;
    @XmlElement
    private FailureDescription failure;

    private long duration;

    @XmlAttribute(name = "time")
    public double getTime() {
        return duration / 1000.0;
    }

    public boolean isSuccess() {
        return !isSkipped() && error == null && failure == null;
    }

    public boolean isFailure() {
        return error == null && failure != null;
    }

    public boolean isError() {
        return error != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestResult) {
            final TestResult other = (TestResult) obj;
            return Objects.equal(name, other.name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    /////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSysout(String sysout) {
        this.sysout = sysout;
    }

    public String getSysout() {
        return sysout;
    }

    public void setSyserr(String syserr) {
        this.syserr = syserr;
    }

    public String getSyserr() {
        return syserr;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public ErrorDescription getError() {
        return error;
    }

    public void setError(ErrorDescription error) {
        this.error = error;
    }

    public FailureDescription getFailure() {
        return failure;
    }

    public void setFailure(FailureDescription failure) {
        this.failure = failure;
    }

    public boolean isSkipped() {
        return skipped != null && skipped != false;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

}
