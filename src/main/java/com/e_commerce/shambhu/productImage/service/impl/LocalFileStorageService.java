package com.e_commerce.shambhu.productImage.service.impl;

import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.util.StoredFile;
import com.e_commerce.shambhu.config.FileStorageProperties;
import com.e_commerce.shambhu.productImage.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LocalFileStorageService.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    private final FileStorageProperties properties;

    @Override
    public StoredFile upload(MultipartFile file) {

        validateExtension(file);

        try {

            Path uploadPath = Paths.get(properties.getUploadDir())
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadPath);

            String extension = getFileExtension(
                    file.getOriginalFilename()
            );

            String fileName =
                    UUID.randomUUID() + "." + extension;

            Path targetLocation =
                    uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            LOGGER.info(
                    "File uploaded successfully. FileName={}",
                    fileName
            );

            String fileUrl =
                    "/uploads/products/" + fileName;

            return StoredFile.builder()
                    .fileName(fileName)
                    .fileUrl(fileUrl)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

        } catch (IOException ex) {

            LOGGER.error(
                    "Failed to upload file.",
                    ex
            );

            throw new BusinessException(
                    "Unable to store image."
            );
        }
    }

    @Override
    public void delete(String fileName) {

        try {

            Path filePath = Paths.get(properties.getUploadDir())
                    .resolve(fileName)
                    .normalize();

            Files.deleteIfExists(filePath);

            LOGGER.info(
                    "File deleted successfully. FileName={}",
                    fileName
            );

        } catch (IOException ex) {

            LOGGER.error(
                    "Unable to delete file {}",
                    fileName,
                    ex
            );

            throw new BusinessException(
                    "Unable to delete image."
            );
        }
    }

    private void validateExtension(MultipartFile file) {

        String extension =
                getFileExtension(file.getOriginalFilename());

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {

            LOGGER.warn(
                    "Invalid file extension: {}",
                    extension
            );

            throw new BusinessException(
                    "Unsupported file type."
            );
        }
    }

    private String getFileExtension(String fileName) {

        String cleanFileName =
                StringUtils.cleanPath(fileName);

        int index =
                cleanFileName.lastIndexOf('.');

        if (index == -1) {

            throw new BusinessException(
                    "File extension is missing."
            );
        }

        return cleanFileName.substring(index + 1);
    }
}