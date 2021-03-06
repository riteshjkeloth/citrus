package com.consol.citrus.spi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Type resolver resolves references via resource path lookup. Provided resource paths should point to a resource in classpath
 * (e.g. META-INF/my/resource/path/file-name). The resolver will try to locate the resource as classpath resource and read the file as property
 * file. By default the resolver reads the default type resolver property {@link TypeResolver#DEFAULT_TYPE_PROPERTY} and instantiates a new instance
 * for the given type information.
 *
 * A possible property file content that represents the resource in classpath could look like this:
 * type=com.consol.citrus.MySpecialPojo
 *
 * Users can define custom property names to read instead of the default {@link TypeResolver#DEFAULT_TYPE_PROPERTY}.
 * @author Christoph Deppisch
 */
public class ResourcePathTypeResolver implements TypeResolver {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(ResourcePathTypeResolver.class);

    /** Base path for resources */
    private final String resourceBasePath;

    /**
     * Default constructor using META-INF resource base path.
     */
    public ResourcePathTypeResolver() {
        this("META-INF");
    }

    /**
     * Default constructor initializes with given resource path.
     * @param resourceBasePath
     */
    public ResourcePathTypeResolver(String resourceBasePath) {
        if (resourceBasePath.endsWith("/")) {
            this.resourceBasePath = resourceBasePath.substring(0, resourceBasePath.length() -1);
        } else {
            this.resourceBasePath = resourceBasePath;
        }
    }

    @Override
    public String resolveProperty(String resourcePath, String property) {
        String path = getFullResourcePath(resourcePath);

        InputStream in = ResourcePathTypeResolver.class.getClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new CitrusRuntimeException(String.format("Failed to locate resource path '%s'", path));
        }

        try {
            Properties config = new Properties();
            config.load(in);

            return config.getProperty(property);
        } catch (IOException e) {
            throw new CitrusRuntimeException(String.format("Unable to load properties from resource path configuration at '%s'", path), e);
        }
    }

    @Override
    public <T> T resolve(String resourcePath, String property) {
        String type = resolveProperty(resourcePath, property);
        try {
            return (T) Class.forName(type).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new CitrusRuntimeException(String.format("Failed to resolve classpath resource of type '%s'", type), e);
        }
    }

    @Override
    public <T> Map<String, T> resolveAll(String resourcePath, String property, String keyProperty) {
        Map<String, T> resources = new HashMap<>();
        final String path = getFullResourcePath(resourcePath);

        try {
            Stream.of(new PathMatchingResourcePatternResolver().getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path + "/*"))
                    .forEach(file -> {
                        Optional<String> resourceName = Optional.ofNullable(file.getFilename());
                        if (resourceName.isPresent()) {
                            T resource = resolve(path + "/" + resourceName.get());

                            if (keyProperty != null) {
                                resources.put(resolveProperty(path + "/" + resourceName.get(), keyProperty), resource);
                            } else {
                                resources.put(resourceName.get(), resource);
                            }
                        } else {
                            log.warn(String.format("Skip unsupported resource '%s' for resource lookup", file));
                        }
                    });
        } catch (IOException e) {
            log.warn(String.format("Failed to resolve resources in '%s'", path), e);
        }

        return resources;
    }

    /**
     * Combine base resource path and given resource path to proper full resource path.
     * @param resourcePath
     * @return
     */
    private String getFullResourcePath(String resourcePath) {
        if (resourcePath == null || resourcePath.length() == 0) {
            return resourceBasePath;
        } else if (!resourcePath.startsWith(resourceBasePath)) {
            return resourceBasePath + "/" + resourcePath;
        } else {
            return resourcePath;
        }
    }
}
