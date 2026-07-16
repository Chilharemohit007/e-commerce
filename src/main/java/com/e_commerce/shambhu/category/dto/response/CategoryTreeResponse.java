package com.e_commerce.shambhu.category.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryTreeResponse {

    private Long id;

    private String name;

    private String slug;

    private String imageUrl;

    private Integer displayOrder;

    private List<CategoryTreeResponse> children = new ArrayList<>();
}