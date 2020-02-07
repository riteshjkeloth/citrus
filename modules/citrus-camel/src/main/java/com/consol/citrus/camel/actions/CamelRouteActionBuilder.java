package com.consol.citrus.camel.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.consol.citrus.TestActionBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ModelCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * Action builder.
 */
public class CamelRouteActionBuilder implements TestActionBuilder.DelegatingTestActionBuilder<AbstractCamelRouteAction> {

    /** Spring application context */
    private ApplicationContext applicationContext;

    private ModelCamelContext camelContext;
    private List<String> routeIds = new ArrayList<>();

    private TestActionBuilder<? extends AbstractCamelRouteAction> delegate;

    /**
     * Fluent API action building entry method used in Java DSL.
     * @return
     */
    public static CamelRouteActionBuilder camel() {
        return new CamelRouteActionBuilder();
    }

    /**
     * Sets the Camel context to use.
     * @param camelContext
     * @return
     */
    public CamelRouteActionBuilder context(String camelContext) {
        Assert.notNull(applicationContext, "Citrus application context is not initialized!");
        this.camelContext = applicationContext.getBean(camelContext, ModelCamelContext.class);
        return this;
    }

    /**
     * Sets the Camel context to use.
     * @param camelContext
     * @return
     */
    public CamelRouteActionBuilder context(ModelCamelContext camelContext) {
        this.camelContext = camelContext;
        return this;
    }

    /**
     * Adds route ids.
     * @param routeIds
     * @return
     */
    public CamelRouteActionBuilder routes(String... routeIds) {
        return routes(Arrays.asList(routeIds));
    }

    /**
     * Add list of route ids.
     * @param routeIds
     * @return
     */
    public CamelRouteActionBuilder routes(List<String> routeIds) {
        this.routeIds.addAll(routeIds);
        return this;
    }

    /**
     * Creates new Camel routes in route builder.
     * @param routeBuilder
     * @return
     */
    public CreateCamelRouteAction.Builder create(RouteBuilder routeBuilder) {
        if (getCamelContext() == null) {
            context(routeBuilder.getContext());
        }

        CreateCamelRouteAction.Builder builder = new CreateCamelRouteAction.Builder()
                .context(getCamelContext())
                .routeIds(routeIds)
                .route(routeBuilder);

        this.delegate = builder;
        return builder;
    }

    /**
     * Creates new Camel routes from route context XML.
     * @param routeContext
     * @return
     */
    public CreateCamelRouteAction.Builder create(String routeContext) {
        CreateCamelRouteAction.Builder builder = new CreateCamelRouteAction.Builder()
                .context(getCamelContext())
                .routeIds(routeIds)
                .routeContext(routeContext);

        this.delegate = builder;
        return builder;
    }

    /**
     * Execute control bus Camel operations.
     * @return
     */
    public CamelControlBusAction.Builder controlBus() {
        CamelControlBusAction.Builder builder = new CamelControlBusAction.Builder()
                .context(getCamelContext())
                .routeIds(routeIds);

        this.delegate = builder;
        return builder;
    }

    /**
     * Start these Camel routes.
     */
    public StartCamelRouteAction.Builder start(String ... routes) {
        StartCamelRouteAction.Builder builder = new StartCamelRouteAction.Builder()
                .context(getCamelContext())
                .routeIds(routeIds)
                .routes(routes);

        this.delegate = builder;
        return builder;
    }

    /**
     * Stop these Camel routes.
     */
    public StopCamelRouteAction.Builder stop(String ... routes) {
        StopCamelRouteAction.Builder builder = new StopCamelRouteAction.Builder()
                .context(getCamelContext())
                .routeIds(routeIds)
                .routes(routes);

        this.delegate = builder;
        return builder;
    }

    /**
     * Remove these Camel routes.
     */
    public RemoveCamelRouteAction.Builder remove(String ... routes) {
        RemoveCamelRouteAction.Builder builder = new RemoveCamelRouteAction.Builder()
                .context(getCamelContext())
                .routeIds(routeIds)
                .routes(routes);

        this.delegate = builder;
        return builder;
    }

    /**
     * Sets the Spring bean application context.
     * @param applicationContext
     */
    public CamelRouteActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    /**
     * Gets the camel context either explicitly set before or default
     * context from Spring application context.
     * @return
     */
    protected ModelCamelContext getCamelContext() {
        if (camelContext == null) {
            Assert.notNull(applicationContext, "Citrus application context is not initialized!");

            if (applicationContext.containsBean("citrusCamelContext")) {
                camelContext = applicationContext.getBean("citrusCamelContext", ModelCamelContext.class);
            } else {
                camelContext = applicationContext.getBean(ModelCamelContext.class);
            }
        }

        return camelContext;
    }

    @Override
    public AbstractCamelRouteAction build() {
        Assert.notNull(delegate, "Missing delegate action to build");
        return delegate.build();
    }

    @Override
    public TestActionBuilder<?> getDelegate() {
        return delegate;
    }
}