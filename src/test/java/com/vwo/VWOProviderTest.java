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
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Structure;
import dev.openfeature.sdk.Value;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Exercises the real provider evaluation path against a mocked VWO client, so a
 * signature change in the VWO SDK (e.g. the Wingify refactor of vwo-fme-java-sdk
 * 1.65.0, which changed VWO.getFlag to take WingifyUserContext) fails the build
 * instead of surfacing as a runtime NoSuchMethodError in consumer applications.
 */
public class VWOProviderTest {

    private VWO vwoClient;
    private VWOProvider provider;

    @Before
    public void setUp() {
        vwoClient = mock(VWO.class);
        provider = new VWOProvider(vwoClient);
    }

    @Test
    public void booleanEvaluationFallsBackToFlagEnabledWithoutContextKey() {
        GetFlag getFlag = mock(GetFlag.class);
        when(getFlag.isEnabled()).thenReturn(true);
        when(getFlag.getVariables()).thenReturn(Collections.emptyList());
        when(vwoClient.getFlag(eq("my-flag"), any(VWOContext.class))).thenReturn(getFlag);

        ProviderEvaluation<Boolean> result = provider.getBooleanEvaluation("my-flag", false, new TestEvaluationContext());

        assertTrue(result.getValue());
        assertNull(result.getReason());
    }

    @Test
    public void booleanEvaluationResolvesVariableFromContextKey() {
        GetFlag getFlag = mock(GetFlag.class);
        when(getFlag.isEnabled()).thenReturn(false);
        Map<String, Object> variable = new HashMap<>();
        variable.put("type", "boolean");
        variable.put("key", "beta");
        variable.put("value", true);
        when(getFlag.getVariables()).thenReturn(Collections.singletonList(variable));
        when(vwoClient.getFlag(anyString(), any(VWOContext.class))).thenReturn(getFlag);

        ProviderEvaluation<Boolean> result = provider.getBooleanEvaluation("my-flag", false, context("key", "beta"));

        assertTrue(result.getValue());
    }

    @Test
    public void stringEvaluationResolvesVariableFromContextKey() {
        GetFlag getFlag = mock(GetFlag.class);
        Map<String, Object> variable = new HashMap<>();
        variable.put("type", "string");
        variable.put("key", "greeting");
        variable.put("value", "hello");
        when(getFlag.getVariables()).thenReturn(Collections.singletonList(variable));
        when(vwoClient.getFlag(anyString(), any(VWOContext.class))).thenReturn(getFlag);

        ProviderEvaluation<String> result = provider.getStringEvaluation("my-flag", "default", context("key", "greeting"));

        assertEquals("hello", result.getValue());
    }

    @Test
    public void evaluationContextIsMappedToVwoContext() {
        GetFlag getFlag = mock(GetFlag.class);
        when(getFlag.getVariables()).thenReturn(Collections.emptyList());
        when(vwoClient.getFlag(anyString(), any(VWOContext.class))).thenReturn(getFlag);

        Map<String, Object> custom = new HashMap<>();
        custom.put("plan", "pro");
        Map<String, Value> values = new HashMap<>();
        values.put("userId", new Value("user-123"));
        values.put("userAgent", new Value("test-agent"));
        values.put("ipAddress", new Value("127.0.0.1"));
        values.put("customVariables", new Value(Structure.mapToStructure(custom)));

        provider.getStringEvaluation("my-flag", "default", new TestEvaluationContext(values));

        ArgumentCaptor<VWOContext> captor = ArgumentCaptor.forClass(VWOContext.class);
        verify(vwoClient).getFlag(eq("my-flag"), captor.capture());
        VWOContext vwoContext = captor.getValue();
        assertEquals("user-123", vwoContext.getId());
        assertEquals("test-agent", vwoContext.getUserAgent());
        assertEquals("127.0.0.1", vwoContext.getIpAddress());
        assertEquals("pro", vwoContext.getCustomVariables().get("plan"));
    }

    @Test
    public void evaluationFailureReportsErrorReason() {
        when(vwoClient.getFlag(anyString(), any(VWOContext.class)))
                .thenThrow(new IllegalStateException("sdk failure"));

        ProviderEvaluation<String> result = provider.getStringEvaluation("my-flag", "default", new TestEvaluationContext());

        assertEquals("default", result.getValue());
        assertEquals(Reason.ERROR.name(), result.getReason());
        assertFalse(result.getErrorMessage().isEmpty());
    }

    private static EvaluationContext context(String key, String value) {
        Map<String, Value> values = new HashMap<>();
        values.put(key, new Value(value));
        return new TestEvaluationContext(values);
    }

    /** Minimal EvaluationContext backed by a fixed value map (SDK 1.8.0 API). */
    private static class TestEvaluationContext implements EvaluationContext {

        private final Map<String, Value> values;

        TestEvaluationContext() {
            this(new HashMap<>());
        }

        TestEvaluationContext(Map<String, Value> values) {
            this.values = values;
        }

        @Override
        public String getTargetingKey() {
            return null;
        }

        @Override
        public EvaluationContext merge(EvaluationContext other) {
            return other;
        }

        @Override
        public Set<String> keySet() {
            return values.keySet();
        }

        @Override
        public Value getValue(String key) {
            return values.get(key);
        }

        @Override
        public Map<String, Value> asMap() {
            return values;
        }

        @Override
        public Map<String, Object> asObjectMap() {
            Map<String, Object> objectMap = new HashMap<>();
            values.forEach((key, value) -> objectMap.put(key, value.asObject()));
            return objectMap;
        }
    }
}
