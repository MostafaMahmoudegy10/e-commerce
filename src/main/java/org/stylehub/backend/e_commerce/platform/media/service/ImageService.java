package org.stylehub.backend.e_commerce.platform.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;

import java.io.IOException;
import java.util.Map;

@Service
@AllArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;

    public UploadResponse uploadImage(MultipartFile file) {
     try {
         Map uploadResults = cloudinary.uploader().upload(
                 file.getBytes(),
                 ObjectUtils.asMap(
                         "folder", "e-commerce/products"
                 )
         );
         UploadResponse uploadResponse =
                 new UploadResponse(
                         uploadResults.get("secure_url").toString(),
                         uploadResults.get("public_id").toString()
                 );
         return uploadResponse;
     }catch (IOException e) {
         throw new IllegalArgumentException("the file could not be uploaded"+e.getMessage());
     }
    }

    public void deleteImage(String publicId)  {
      try {
          Map result = cloudinary.uploader().destroy(
                  "ecommerce/products/" + publicId,
                  ObjectUtils.asMap("invalidate", true)
          );
      }catch (IOException e) {
          throw new IllegalArgumentException("the file is not presented "+e.getMessage());

      }
    }
}
