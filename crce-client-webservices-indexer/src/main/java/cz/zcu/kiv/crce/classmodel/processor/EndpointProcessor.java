package cz.zcu.kiv.crce.classmodel.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cz.zcu.kiv.crce.classmodel.definition.Definition;
import cz.zcu.kiv.crce.classmodel.definition.DefinitionType;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinition;
import cz.zcu.kiv.crce.classmodel.definition.MethodDefinitionMap;
import cz.zcu.kiv.crce.classmodel.definition.tools.ArgTools;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint.EndpointType;
import cz.zcu.kiv.crce.classmodel.processor.tools.PrimitiveClassTester;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.classmodel.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.classmodel.structures.Field;
import cz.zcu.kiv.crce.classmodel.structures.Operation;

class EndpointHandler extends MethodProcessor {

    private Endpoint endpoint = null;
    static Logger logger = LogManager.getLogger("extractor");
    private final String methodSetGetPrefixRegExp = "^(set)";
    private Map<String, Endpoint> endpoints = new HashMap<>();
    private MethodDefinitionMap md = Definition.getDefinitions();

    public EndpointHandler(ClassMap classes) {
        super(classes);
    }

    private String getClassName(String name) {
        return name.substring(1).replace(";", "");
    }

    /**
     * Converts names of getters or setters into field name
     * 
     * @param methodName Name of the getter/setter
     * @return Field name
     */
    private String methodNameIntoFieldName(String methodName) {
        String newMethodName = methodName.replaceFirst(methodSetGetPrefixRegExp, "");
        return newMethodName.replaceFirst(newMethodName.charAt(0) + "",
                Character.toLowerCase(newMethodName.charAt(0)) + "");
    }

    /**
     * Converts fields of class into map structure like "field_name: field_type"
     * 
     * @param class_ Input class
     * @return Map of fields
     */
    private Map<String, Object> fieldsToMap(ClassWrapper class_) {
        Map<String, Object> map = new HashMap<>();
        final List<MethodWrapper> methods = class_.getMethods();
        final Map<String, Field> fields = class_.getFieldsContainer();

        for (final MethodWrapper method : methods) {
            final String expFieldName = methodNameIntoFieldName(method.getMethodStruct().getName());
            if (fields.containsKey(expFieldName)) {
                // checks the field has getter and setter function
                final Field field = fields.get(expFieldName);
                final String fieldName = field.getName();
                final String fieldType = field.getDataType().getBasicType();

                if (PrimitiveClassTester.isPrimitive(fieldType)) {
                    map.put(fieldName, field.getDataType().getBasicType());
                } else if (this.classes.containsKey(fieldType)) {
                    ClassWrapper classWrapper = this.classes.get(fieldType);
                    map.put(fieldName, fieldsToMap(classWrapper));
                } else {
                    logger.error(
                            "Could not find type/class=" + fieldType + "of this field=" + field);
                }

            }
        }
        return map;
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
            if (typeSet.contains(DefinitionType.RESPONSE)) {
                final String className = this.getClassName(values.peek().toString());
                if (this.classes.containsKey(className)) {
                    // class exist int the processede JAR
                    final ClassWrapper class_ = this.classes.get(className);
                    final Map<String, Object> fieldsMap = fieldsToMap(class_);
                    endpoint.setExpectedResponse(fieldsMap);
                } else if (PrimitiveClassTester.isPrimitive(className)) {
                    endpoint.setExpectedResponse(className);
                } else {
                    logger.error("Could not find type/class=" + className);
                }
                if (endpoint.getUri() == null) {
                    endpoint = null;
                    return;
                }
                Helpers.EndpointF.merge(endpoints, endpoint);
                endpoint = null;
                return;
            }

            if (typeSet.contains(DefinitionType.URI)) {
                StringBuilder val = ArgTools.getURI(values, methodDefinition.getArgs());
                endpoint.setUri(val != null ? val.toString() : null);
                return;
            }
            DefinitionType[] types = new DefinitionType[typeSet.size()];
            typeSet.toArray(types);

            for (final DefinitionType type : types) {
                endpoint.addType(EndpointType.values()[type.ordinal()]);
            }
        } else {
            super.processCALL(operation, values);
        }
    }

    /**
     * {@inheritDoc}
     */
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


public class EndpointProcessor {
    private EndpointHandler endpointHandler;
    private Map<String, Endpoint> endpoints = null;
    private FieldProcessor fieldProcessor;


    public EndpointProcessor(ClassMap classes) {
        this.endpointHandler = new EndpointHandler(classes);
        this.fieldProcessor = new FieldProcessor(classes);
    }

    /**
     * Process class its methods and fields with endpoints handler
     * 
     * @param class_ Class to processing
     */
    public void process(ClassWrapper class_) {
        this.fieldProcessor.process(class_);
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
