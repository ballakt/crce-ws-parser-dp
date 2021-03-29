package cz.zcu.kiv.crce.classmodel.processor;

import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.EndpointType;

public class ProcessorTest {

    private static Map<String, Endpoint> endpoints;
    private static Map<String, Endpoint> expectedEndpoints;

    @BeforeAll
    public static void init() {

        expectedEndpoints = Map.of("/123", new Endpoint("/123", EndpointType.GET),
                "/prvni/uri/trida", new Endpoint("/prvni/uri/trida", EndpointType.PUT),
                "/employee/{id}/prvni/uri/tridaNONSTATICtest",
                new Endpoint("/employee/{id}/prvni/uri/tridaNONSTATICtest",
                        (Set<EndpointType>) (Stream.of(EndpointType.PUT, EndpointType.DELETE)
                                .collect(Collectors.toCollection(HashSet::new)))),
                "/bla/uri/s/argumentem/{id}",
                new Endpoint("/bla/uri/s/argumentem/{id}",
                        (Set<EndpointType>) (Stream.of(EndpointType.PUT)
                                .collect(Collectors.toCollection(HashSet::new)))),
                "/prvni/uri/tridaNONSTATICtest",
                new Endpoint("/prvni/uri/tridaNONSTATICtest",
                        (Set<EndpointType>) (Stream.of(EndpointType.PUT)
                                .collect(Collectors.toCollection(HashSet::new)))),
                "/test", new Endpoint("/test", EndpointType.PUT), "/employee",
                new Endpoint("/employee", EndpointType.POST), "/nejaka/uri/s/argumentem/{id}",
                new Endpoint("/nejaka/uri/s/argumentem/{id}", EndpointType.POST)

        );
        ClassLoader classLoader = ProcessorTest.class.getClassLoader();
        File file = new File(classLoader.getResource("spring_webclient.jar").getFile());
        try {
            endpoints = Processor.process(file);
        } catch (IOException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testProcessing() {

        for (Endpoint endpoint : expectedEndpoints.values()) {
            if (!endpoints.containsKey(endpoint.getUri())) {
                fail("Missing endpoint " + endpoint);
                return;
            }
            Endpoint found = endpoints.get(endpoint.getUri());
            if (!found.equals(endpoint)) {

                fail("Endpoint mismatch " + found + " != " + endpoint);
            }
            System.out.println("ENDPOINT=" + found);
        }

    }
}
