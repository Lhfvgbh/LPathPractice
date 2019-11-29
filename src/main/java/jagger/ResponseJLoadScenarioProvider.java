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
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaDuration;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.DurationInSeconds;
import jagger.calculators.DurationInvocationListener;
import jagger.calculators.ResponseContentLengthInvocationListener;
import jagger.calculators.ResponseSizeInvocationListener;
import jagger.providers.DefaultInvokerProvider;
import jagger.providers.EndpointsProvider;
import jagger.providers.QueryProvider;
import jagger.util.TestProperties;
import jagger.validators.EmptyKeyValidatorProvider;
import jagger.validators.StatusCodeValidatorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class ResponseJLoadScenarioProvider {

    @Autowired
    TestProperties properties;

    @Bean
    public JLoadScenario responseJaggerLoadScenario() {

        JTestDefinition jTestDefinition = JTestDefinition
                .builder(Id.of("responseTD"), new EndpointsProvider(properties.getCommonUrl()))
                .withInvoker(new DefaultInvokerProvider())
                .withQueryProvider(new QueryProvider(properties.getResponseTestPath()))
                .withLoadBalancer(new OneByOneLoadBalancer())
                .addValidator(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class))
                .addValidator(new StatusCodeValidatorProvider())
                .addValidator(new EmptyKeyValidatorProvider())
                .addListener(new NotNullInvocationListener())
                .addListener(new DurationInvocationListener())
                .addListener(new ResponseSizeInvocationListener())
                .addListener(new ResponseContentLengthInvocationListener())
                .build();

        JLoadProfileUsers jLoadProfileUsers = JLoadProfileUsers.builder(NumberOfUsers.of(1)).build();

        JParallelTestsGroup responsePTG = JParallelTestsGroup
                .builder(Id.of("responsePTG"),
                        JLoadTest.builder(Id.of("responseU1LT"),
                                jTestDefinition,
                                JLoadProfileUserGroups
                                        .builder(jLoadProfileUsers)
                                        .withDelayBetweenInvocationsInMilliseconds(properties.getResponseTestInvocationDelayU1()) //20sec
                                        .build(),
                                JTerminationCriteriaDuration.of(DurationInSeconds.of(properties.getResponseTestDuration()))) //180sec
                                .build(),
                        JLoadTest.builder(Id.of("responseU2LT"),
                                jTestDefinition,
                                JLoadProfileUserGroups
                                        .builder(jLoadProfileUsers)
                                        .withDelayBetweenInvocationsInMilliseconds(properties.getResponseTestInvocationDelayU2()) //15sec
                                        .build(),
                                JTerminationCriteriaBackground.getInstance())
                                .build())
                .build();

        return JLoadScenario.builder(Id.of("responseJLoadScenario"), responsePTG)
                .addListener(new ExampleLoadScenarioListener())
                .withLatencyPercentiles(Arrays.asList(85D, 90D, 95D))
                .build();
    }
}
