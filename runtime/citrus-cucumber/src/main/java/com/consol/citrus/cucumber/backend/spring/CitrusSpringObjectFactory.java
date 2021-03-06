/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.cucumber.backend.spring;

import com.consol.citrus.DefaultTestCaseRunner;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusAnnotations;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.cucumber.backend.CitrusBackend;
import io.cucumber.core.backend.CucumberBackendException;
import io.cucumber.core.backend.ObjectFactory;
import io.cucumber.spring.SpringFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
public class CitrusSpringObjectFactory implements ObjectFactory {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(CitrusSpringObjectFactory.class);

    /** Test runner */
    private TestCaseRunner runner;

    /** Test context */
    private TestContext context;

    /** Static self reference */
    private static CitrusSpringObjectFactory selfReference;

    /** Delegate object factory */
    private final SpringFactory delegate = new SpringFactory();

    /**
     * Default constructor with static self reference initialization.
     */
    public CitrusSpringObjectFactory() {
        selfReference = this;
    }

    @Override
    public void start() {
        delegate.start();
        context = getInstance(TestContext.class);
        runner = new DefaultTestCaseRunner(context);
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        if (context == null) {
            try {
                context = delegate.getInstance(TestContext.class);
                CitrusBackend.initializeCitrus(context.getApplicationContext());
            } catch (CucumberBackendException e) {
                log.warn("Failed to get proper TestContext from Cucumber Spring application context: " + e.getMessage());
                context = CitrusBackend.getCitrus().getCitrusContext().createTestContext();
            }
        }

        if (TestContext.class.isAssignableFrom(type)) {
            return (T) context;
        }

        if (CitrusSpringObjectFactory.class.isAssignableFrom(type)) {
            return (T) this;
        }

        T instance = delegate.getInstance(type);
        CitrusAnnotations.injectAll(instance, CitrusBackend.getCitrus());
        CitrusAnnotations.injectTestRunner(instance, runner);

        return instance;
    }

    @Override
    public boolean addClass(Class<?> glueClass) {
        return delegate.addClass(glueClass);
    }

    /**
     * Static access to self reference.
     * @return
     */
    public static CitrusSpringObjectFactory instance() throws IllegalAccessException {
        if (selfReference == null) {
            throw new IllegalAccessException("Illegal access to self reference - not available yet");
        }

        return selfReference;
    }
}
