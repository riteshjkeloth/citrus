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

package com.consol.citrus.context;

import com.consol.citrus.endpoint.EndpointFactory;
import com.consol.citrus.functions.FunctionRegistry;
import com.consol.citrus.report.MessageListeners;
import com.consol.citrus.report.TestListeners;
import com.consol.citrus.spi.ReferenceResolver;
import com.consol.citrus.validation.MessageValidatorRegistry;
import com.consol.citrus.validation.interceptor.MessageConstructionInterceptors;
import com.consol.citrus.validation.matcher.ValidationMatcherRegistry;
import com.consol.citrus.variable.GlobalVariables;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

/**
 * Factory bean implementation taking care of {@link FunctionRegistry} and {@link GlobalVariables}.
 *
 * @author Christoph Deppisch
 */
public class TestContextFactoryBean extends TestContextFactory implements FactoryBean<TestContext>, InitializingBean, ApplicationContextAware {

    @Autowired
    private FunctionRegistry functionRegistry;

    @Autowired
    private ValidationMatcherRegistry validationMatcherRegistry;

    @Autowired(required = false)
    private GlobalVariables globalVariables = new GlobalVariables();

    @Autowired
    private MessageValidatorRegistry messageValidatorRegistry;

    @Autowired
    private TestListeners testListeners;

    @Autowired
    private MessageListeners messageListeners;

    @Autowired
    private EndpointFactory endpointFactory;

    @Autowired
    private ReferenceResolver referenceResolver;

    @Autowired
    private MessageConstructionInterceptors messageConstructionInterceptors;

    @Autowired(required=false)
    private NamespaceContextBuilder namespaceContextBuilder;

    private ApplicationContext applicationContext;

    /**
     * Construct new factory instance from application context.
     * @param applicationContext
     * @return
     */
    public static TestContextFactory newInstance(ApplicationContext applicationContext) {
        TestContextFactory factory = new TestContextFactory();

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(FunctionRegistry.class))) {
            factory.setFunctionRegistry(applicationContext.getBean(FunctionRegistry.class));
        }

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(ValidationMatcherRegistry.class))) {
            factory.setValidationMatcherRegistry(applicationContext.getBean(ValidationMatcherRegistry.class));
        }

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(GlobalVariables.class))) {
            factory.setGlobalVariables(applicationContext.getBean(GlobalVariables.class));
        }

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(MessageValidatorRegistry.class))) {
            factory.setMessageValidatorRegistry(applicationContext.getBean(MessageValidatorRegistry.class));
        }

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(TestListeners.class))) {
            factory.setTestListeners(applicationContext.getBean(TestListeners.class));
        }

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(MessageListeners.class))) {
            factory.setMessageListeners(applicationContext.getBean(MessageListeners.class));
        }

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(MessageConstructionInterceptors.class))) {
            factory.setMessageConstructionInterceptors(applicationContext.getBean(MessageConstructionInterceptors.class));
        }

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(EndpointFactory.class))) {
            factory.setEndpointFactory(applicationContext.getBean(EndpointFactory.class));
        }

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(ReferenceResolver.class))) {
            factory.setReferenceResolver(applicationContext.getBean(ReferenceResolver.class));
        }

        if (!CollectionUtils.isEmpty(applicationContext.getBeansOfType(NamespaceContextBuilder.class))) {
            factory.setNamespaceContextBuilder(applicationContext.getBean(NamespaceContextBuilder.class));
        }

        return factory;
    }

    @Override
    public Class<TestContext> getObjectType() {
        return TestContext.class;
    }

    @Override
    public TestContext getObject() {
        TestContext context = super.getObject();
        context.setApplicationContext(applicationContext);
        return context;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (functionRegistry != null) {
            super.setFunctionRegistry(functionRegistry);
        }

        if (validationMatcherRegistry != null) {
            super.setValidationMatcherRegistry(validationMatcherRegistry);
        }

        if (globalVariables != null) {
            super.setGlobalVariables(globalVariables);
        }

        if (messageValidatorRegistry != null) {
            super.setMessageValidatorRegistry(messageValidatorRegistry);
        }

        if (testListeners != null) {
            super.setTestListeners(testListeners);
        }

        if (messageListeners != null) {
            super.setMessageListeners(messageListeners);
        }

        if (messageConstructionInterceptors != null) {
            super.setMessageConstructionInterceptors(messageConstructionInterceptors);
        }

        if (endpointFactory != null) {
            super.setEndpointFactory(endpointFactory);
        }

        if (referenceResolver != null) {
            super.setReferenceResolver(referenceResolver);
        }

        if (namespaceContextBuilder != null) {
            super.setNamespaceContextBuilder(namespaceContextBuilder);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
