package jagger;

import jagger.calculators.*;
import jagger.providers.*;
import jagger.util.*;
import com.griddynamics.jagger.engine.e1.collector.DefaultResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.ExampleLoadScenarioListener;
import com.griddynamics.jagger.invoker.OneByOneLoadBalancer;
import com.griddynamics.jagger.user.test.configurations.*;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import jagger.validators.JSONResponseTypeValidatorProvider;
import jagger.validators.StatusCodeValidatorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class GetJLoadScenarioProvider {

    @Autowired
    TestProperties properties;

    @Bean
    public JLoadScenario getJaggerLoadScenario() {

        JTestDefinition getTD = JTestDefinition
                .builder(Id.of("getTD"), new EndpointsProvider(properties.getCommonUrl()))
                .withInvoker(new DefaultInvokerProvider())
                .withQueryProvider(new QueryProvider(properties.getGetTestPath()))
                .withLoadBalancer(new OneByOneLoadBalancer())
                .addValidator(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class))
                .addValidator(new StatusCodeValidatorProvider())
                .addValidator(new JSONResponseTypeValidatorProvider())
                .addListener(new NotNullInvocationListener())
                .addListener(new DurationInvocationListener())
                .addListener(new ResponseSizeInvocationListener())
                .addListener(new ResponseArgumentsInvocationListener())
                .addListener(new ResponseHeadersInvocationListener())
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

        return JLoadScenario.builder(Id.of("getJLoadScenario"), getU1PTG, getU2PTG)
                .addListener(new ExampleLoadScenarioListener())
                .withLatencyPercentiles(Arrays.asList(85D, 90D, 95D))
                .build();
    }
}
