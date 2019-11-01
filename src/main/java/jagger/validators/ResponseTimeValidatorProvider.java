package jagger.validators;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

public class ResponseTimeValidatorProvider implements ResponseValidatorProvider {

    @Override
    public ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> provide(String taskId, String sessionId, NodeContext kernelContext) {

        return new ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse>(taskId, sessionId, kernelContext) {

            @Override
            public String getName() {
                return "Request duration validator (Query took less than 4sec)";
            }

            @Override
            public boolean validate(JHttpQuery jHttpQuery, JHttpEndpoint jHttpEndpoint, JHttpResponse jHttpResponse, long l) {
                return l < 4000;
            }
        };
    }
}