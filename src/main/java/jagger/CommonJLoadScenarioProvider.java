package jagger;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.DefaultResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.ExampleLoadScenarioListener;
import com.griddynamics.jagger.invoker.OneByOneLoadBalancer;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUserGroups;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaDuration;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.DurationInSeconds;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import jagger.calculators.*;
import jagger.providers.*;
import jagger.util.TestProperties;
import jagger.validators.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CommonJLoadScenarioProvider {

    private static List<Provider<InvocationListener>> commonInvocationListeners() {
        return new ArrayList<>(Arrays.asList(
                new NotNullInvocationListener(),
                new DurationInvocationListener(),
                new ResponseSizeInvocationListener()));
    }

    private static List<ResponseValidatorProvider> commonResponseValidators() {
        return new ArrayList<>(Arrays.asList(
                DefaultResponseValidatorProvider.of(NotNullResponseValidator.class),
                new StatusCodeValidatorProvider()));
    }

    @Autowired
    TestProperties properties;

    @Bean
    public JLoadScenario commonJaggerLoadScenario() {

        //2 users 5 iterations
        //path = /get
        JTestDefinition getTD = JTestDefinition
                .builder(Id.of("getTD"), new EndpointsProvider(properties.getCommonUrl()))
                .withInvoker(new DefaultInvokerProvider())
                .withQueryProvider(new QueryProvider(properties.getGetTestPath()))
                .withLoadBalancer(new OneByOneLoadBalancer())
                .addValidator(new JSONResponseTypeValidatorProvider())
                .addValidators(commonResponseValidators())
                .addListener(new ResponseHeadersInvocationListener())
                .addListener(new ResponseArgumentsInvocationListener())
                .addListeners(commonInvocationListeners())
                .build();

        JLoadProfileInvocation getLPI = JLoadProfileInvocation
                .builder(InvocationCount.of(properties.getGetTestInvocations()), ThreadCount.of(1))
                .build();

        JTerminationCriteriaIterations getTCI = JTerminationCriteriaIterations.of(
                IterationsNumber.of(properties.getGetTestInvocations()),
                MaxDurationInSeconds.of(properties.getCommonTestMaxDuration()));

        JLoadTest getLT = JLoadTest.builder(Id.of("getLT"), getTD, getLPI, getTCI).build();

        JParallelTestsGroup getU1PTG = JParallelTestsGroup.builder(Id.of("getU1PTG"), getLT).build();

        JParallelTestsGroup getU2PTG = JParallelTestsGroup.builder(Id.of("getU2PTG"), getLT).build();


        //3 user for 2 minutes with 15 seconds delay between invocations starting by 1 user each 20 seconds
        //path = /xml
        JTestDefinition xmlTD = JTestDefinition
                .builder(Id.of("xmlTD"), new EndpointsProvider(properties.getCommonUrl()))
                .withInvoker(new DefaultInvokerProvider())
                .withQueryProvider(new QueryProvider(properties.getXmlTestPath()))
                .withLoadBalancer(new OneByOneLoadBalancer())
                .addValidator(new XMLResponseTypeValidatorProvider())
                .addValidators(commonResponseValidators())
                .addListener(new XMLItemsInvocationListener())
                .addListeners(commonInvocationListeners())
                .build();

        JTerminationCriteriaDuration xmlTCD = JTerminationCriteriaDuration.of(DurationInSeconds.of(properties.getXmlTestInvocationDuration())); //120sec

        JLoadProfileUserGroups xmlUG = JLoadProfileUserGroups
                .builder(
                        JLoadProfileUsers
                                .builder(NumberOfUsers.of(1))
                                .build(),
                        JLoadProfileUsers
                                .builder(NumberOfUsers.of(1))
                                .withStartDelayInSeconds(properties.getXmlTestUserDelay()) //20sec
                                .build(),
                        JLoadProfileUsers
                                .builder(NumberOfUsers.of(1))
                                .withStartDelayInSeconds(properties.getXmlTestUserDelay() * 2) //40sec
                                .build()
                )
                .withDelayBetweenInvocationsInMilliseconds(properties.getXmlTestInvocationDelay()) //15sec
                .build();

        JLoadTest xmlLT = JLoadTest.builder(Id.of("xmlLT"), xmlTD, xmlUG, xmlTCD).build();

        JParallelTestsGroup xmlPTG = JParallelTestsGroup.builder(Id.of("xmlPTG"), xmlLT).build();


        //2 users in parallel: 1 user for 3 minutes with 20 seconds delay between invocation, second with 15 seconds delay working in background
        //path = /response-headers?key=value
        JTestDefinition responseTD = JTestDefinition
                .builder(Id.of("responseTD"), new EndpointsProvider(properties.getCommonUrl()))
                .withInvoker(new DefaultInvokerProvider())
                .withQueryProvider(new QueryProvider(properties.getResponseTestPath()))
                .withLoadBalancer(new OneByOneLoadBalancer())
                .addValidator(new JSONResponseTypeValidatorProvider())
                .addValidator(new EmptyKeyValidatorProvider())
                .addValidators(commonResponseValidators())
                .addListener(new ResponseContentLengthInvocationListener())
                .addListeners(commonInvocationListeners())
                .build();

        JTerminationCriteriaDuration responseTCD1 = JTerminationCriteriaDuration.of(DurationInSeconds.of(properties.getResponseTestDuration())); //180sec

        JTerminationCriteriaBackground responseTCD2 = JTerminationCriteriaBackground.getInstance();

        JLoadProfileUserGroups responseUG1 = JLoadProfileUserGroups
                .builder(JLoadProfileUsers.builder(NumberOfUsers.of(1)).build())
                .withDelayBetweenInvocationsInMilliseconds(properties.getResponseTestInvocationDelayU1()) //20sec
                .build();

        JLoadProfileUserGroups responseUG2 = JLoadProfileUserGroups
                .builder(JLoadProfileUsers.builder(NumberOfUsers.of(1)).build())
                .withDelayBetweenInvocationsInMilliseconds(properties.getResponseTestInvocationDelayU2()) //15sec
                .build();

        JLoadTest responseLT1 = JLoadTest.builder(Id.of("responseU1LT"), responseTD, responseUG1, responseTCD1).build();

        JLoadTest responseLT2 = JLoadTest.builder(Id.of("responseU2LT"), responseTD, responseUG2, responseTCD2).build();

        JParallelTestsGroup responsePTG = JParallelTestsGroup.builder(Id.of("responsePTG"), responseLT1, responseLT2).build();

        return JLoadScenario.builder(Id.of("commonJLoadScenario"), getU1PTG, getU2PTG, xmlPTG, responsePTG)
                .addListener(new ExampleLoadScenarioListener())
                .withLatencyPercentiles(Arrays.asList(85D, 90D, 95D))
                .build();
    }
}
