package jagger.util;

public class TestProperties {

    private String commonTestUrl;
    private String commonTestMaxDuration;
    private String getTestPath;
    private String getTestInvocations;
    private String xmlTestPath;
    private String xmlTestInvocationDuration;
    private String xmlTestUserDelay;
    private String xmlTestInvocationDelay;
    private String responseTestPath;
    private String responseTestDuration;
    private String responseTestInvocationDelayU1;
    private String responseTestInvocationDelayU2;

    public void setCommonTestUrl(String commonTestUrl) {
        this.commonTestUrl = commonTestUrl;
    }

    public void setCommonTestMaxDuration(String commonTestMaxDuration) {
        this.commonTestMaxDuration = commonTestMaxDuration;
    }

    public void setGetTestPath(String getTestPath) {
        this.getTestPath = getTestPath;
    }

    public void setGetTestInvocations(String getTestInvocations) {
        this.getTestInvocations = getTestInvocations;
    }

    public void setXmlTestPath(String xmlTestPath) {
        this.xmlTestPath = xmlTestPath;
    }

    public void setXmlTestInvocationDuration(String xmlTestInvocationDuration) {
        this.xmlTestInvocationDuration = xmlTestInvocationDuration;
    }

    public void setXmlTestUserDelay(String xmlTestUserDelay) {
        this.xmlTestUserDelay = xmlTestUserDelay;
    }

    public void setXmlTestInvocationDelay(String xmlTestInvocationDelay) {
        this.xmlTestInvocationDelay = xmlTestInvocationDelay;
    }

    public void setResponseTestPath(String responseTestPath) {
        this.responseTestPath = responseTestPath;
    }

    public void setResponseTestDuration(String responseTestDuration) {
        this.responseTestDuration = responseTestDuration;
    }

    public void setResponseTestInvocationDelayU1(String responseTestInvocationDelayU1) {
        this.responseTestInvocationDelayU1 = responseTestInvocationDelayU1;
    }

    public void setResponseTestInvocationDelayU2(String responseTestInvocationDelayU2) {
        this.responseTestInvocationDelayU2 = responseTestInvocationDelayU2;
    }

    public String getCommonUrl() {
        return commonTestUrl;
    }

    public Long getCommonTestMaxDuration() {
        return Long.valueOf(commonTestMaxDuration);
    }

    public String getGetTestPath() {
        return getTestPath;
    }

    public int getGetTestInvocations() {
        return Integer.valueOf(getTestInvocations);
    }

    public String getXmlTestPath() {
        return xmlTestPath;
    }

    public int getXmlTestUserDelay() {
        return Integer.valueOf(xmlTestUserDelay);
    }

    public int getXmlTestInvocationDuration() {
        return Integer.valueOf(xmlTestInvocationDuration);
    }

    public int getXmlTestInvocationDelay() {
        return Integer.valueOf(xmlTestInvocationDelay);
    }

    public String getResponseTestPath() {
        return responseTestPath;
    }

    public int getResponseTestDuration() {
        return Integer.valueOf(responseTestDuration);
    }

    public int getResponseTestInvocationDelayU1() {
        return Integer.valueOf(responseTestInvocationDelayU1);
    }

    public int getResponseTestInvocationDelayU2() {
        return Integer.valueOf(responseTestInvocationDelayU2);
    }
}
