package cz.zcu.kiv.crce.classmodel.processor;

import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.HttpMethod;
import cz.zcu.kiv.crce.rest.EndpointParameter;

public class ProcessorTest {

        private static Map<String, Endpoint> springWebClientEndpoints;
        private static Map<String, Endpoint> springWebClientExpectedEndpoints;

        private static Map<String, Endpoint> springResttemplateEndpoints;
        private static Map<String, Endpoint> springResttemplateExpectedEndpoints;

        @BeforeAll
        public static void initWebClient() {
                final Endpoint endpoint1 = new Endpoint("/123", Set.of(HttpMethod.GET),
                                new HashSet<>(),
                                Set.of(new EndpointRequestBody(
                                                "com/baeldung/reactive/model/Employee", false)),
                                Set.of(new EndpointParameter(null, "java/lang/Integer", false)));
                final Endpoint endpoint2 = new Endpoint("/prvni/uri/trida", Set.of(HttpMethod.PUT),
                                new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/lang/String", false)),
                                new HashSet<>());
                final Endpoint endpoint3 = new Endpoint(
                                "/employee/{id}/prvni/uri/tridaNONSTATICtest",
                                Set.of(HttpMethod.PUT, HttpMethod.DELETE),
                                Set.of(new EndpointRequestBody(
                                                "com/baeldung/reactive/model/Employee", false)),
                                Set.of(new EndpointRequestBody(
                                                "com/baeldung/reactive/model/Employee", false),
                                                new EndpointRequestBody("java/lang/String", false)),
                                Set.of(new EndpointParameter(null, "java/lang/Integer", false)));
                final Endpoint endpoint4 = new Endpoint("/bla/uri/s/argumentem/{id}",
                                Set.of(HttpMethod.PUT), new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/lang/String", false)),
                                Set.of(new EndpointParameter(null, "java/lang/Integer", false)));
                final Endpoint endpoint5 = new Endpoint("/prvni/uri/tridaNONSTATICtest",
                                Set.of(HttpMethod.PUT), new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/lang/String", false)),
                                new HashSet<>());
                final Endpoint endpoint7 = new Endpoint("/employee", Set.of(HttpMethod.POST),
                                Set.of(new EndpointRequestBody(
                                                "com/baeldung/reactive/model/Employee", false)),
                                Set.of(new EndpointRequestBody(
                                                "com/baeldung/reactive/model/Employee", false)),
                                new HashSet<>());
                final Endpoint endpoint8 = new Endpoint("/nejaka/uri/s/argumentem/{id}",
                                Set.of(HttpMethod.POST), new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/lang/String", false)),
                                Set.of(new EndpointParameter(null, "java/lang/Integer", false)));

                springWebClientExpectedEndpoints = Map.of("/123", endpoint1, "/prvni/uri/trida",
                                endpoint2, "/employee/{id}/prvni/uri/tridaNONSTATICtest", endpoint3,
                                "/bla/uri/s/argumentem/{id}", endpoint4,
                                "/prvni/uri/tridaNONSTATICtest", endpoint5, "/test",
                                new Endpoint("/test", HttpMethod.PUT), "/employee", endpoint7,
                                "/nejaka/uri/s/argumentem/{id}", endpoint8

                );
                ClassLoader classLoader = ProcessorTest.class.getClassLoader();
                File file = new File(classLoader.getResource("spring_webclient.jar").getFile());
                try {
                        springWebClientEndpoints = Processor.process(file);
                } catch (IOException e) {
                        fail(e.getMessage());
                }

        }

        @BeforeAll
        public static void initResttemplate() {
                final Endpoint endpoint1 = new Endpoint("http://localhost:8090/api/user/users",
                                Set.of(HttpMethod.GET), new HashSet<>(),
                                Set.of(new EndpointRequestBody("com/app/demo/service/ApiService",
                                                false)),
                                new HashSet<>());
                final Endpoint endpoint2 = new Endpoint("http://localhost:8090/api/user/addUser",
                                Set.of(HttpMethod.POST),
                                Set.of(new EndpointRequestBody("com/app/demo/model/User", false)),
                                Set.of(new EndpointRequestBody("com/app/demo/model/User", false)),
                                new HashSet<>());
                final Endpoint endpoint3 = new Endpoint("http://localhost:8090/api/user/patchUser/",
                                Set.of(HttpMethod.PATCH),
                                Set.of(new EndpointRequestBody("com/app/demo/model/User", false)),
                                Set.of(new EndpointRequestBody("com/app/demo/model/User", false)),
                                new HashSet<>());
                final Endpoint endpoint4 =
                                new Endpoint("http://localhost:8090/api/user/deleteUser/",
                                                Set.of(HttpMethod.DELETE), new HashSet<>(),
                                                new HashSet<>(), new HashSet<>());
                springResttemplateExpectedEndpoints = Map.of("http://localhost:8090/api/user/users",
                                endpoint1, "http://localhost:8090/api/user/addUser", endpoint2,
                                "http://localhost:8090/api/user/patchUser/", endpoint3,
                                "http://localhost:8090/api/user/deleteUser/", endpoint4

                );
                ClassLoader classLoader = ProcessorTest.class.getClassLoader();
                File file = new File(classLoader.getResource("spring_resttemplate.jar").getFile());
                try {
                        springResttemplateEndpoints = Processor.process(file);
                } catch (IOException e) {
                        fail(e.getMessage());
                }
        }

        @Test
        public void testWebClientSpring() {

                for (Endpoint endpoint : springWebClientExpectedEndpoints.values()) {
                        if (!springWebClientEndpoints.containsKey(endpoint.getPath())) {
                                fail("Missing endpoint " + endpoint);
                                return;
                        }
                        Endpoint found = springWebClientEndpoints.get(endpoint.getPath());
                        if (!found.equals(endpoint)) {
                                fail("Expected " + endpoint + " but got " + found);
                        }
                }

        }

        @Test
        public void testResttemplateSpring() {

                for (Endpoint endpoint : springResttemplateExpectedEndpoints.values()) {
                        if (!springResttemplateEndpoints.containsKey(endpoint.getPath())) {
                                fail("Missing endpoint " + endpoint);
                                return;
                        }
                        Endpoint found = springResttemplateEndpoints.get(endpoint.getPath());
                        if (!found.equals(endpoint)) {
                                fail("Expected " + endpoint + " but got " + found);
                        }
                }

        }
}
