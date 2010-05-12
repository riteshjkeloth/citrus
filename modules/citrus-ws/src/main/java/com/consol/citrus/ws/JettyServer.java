/*
 * Copyright 2006-2010 ConSol* Software GmbH.
 * 
 * This file is part of Citrus.
 * 
 * Citrus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Citrus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Citrus. If not, see <http://www.gnu.org/licenses/>.
 */

package com.consol.citrus.ws;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.*;
import org.mortbay.jetty.servlet.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.*;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.server.AbstractServer;

/**
 * Jetty server implementation wrapping a {@link Server} with Citrus server behaviour, so
 * server can be started/stopped by Citrus.
 * 
 * @author Christoph Deppisch
 */
public class JettyServer extends AbstractServer implements ApplicationContextAware {

    /** Server port */
    private int port = 8080;
    
    /** Server resource base */
    private String resourceBase = "src/main/resources";
    
    /** Application context location for payload mappings etc. */
    private String contextConfigLocation = "classpath:citrus-ws-servlet.xml";
    
    /** Server instance to be wrapped */
    private Server jettyServer;

    /** Application context used as delegate for parent WebApplicationContext in Jetty */
    private ApplicationContext applicationContext;
    
    @Override
    protected void shutdown() {
        if(jettyServer != null) {
            try {
                jettyServer.stop();
            } catch (Exception e) {
                throw new CitrusRuntimeException(e);
            }
        }
    }

    @Override
    protected void startup() {
        jettyServer = new Server(port);
        
        HandlerCollection handlers = new HandlerCollection();
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        
        Context context = new Context();
        context.setContextPath("/");
        context.setResourceBase(resourceBase);
        
        context.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, new WebApplicationContext() {
            public Resource getResource(String location) {
                return applicationContext.getResource(location);
            }
            public ClassLoader getClassLoader() {
                return applicationContext.getClassLoader();
            }
            public Resource[] getResources(String locationPattern) throws IOException {
                return applicationContext.getResources(locationPattern);
            }
            public void publishEvent(ApplicationEvent event) {
                applicationContext.publishEvent(event);
            }
            public String getMessage(String code, Object[] args, String defaultMessage,
                    Locale locale) {
                return applicationContext.getMessage(code, args, defaultMessage, locale);
            }
            public String getMessage(String code, Object[] args, Locale locale)
                    throws NoSuchMessageException {
                return applicationContext.getMessage(code, args, locale);
            }
            public String getMessage(MessageSourceResolvable resolvable, Locale locale)
                    throws NoSuchMessageException {
                return applicationContext.getMessage(resolvable, locale);
            }
            public BeanFactory getParentBeanFactory() {
                return applicationContext.getParentBeanFactory();
            }
            public boolean containsLocalBean(String name) {
                return applicationContext.containsBean(name);
            }
            @SuppressWarnings("unchecked")
            public boolean isTypeMatch(String name, Class targetType)
                    throws NoSuchBeanDefinitionException {
                return applicationContext.isTypeMatch(name, targetType);
            }
            public boolean isSingleton(String name)
                    throws NoSuchBeanDefinitionException {
                return applicationContext.isSingleton(name);
            }
            public boolean isPrototype(String name)
                    throws NoSuchBeanDefinitionException {
                return applicationContext.isPrototype(name);
            }
            @SuppressWarnings("unchecked")
            public Class getType(String name) throws NoSuchBeanDefinitionException {
                return applicationContext.getType(name);
            }
            public Object getBean(String name, Object[] args) throws BeansException {
                return applicationContext.getBean(name, args);
            }
            @SuppressWarnings("unchecked")
            public Object getBean(String name, Class requiredType)
                    throws BeansException {
                return applicationContext.getBean(name, requiredType);
            }
            public Object getBean(String name) throws BeansException {
                return applicationContext.getBean(name);
            }
            public String[] getAliases(String name) {
                return applicationContext.getAliases(name);
            }
            public boolean containsBean(String name) {
                return applicationContext.containsBean(name);
            }
            @SuppressWarnings("unchecked")
            public Map getBeansOfType(Class type, boolean includeNonSingletons,
                    boolean allowEagerInit) throws BeansException {
                return applicationContext.getBeansOfType(type, includeNonSingletons, allowEagerInit);
            }
            @SuppressWarnings("unchecked")
            public Map getBeansOfType(Class type) throws BeansException {
                return applicationContext.getBeansOfType(type);
            }
            @SuppressWarnings("unchecked")
            public String[] getBeanNamesForType(Class type,
                    boolean includeNonSingletons, boolean allowEagerInit) {
                return applicationContext.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
            }
            @SuppressWarnings("unchecked")
            public String[] getBeanNamesForType(Class type) {
                return applicationContext.getBeanNamesForType(type);
            }
            public String[] getBeanDefinitionNames() {
                return applicationContext.getBeanDefinitionNames();
            }
            public int getBeanDefinitionCount() {
                return applicationContext.getBeanDefinitionCount();
            }
            public boolean containsBeanDefinition(String beanName) {
                return applicationContext.containsBeanDefinition(beanName);
            }
            public long getStartupDate() {
                return applicationContext.getStartupDate();
            }
            public ApplicationContext getParent() {
                return applicationContext.getParent();
            }
            public String getId() {
                return applicationContext.getId();
            }
            public String getDisplayName() {
                return applicationContext.getDisplayName();
            }
            public AutowireCapableBeanFactory getAutowireCapableBeanFactory()
                    throws IllegalStateException {
                return applicationContext.getAutowireCapableBeanFactory();
            }
            public ServletContext getServletContext() {
                return null;
            }
        });
        
        ServletHandler servletHandler = new ServletHandler();
        
        ServletHolder servletHolder = new ServletHolder(new MessageDispatcherServlet());
        servletHolder.setName("spring-ws");
        servletHolder.setInitParameter("contextConfigLocation", contextConfigLocation);
        
        servletHandler.addServlet(servletHolder);
        
        ServletMapping servletMapping = new ServletMapping();
        servletMapping.setServletName("spring-ws");
        servletMapping.setPathSpec("/*");
        
        servletHandler.addServletMapping(servletMapping);
        
        context.setServletHandler(servletHandler);
        
        contexts.addHandler(context);
        
        handlers.addHandler(contexts);
        
        handlers.addHandler(new DefaultHandler());
        handlers.addHandler(new RequestLogHandler());
        
        jettyServer.setHandler(handlers);
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            jettyServer.start();
        } catch (Exception e) {
            throw new CitrusRuntimeException(e);
        }
    }

    /**
     * Get the server port.
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the server port.
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
    
    /**
     * Set the server resource base.
     * @param resourceBase the resourceBase to set
     */
    public void setResourceBase(String resourceBase) {
        this.resourceBase = resourceBase;
    }

    /**
     * Set the context config location.
     * @param contextConfigLocation the contextConfigLocation to set
     */
    public void setContextConfigLocation(String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    /** (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) 
        throws BeansException {
        this.applicationContext = applicationContext;
    }
}
