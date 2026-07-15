package com.e_commerce.shambhu.productImage.service;

import com.e_commerce.shambhu.common.util.StoredFile;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Uploads a file and returns its accessible URL or path.
     */
    StoredFile upload(MultipartFile file);

    /**
     * Deletes a previously uploaded file.
     */
    void delete(String fileName);

}
