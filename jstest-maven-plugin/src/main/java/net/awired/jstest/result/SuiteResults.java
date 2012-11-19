package net.awired.jstest.result;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "testsuites")
@XmlAccessorType(XmlAccessType.NONE)
public class SuiteResults {

	 @XmlElement(name = "testsuite")
	 private List<SuiteResult> testsuite = new ArrayList<SuiteResult>();
	 
	 @Override
	public String toString() {
		// TODO Auto-generated method stub
		return testsuite.toString();
	}
}
