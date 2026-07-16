package com.e_commerce.shambhu.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.file-storage")
public class FileStorageProperties {

    /**
     * Directory where files will be stored.
     */
    private String uploadDir;

    /**
     * Maximum file size in bytes.
     */
    private long maxFileSize;

    /**
     * Allowed MIME types.
     */
    private List<String> allowedContentTypes;

}
