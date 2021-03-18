package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import cz.zcu.kiv.crce.classmodel.definition.Definition;
import cz.zcu.kiv.crce.classmodel.definition.DefinitionType;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinition;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinitionMap;
import cz.zcu.kiv.crce.classmodel.definition.tools.ArgTools;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.EndpointType;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Operation;

class EndpointHandler extends MethodProcessor {

    private Endpoint endpoint = null;
    private Map<String, Endpoint> endpoints = new HashMap<>();
    private MethodDefinitionMap md = Definition.getDefinitions();

    public EndpointHandler(ClassMap classes) {
        super(classes);
    }

    /**
     * Processes possible endpoints by detecting method CALL like .uri(), .put() etc.
     * 
     * @param operation Operation to be handled
     * @param values    String values
     */
    @Override
    protected void processCALL(Operation operation, Stack<StringBuilder> values) {
        if (md.containsKey(operation.getOwner())) {
            HashMap<String, MethodDefinition> methodDefinitionMap = md.get(operation.getOwner());
            if (!methodDefinitionMap.containsKey(operation.getFuncName())) {
                return;
            }
            MethodDefinition methodDefinition = methodDefinitionMap.get(operation.getFuncName());
            if (endpoint == null) {
                endpoint = new Endpoint();
            }
            Set<DefinitionType> typeSet = methodDefinition.getType();
            if (typeSet.contains(DefinitionType.RETRIEVE)) {
                if (endpoint != null && endpoint.getUri() != null) {
                    Helpers.EndpointF.merge(endpoints, endpoint);
                }
                endpoint = null;
                return;
            }

            if (typeSet.contains(DefinitionType.URI)) {
                StringBuilder val = ArgTools.merge(values, methodDefinition.getArgs());
                endpoint.setUri(val != null ? val.toString() : null);
                return;
            }
            DefinitionType[] types = new DefinitionType[typeSet.size()];
            typeSet.toArray(types);

            for (final DefinitionType type : types) {
                endpoint.addType(EndpointType.values()[type.ordinal()]);
            }
            return;
        }
        super.processCALL(operation, values);
    }

    @Override
    public void process(MethodWrapper method) {
        this.endpoint = null;
        super.process(method);
    }

    /**
     * 
     * @return Endpoints
     */
    public Map<String, Endpoint> getEndpoints() {
        return this.endpoints;
    }
}


public class EndpointProcessor extends ClassProcessor {
    private EndpointHandler endpointHandler;
    private Map<String, Endpoint> endpoints = null;


    public EndpointProcessor(ClassMap classes) {
        super(classes);
        this.endpointHandler = new EndpointHandler(classes);
    }

    /**
     * Process class its methods and fields with endpoints handler
     * 
     * @param class_ Class to processing
     */
    @Override
    public void process(ClassWrapper class_) {
        super.process(class_);
        for (MethodWrapper method : class_.getMethods()) {
            endpointHandler.process(method);
        }
        this.endpoints = endpointHandler.getEndpoints();
    }

    /**
     * 
     * @return Endpoints
     */
    public Map<String, Endpoint> getEndpoints() {
        return this.endpoints;
    }
}
