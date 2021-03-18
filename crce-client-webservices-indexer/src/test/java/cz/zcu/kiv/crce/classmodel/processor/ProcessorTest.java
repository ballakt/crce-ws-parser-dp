package cz.zcu.kiv.crce.classmodel.processor;

import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import cz.zcu.kiv.crce.classmodel.Collector;
import cz.zcu.kiv.crce.classmodel.extracting.Loader;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.EndpointType;

public class ProcessorTest {

    private static Map<String, Endpoint> expectedEndpoints = Map.of("/123",
            new Endpoint("/123", EndpointType.GET), "/prvni/uri/trida",
            new Endpoint("/prvni/uri/trida", EndpointType.PUT), "/employee/{id}",
            new Endpoint("/employee/{id}",
                    (Stream.of(EndpointType.PUT, EndpointType.DELETE)
                            .collect(Collectors.toCollection(HashSet::new)))),
            "/bla/uri/s/argumentem/{id}",
            new Endpoint("/bla/uri/s/argumentem/{id}",
                    (Stream.of(EndpointType.PUT).collect(Collectors.toCollection(HashSet::new)))),
            "/test", new Endpoint("/test", EndpointType.PUT), "/employee",
            new Endpoint("/employee", EndpointType.POST), "/nejaka/uri/s/argumentem/{id}",
            new Endpoint("/nejaka/uri/s/argumentem/{id}", EndpointType.POST)

    );


    @Test
    public void testProcessing() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("spring_webclient.jar").getFile());
        try {
            Loader.loadClasses(file);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        Map<String, Endpoint> endpoints = Processor
                .processMany(Helpers.convertStructMap(Collector.getInstance().getClasses()));



        for (Endpoint endpoint : expectedEndpoints.values()) {
            if (!endpoints.containsKey(endpoint.getUri())) {
                fail("Expected endpoint " + endpoint);
                return;
            }
            Endpoint found = endpoints.get(endpoint.getUri());
            if (!found.equals(endpoint)) {

                fail("Endpoint mismatch " + found + " != " + endpoint);
            }
        }

    }
}
