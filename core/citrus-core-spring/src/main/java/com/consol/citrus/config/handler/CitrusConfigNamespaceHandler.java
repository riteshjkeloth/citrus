/*
 * Copyright 2006-2010 the original author or authors.
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

package com.consol.citrus.config.handler;

import java.util.Map;

import com.consol.citrus.config.xml.DefaultMessageQueueParser;
import com.consol.citrus.config.xml.DirectEndpointAdapterParser;
import com.consol.citrus.config.xml.DirectEndpointParser;
import com.consol.citrus.config.xml.DirectSyncEndpointParser;
import com.consol.citrus.config.xml.EmptyResponseEndpointAdapterParser;
import com.consol.citrus.config.xml.FunctionLibraryParser;
import com.consol.citrus.config.xml.GlobalVariablesParser;
import com.consol.citrus.config.xml.MessageValidatorRegistryParser;
import com.consol.citrus.config.xml.NamespaceContextParser;
import com.consol.citrus.config.xml.RequestDispatchingEndpointAdapterParser;
import com.consol.citrus.config.xml.SchemaParser;
import com.consol.citrus.config.xml.SchemaRepositoryParser;
import com.consol.citrus.config.xml.SequenceAfterSuiteParser;
import com.consol.citrus.config.xml.SequenceAfterTestParser;
import com.consol.citrus.config.xml.SequenceBeforeSuiteParser;
import com.consol.citrus.config.xml.SequenceBeforeTestParser;
import com.consol.citrus.config.xml.StaticResponseEndpointAdapterParser;
import com.consol.citrus.config.xml.TestActorParser;
import com.consol.citrus.config.xml.TimeoutProducingEndpointAdapterParser;
import com.consol.citrus.config.xml.ValidationMatcherLibraryParser;
import com.consol.citrus.spi.ResourcePathTypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Namespace handler for components in Citrus configuration.
 *
 * @author Christoph Deppisch
 */
public class CitrusConfigNamespaceHandler extends NamespaceHandlerSupport {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(CitrusConfigNamespaceHandler.class);

    /** Resource path where to find custom config parsers via lookup */
    private static final String RESOURCE_PATH = "META-INF/citrus/config/parser/core";

    @Override
    public void init() {
        registerBeanDefinitionParser("schema-repository", new SchemaRepositoryParser());
        registerBeanDefinitionParser("schema", new SchemaParser());
        registerBeanDefinitionParser("actor", new TestActorParser());
        registerBeanDefinitionParser("global-variables", new GlobalVariablesParser());
        registerBeanDefinitionParser("message-validators", new MessageValidatorRegistryParser());
        registerBeanDefinitionParser("namespace-context", new NamespaceContextParser());
        registerBeanDefinitionParser("function-library", new FunctionLibraryParser());
        registerBeanDefinitionParser("validation-matcher-library", new ValidationMatcherLibraryParser());
        registerBeanDefinitionParser("before-suite", new SequenceBeforeSuiteParser());
        registerBeanDefinitionParser("before-test", new SequenceBeforeTestParser());
        registerBeanDefinitionParser("after-suite", new SequenceAfterSuiteParser());
        registerBeanDefinitionParser("after-test", new SequenceAfterTestParser());
        registerBeanDefinitionParser("direct-endpoint", new DirectEndpointParser());
        registerBeanDefinitionParser("direct-sync-endpoint", new DirectSyncEndpointParser());
        registerBeanDefinitionParser("queue", new DefaultMessageQueueParser());
        registerBeanDefinitionParser("message-queue", new DefaultMessageQueueParser());
        registerBeanDefinitionParser("direct-endpoint-adapter", new DirectEndpointAdapterParser());
        registerBeanDefinitionParser("dispatching-endpoint-adapter", new RequestDispatchingEndpointAdapterParser());
        registerBeanDefinitionParser("static-response-adapter", new StaticResponseEndpointAdapterParser());
        registerBeanDefinitionParser("empty-response-adapter", new EmptyResponseEndpointAdapterParser());
        registerBeanDefinitionParser("timeout-producing-adapter", new TimeoutProducingEndpointAdapterParser());

        lookupBeanDefinitionParser();
    }

    /**
     * Lookup custom bean definition parser from resource path.
     */
    private void lookupBeanDefinitionParser() {
        Map<String, BeanDefinitionParser> actionParserMap = new ResourcePathTypeResolver()
                .resolveAll(RESOURCE_PATH);

        actionParserMap.forEach((k, p) -> {
            registerBeanDefinitionParser(k, p);
            log.info(String.format("Register bean definition parser %s from resource %s", p.getClass(), k));
        });
    }

}
