package jagger.calculators;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.AvgMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class XMLItemsInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final String metricNameForAllItems = "avg-number-of-xml-elements";
    private final String metricNameForNotEmptyItems = "avg-number-of-not-empty-xml-elements";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricNameForAllItems)
                .displayName("Number of xml elements")
                .showSummary(true)
                .plotData(true)
                .addAggregator(new AvgMetricAggregatorProvider())
        );

        getMetricService().createMetric(new MetricDescription(metricNameForNotEmptyItems)
                .displayName("Number of not empty xml elements")
                .showSummary(true)
                .plotData(true)
                .addAggregator(new AvgMetricAggregatorProvider())
        );
    }

    @Override
    public InvocationListener provide() {
        return new InvocationListener() {
            @Override
            public void onStart(InvocationInfo invocationInfo) {
            }

            @Override
            public void onSuccess(InvocationInfo invocationInfo) {
                if (invocationInfo.getResult() != null) {
                    try {
                        JHttpResponse jHttpResponse = (JHttpResponse) invocationInfo.getResult();
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        ByteArrayInputStream input = new ByteArrayInputStream(
                                jHttpResponse.getBody().toString().getBytes(StandardCharsets.UTF_8));
                        Document document = builder.parse(input);
                        NodeList list = document.getElementsByTagName("*");
                        getMetricService().saveValue(metricNameForAllItems, list.getLength());

                        int liveNodeCounter = 0;
                        for (int i = 0; i < list.getLength(); i++) {
                            try {
                                if (!list.item(i).getFirstChild().getNodeValue().isEmpty()) {
                                    liveNodeCounter++;
                                }
                            } catch (NullPointerException e) {
                                e.getMessage();
                            }
                        }
                        getMetricService().saveValue(metricNameForNotEmptyItems, liveNodeCounter);
                    } catch (ParserConfigurationException | IOException | SAXException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) {
            }

            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) {
            }
        };
    }
}
