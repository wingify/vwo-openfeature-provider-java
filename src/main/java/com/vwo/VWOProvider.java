/**
 * Copyright 2024 Wingify Software Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vwo;

import com.vwo.models.user.GetFlag;
import com.vwo.models.user.VWOContext;
import com.vwo.models.user.VWOInitOptions;
import dev.openfeature.sdk.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VWOProvider implements FeatureProvider {

    private VWO client;
    private List<Hook> hooks;

    @Override
    public Metadata getMetadata() {
        return new Metadata() {
            @Override
            public String getName() {
                return "vwo-openfeature-provider-java-provider";
            }
        };
    }

    /**
     * Constructor to initialize the VWO client
     * @param vwoOptions VWO parameters to initialize the client
     */
    public VWOProvider(Map<String, Object> vwoOptions) {
        try {
            VWOInitOptions vwoInitOptions = new VWOInitOptions();
            if (vwoOptions.get("sdkKey") == null) {
                throw new IllegalArgumentException("sdkKey is required to initialize VWO. Please provide the sdkKey in the options.");
            }
            if (vwoOptions.get("accountId") == null) {
                throw new IllegalArgumentException("accountId is required to initialize VWO. Please provide the accountId in the options.");
            }
            vwoInitOptions.setSdkKey(vwoOptions.get("sdkKey").toString());
            vwoInitOptions.setAccountId(Integer.parseInt(vwoOptions.get("accountId").toString()));
            this.client = VWO.init(vwoInitOptions);
        } catch (Exception e) {
            System.err.println("Encountered unrecoverable initialization error, " + e);
        }
    }

    public VWOProvider(VWO vwoClient) {
        try {
            this.client = vwoClient;
        } catch (Exception e) {
            System.err.println("Encountered unrecoverable initialization error, " + e);
        }
    }

    /**
     * Method to get the boolean evaluation for the given key
     * @param key Flag key
     * @param defaultValue Default value
     * @param ctx Evaluation context
     * @return ProviderEvaluation<Boolean>
     */
    @Override
    public ProviderEvaluation<Boolean> getBooleanEvaluation(String key, Boolean defaultValue, EvaluationContext ctx) {
        try {
            VWOContext vwoContext = getVWOContext(ctx);
            GetFlag getFlag = this.client.getFlag(key, vwoContext);
            List<Map<String, Object>> variables = getFlag.getVariables();

            Boolean value = defaultValue;

            Value ctxKey = ctx.getValue("key");
            if (ctxKey != null) {
                for (Map<String, Object> variable : variables) {
                    if ("boolean".equals(variable.get("type")) && ctxKey.asString().equals(variable.get("key"))) {
                        value = (Boolean) variable.get("value");
                        break;
                    }
                }
            } else {
                value = getFlag.isEnabled();
            }
            return getEvaluation(value);
        } catch (Exception e) {
            System.err.println("Encountered unrecoverable error, " + e);
            return getEvaluation(defaultValue, e.getMessage());
        }
    }

    /**
     * Method to get the string evaluation for the given key
     * @param key Flag key
     * @param defaultValue Default value
     * @param ctx Evaluation context
     * @return ProviderEvaluation<String>
     */
    @Override
    public ProviderEvaluation<String> getStringEvaluation(String key, String defaultValue, EvaluationContext ctx) {
        try {
            VWOContext vwoContext = getVWOContext(ctx);
            GetFlag getFlag = this.client.getFlag(key, vwoContext);
            List<Map<String, Object>> variables = getFlag.getVariables();

            String value = defaultValue;

            Value ctxKey = ctx.getValue("key");
            if (ctxKey != null) {
                for (Map<String, Object> variable : variables) {
                    if ("string".equals(variable.get("type")) && ctxKey.asString().equals(variable.get("key"))) {
                        value = (String) variable.get("value");
                        break;
                    }
                }
            }
            return getEvaluation(value);
        } catch (Exception e) {
            System.err.println("Encountered unrecoverable error, " + e);
            return getEvaluation(defaultValue, e.getMessage());
        }
    }

    /**
     * Method to get the integer evaluation for the given key
     * @param key Flag key
     * @param defaultValue Default value
     * @param ctx Evaluation context
     * @return ProviderEvaluation<Integer>
     */
    @Override
    public ProviderEvaluation<Integer> getIntegerEvaluation(String key, Integer defaultValue, EvaluationContext ctx) {
        try {
            VWOContext vwoContext = getVWOContext(ctx);
            GetFlag getFlag = this.client.getFlag(key, vwoContext);
            List<Map<String, Object>> variables = getFlag.getVariables();

            Integer value = defaultValue;

            Value ctxKey = ctx.getValue("key");
            if (ctxKey != null) {
                for (Map<String, Object> variable : variables) {
                    if ("integer".equals(variable.get("type")) && ctxKey.asString().equals(variable.get("key"))) {
                        value = (Integer) variable.get("value");
                        break;
                    }
                }
            }
            return getEvaluation(value);
        } catch (Exception e) {
            System.err.println("Encountered unrecoverable error, " + e);
            return getEvaluation(defaultValue, e.getMessage());
        }
    }

    /**
     * Method to get the double evaluation for the given key
     * @param key Flag key
     * @param defaultValue Default value
     * @param ctx Evaluation context
     * @return ProviderEvaluation<Double>
     */
    @Override
    public ProviderEvaluation<Double> getDoubleEvaluation(String key, Double defaultValue, EvaluationContext ctx) {
        try {
            VWOContext vwoContext = getVWOContext(ctx);
            GetFlag getFlag = this.client.getFlag(key, vwoContext);
            List<Map<String, Object>> variables = getFlag.getVariables();

            Double value = defaultValue;

            Value ctxKey = ctx.getValue("key");
            if (ctxKey != null) {
                for (Map<String, Object> variable : variables) {
                    if ("double".equals(variable.get("type")) && ctxKey.asString().equals(variable.get("key"))) {
                        value = (Double) variable.get("value");
                        break;
                    }
                }
            }
            return getEvaluation(value);
        } catch (Exception e) {
            System.err.println("Encountered unrecoverable error, " + e);
            return getEvaluation(defaultValue, e.getMessage());
        }
    }

    /**
     * Method to get the object evaluation for the given key
     * @param key Flag key
     * @param defaultValue Default value
     * @param ctx Evaluation context
     * @return ProviderEvaluation<Value>
     */
    @Override
    public ProviderEvaluation<Value> getObjectEvaluation(String key, Value defaultValue, EvaluationContext ctx) {
        try {
            VWOContext vwoContext = this.getVWOContext(ctx);
            GetFlag getFlag = this.client.getFlag(key, vwoContext);
            List<Map<String, Object>> variables = getFlag.getVariables();
            Value value = defaultValue;
            Value ctxKey = ctx.getValue("key");

            if (ctxKey != null) {
                for (Map<String, Object> variable : variables) {
                    if ("json".equals(variable.get("type")) && ctxKey.asString().equals(variable.get("key"))) {
                        Object variableValue = variable.get("value");
                        value = convertToValue(variableValue);
                        break;
                    }
                }
            } else {
                value = convertToValue(variables);
            }

            return this.getEvaluation(value);
        } catch (Exception e) {
            System.err.println("Encountered unrecoverable error: " + e);
            e.printStackTrace();
            return this.getEvaluation(defaultValue, e.getMessage());
        }
    }

    /**
     * Converts an object to a Value instance.
     * @param obj the object to convert
     * @return the converted Value instance
     */
    private Value convertToValue(Object obj) {
        try {
            if (obj instanceof Map) {
                // Convert Map to Structure
                return new Value(Structure.mapToStructure((Map<String, Object>) obj));
            } else if (obj instanceof List) {
                // Convert List of Objects to List of Values
                List<Value> valueList = ((List<?>) obj).stream()
                        .map(Value::objectToValue)
                        .collect(Collectors.toList());
                return new Value(valueList);
            } else {
                // Use the existing objectToValue method for other types
                return Value.objectToValue(obj);
            }
        } catch (Exception e) {
            System.err.println("Encountered unrecoverable error during conversion: " + e);
            e.printStackTrace();
            return new Value();
        }
    }

    /**
     * Method to get the list evaluation for the given key
     */
    private <T> ProviderEvaluation<T> getEvaluation(T value) {
        ProviderEvaluation<T> evaluation = new ProviderEvaluation<>();
        evaluation.setValue(value);
        return evaluation;
    }

    /**
     * Method to get the evaluation for the given value and error
     */
    private <T> ProviderEvaluation<T> getEvaluation(T value, String error) {
        ProviderEvaluation<T> evaluation = new ProviderEvaluation<>();
        evaluation.setValue(value);
        evaluation.setReason(Reason.ERROR.name());
        evaluation.setErrorMessage(error);
        return evaluation;
    }

    /**
     * Method to get the VWO context
     * @param ctx Evaluation context
     * @return VWOContext
     */
    private VWOContext getVWOContext(EvaluationContext ctx) {
        VWOContext vwoContext = new VWOContext();
        if (ctx.getValue("userId") != null) {
            vwoContext.setId(ctx.getValue("userId").asString());
        }
        if (ctx.getValue("customVariables") != null && ctx.getValue("customVariables").isStructure()) {
            Structure customVariables = ctx.getValue("customVariables").asStructure();
            Map<String, Value> customVariablesMap = customVariables.asMap();
            Map<String, Object> resultMap = new HashMap<>();
            for (Map.Entry<String, Value> entry : customVariablesMap.entrySet()) {
                resultMap.put(entry.getKey(), entry.getValue().asObject());
            }
            vwoContext.setCustomVariables(resultMap);
        }
        // check if userAgent is not null
        if (ctx.getValue("userAgent") != null) {
            vwoContext.setUserAgent(ctx.getValue("userAgent").asString());
        }
        // check if ip Address is not null
        if (ctx.getValue("ipAddress") != null) {
            vwoContext.setIpAddress(ctx.getValue("ipAddress").asString());
        }
        return vwoContext;
    }
}
