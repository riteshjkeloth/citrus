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

package com.consol.citrus.junit.integration;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.junit.JUnit4CitrusSupport;
import org.junit.Test;

import static com.consol.citrus.actions.EchoAction.Builder.echo;

/**
 * @author Christoph Deppisch
 * @since 2.5
 */
public class ContextInjectionJUnit4IT extends JUnit4CitrusSupport {

    @Test
    @CitrusTest
    @SuppressWarnings("squid:S2699")
    public void contextInjection(@CitrusResource TestContext context) {
        context.setVariable("message", "Injection worked!");

        run(echo("${message}"));
    }
}
