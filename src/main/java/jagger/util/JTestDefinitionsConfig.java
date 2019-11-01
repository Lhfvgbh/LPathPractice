package jagger.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:test.properties")
public class JTestDefinitionsConfig {

    @Autowired
    Environment environment;

    @Bean
    public TestProperties testProperties(){
        TestProperties testProperties = new TestProperties();
        testProperties.setCommonTestUrl(environment.getProperty("common.test.url"));
        testProperties.setCommonTestMaxDuration(environment.getProperty("common.test.max.duration.in.seconds"));

        testProperties.setGetTestPath(environment.getProperty("get.test.url.path"));
        testProperties.setGetTestInvocations(environment.getProperty("get.test.invocations"));

        testProperties.setXmlTestPath(environment.getProperty("xml.test.url.path"));
        testProperties.setXmlTestInvocationDuration(environment.getProperty("xml.test.duration.in.seconds"));
        testProperties.setXmlTestUserDelay(environment.getProperty("xml.test.user.delay.in.seconds"));
        testProperties.setXmlTestInvocationDelay(environment.getProperty("xml.test.invocation.delay.in.milliseconds"));

        testProperties.setResponseTestPath(environment.getProperty("response.test.url.path"));
        testProperties.setResponseTestDuration(environment.getProperty("response.test.duration.in.seconds"));
        testProperties.setResponseTestInvocationDelayU1(environment.getProperty("response.test.invocation.delay.in.milliseconds.u1"));
        testProperties.setResponseTestInvocationDelayU2(environment.getProperty("response.test.invocation.delay.in.milliseconds.u2"));

        return testProperties;
    }

}
