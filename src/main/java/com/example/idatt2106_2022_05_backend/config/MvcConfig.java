package com.example.idatt2106_2022_05_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is to display the images in a browser. To do this we
 * must expose the directory containing the uploaded files to the clients (web browsers)
 * so they can access them.
 *
 * Here, we configure Spring MVC to allow access to the directory ad-photos in the file system.
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Resource handlers are used to serve static resources (images in this case)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Exposing the directory containing photos relates to ads
        exposeDirectory("ad-photos", registry);
    }

    // ResourceHandlerRegistry stores registrations of resource handlers for serving static resources such as images
    private void exposeDirectory(String directoryName, ResourceHandlerRegistry registry) {

        // The upload directory is created if non-existent when photos are added
        Path uploadDir = Paths.get(directoryName);

        // Getting the path of the file
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Strip the directory name of the previous directories
        if (directoryName.startsWith("../")) {
            directoryName = directoryName.replace("../", "");
        }

        // Add a new resource handler to the resource handler registry
        registry.addResourceHandler("/" + directoryName + "/**").
                 addResourceLocations("file:/"+ uploadPath + "/");
    }
}
