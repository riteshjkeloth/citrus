/*
 * Copyright 2006-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.channel.selector;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.json.JsonPathUtils;
import com.consol.citrus.validation.matcher.ValidationMatcherUtils;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;
import org.springframework.util.StringUtils;

/**
 * Message selector accepts JSON messages in case JsonPath expression evaluation result matches
 * the expected value. With this selector someone can select messages according to a message payload JSON
 * element value for instance.
 *
 * Syntax is jsonPath:$.root.element
 *
 * @author Christoph Deppisch
 * @since 2.7.5
 */
public class JsonPathPayloadMessageSelector implements MessageSelector {

    /** Expression to evaluate for acceptance */
    private final String expression;

    /** Control value to check for */
    private final String control;

    /** Test context */
    private final TestContext context;

    /** Special selector element name identifying this message selector implementation */
    public static final String JSON_PATH_SELECTOR_ELEMENT = "jsonPath:";

    /**
     * Default constructor using fields.
     */
    public JsonPathPayloadMessageSelector(String expression, String control, TestContext context) {
        this.expression = expression.substring(JSON_PATH_SELECTOR_ELEMENT.length());
        this.control = control;
        this.context = context;
    }

    @Override
    public boolean accept(Message<?> message) {
        String payload;
        if (message.getPayload() instanceof com.consol.citrus.message.Message) {
            payload = ((com.consol.citrus.message.Message) message.getPayload()).getPayload(String.class);
        } else {
            payload = message.getPayload().toString();
        }

        if (StringUtils.hasText(payload) &&
                !payload.trim().startsWith("{") &&
                !payload.trim().startsWith("[")) {
            return false;
        }

        try {
            String value = JsonPathUtils.evaluateAsString(payload, expression);
            if (ValidationMatcherUtils.isValidationMatcherExpression(control)) {
                try {
                    ValidationMatcherUtils.resolveValidationMatcher(expression, value, control, context);
                    return true;
                } catch (ValidationException e) {
                    return false;
                }
            } else {
                return value.equals(control);
            }
        } catch (CitrusRuntimeException e) {
            return false;
        }
    }

    /**
     * Message selector factory for this implementation.
     */
    public static class Factory implements MessageSelectorFactory<JsonPathPayloadMessageSelector> {
        @Override
        public boolean supports(String key) {
            return key.startsWith(JSON_PATH_SELECTOR_ELEMENT);
        }

        @Override
        public JsonPathPayloadMessageSelector create(String key, String value, TestContext context) {
            return new JsonPathPayloadMessageSelector(key, value, context);
        }
    }
}
