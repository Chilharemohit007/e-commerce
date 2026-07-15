package com.e_commerce.shambhu.common.util;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoredFile {

    private String fileName;

    private String fileUrl;

    private String contentType;

    private Long fileSize;
}
