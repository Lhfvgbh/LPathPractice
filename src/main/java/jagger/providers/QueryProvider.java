package jagger.providers;

import com.google.common.io.Files;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryProvider implements Iterable {

    private String path;

    public QueryProvider(String path) {
        this.path = path;
    }

    @Override
    public Iterator iterator() {
        List<JHttpQuery> queries = new ArrayList<>();

        if (path.equals("/response-headers")) {
            List<String> searchQueries = setQueries();
            if (!searchQueries.isEmpty()) {
                for (String query : searchQueries) {
                    queries.add(new JHttpQuery()
                            .get()
                            .responseBodyType(String.class)
                            .path(this.path)
                            .queryParam("key", query));
                }
            }
        } else {
            queries.add(new JHttpQuery()
                    .get()
                    .responseBodyType(String.class)
                    .path(this.path));
        }
        return queries.iterator();
    }

    private List<String> setQueries() {
        File filename = new File("datasets/queries.csv");
        try {
            return (filename.exists() ? Files.readLines(filename, Charset.defaultCharset()) : new ArrayList<>());
        } catch (IOException e) {
            throw new RuntimeException("File with queries not found: " + filename.getAbsolutePath(), e);
        }
    }
}