package com.example.idatt2106_2022_05_backend.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Needed in order to upload images to the db (for ads and users). This utility class is responsible for creating the
 * directory if it does not exist, as well as to save the uploaded file from MultipartFile object to a file in the
 * specified directory in the file system.
 */
public class FileUploadUtility {

    public static void saveFile(String uploadDirectory, String fileName, MultipartFile multipartFile)
            throws IOException {

        // The path is retrieved from the directory
        Path uploadPath = Paths.get(uploadDirectory);

        // If a file is not already there, a directory is created
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // When we know the directory exist, we retrieve the multipartFile's content (bytes)
        try (InputStream inputStream = multipartFile.getInputStream()) {

            // Converting the given path string to a Path including the filename (see param)
            Path filePath = uploadPath.resolve(fileName);

            // Copies all bytes from an input stream to a file
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {

            // In case the target file already exists or is a symbolic link
            throw new IOException("Could not save image file: " + fileName, e);
        }
    }
}
