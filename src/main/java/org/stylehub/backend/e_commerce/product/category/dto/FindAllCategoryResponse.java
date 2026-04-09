package org.stylehub.backend.e_commerce.product.category.dto;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.UUID;

public  record FindAllCategoryResponse(
          String categoryName,
          Gender categoryGender,
          UUID id
  ){}


