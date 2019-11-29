package jagger;

import com.griddynamics.jagger.engine.e1.collector.DefaultResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.ExampleLoadScenarioListener;
import com.griddynamics.jagger.invoker.OneByOneLoadBalancer;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUserGroups;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaDuration;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.DurationInSeconds;
import jagger.calculators.DurationInvocationListener;
import jagger.calculators.ResponseSizeInvocationListener;
import jagger.calculators.XMLItemsInvocationListener;
import jagger.providers.DefaultInvokerProvider;
import jagger.providers.EndpointsProvider;
import jagger.providers.QueryProvider;
import jagger.util.TestProperties;
import jagger.validators.StatusCodeValidatorProvider;
import jagger.validators.XMLResponseTypeValidatorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class XMLJLoadScenarioProvider {

    @Autowired
    TestProperties properties;

    @Bean
    public JLoadScenario xmlJaggerLoadScenario() {

        JParallelTestsGroup xmlPTG = JParallelTestsGroup
                .builder(Id.of("xmlPTG"), JLoadTest
                        .builder(Id.of("xmlLT"),
                                JTestDefinition
                                        .builder(Id.of("xmlTD"), new EndpointsProvider(properties.getCommonUrl()))
                                        .withInvoker(new DefaultInvokerProvider())
                                        .withQueryProvider(new QueryProvider(properties.getXmlTestPath()))
                                        .withLoadBalancer(new OneByOneLoadBalancer())
                                        .addValidator(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class))
                                        .addValidator(new StatusCodeValidatorProvider())
                                        .addValidator(new XMLResponseTypeValidatorProvider())
                                        .addListener(new NotNullInvocationListener())
                                        .addListener(new DurationInvocationListener())
                                        .addListener(new ResponseSizeInvocationListener())
                                        .addListener(new XMLItemsInvocationListener())
                                        .build(),
                                JLoadProfileUserGroups
                                        .builder(
                                                JLoadProfileUsers
                                                        .builder(NumberOfUsers.of(1)) //1user
                                                        .build(),
                                                JLoadProfileUsers
                                                        .builder(NumberOfUsers.of(1)) //1user
                                                        .withStartDelayInSeconds(properties.getXmlTestUserDelay())//20sec
                                                        .build(),
                                                JLoadProfileUsers
                                                        .builder(NumberOfUsers.of(1)) //1user
                                                        .withStartDelayInSeconds(properties.getXmlTestUserDelay() * 2)//40sec
                                                        .build()
                                        )
                                        .withDelayBetweenInvocationsInMilliseconds(properties.getXmlTestInvocationDelay()) //15sec
                                        .build(),
                                JTerminationCriteriaDuration.of(DurationInSeconds.of(properties.getXmlTestInvocationDuration()))) //300sec
                        .build())
                .build();

        return JLoadScenario.builder(Id.of("xmlJLoadScenario"), xmlPTG)
                .addListener(new ExampleLoadScenarioListener())
                .withLatencyPercentiles(Arrays.asList(85D, 90D, 95D))
                .build();

    }
}
