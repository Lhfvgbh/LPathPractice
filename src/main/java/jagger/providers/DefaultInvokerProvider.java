package jagger.providers;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

public class DefaultInvokerProvider implements Provider<Invoker>  {
    
    @Override
    public Invoker provide() {
        return new DefaultHttpInvoker() {
            @Override
            public JHttpResponse invoke(JHttpQuery query, JHttpEndpoint endpoint) throws InvocationException {
                return super.invoke(query, endpoint);
            }
        };
    }
}