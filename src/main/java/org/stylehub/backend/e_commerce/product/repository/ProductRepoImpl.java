//package org.stylehub.backend.e_commerce.product.repository;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//import org.stylehub.backend.e_commerce.image.dto.UploadResponse;
//import org.stylehub.backend.e_commerce.image.entity.ProductItemImage;
//import org.stylehub.backend.e_commerce.image.service.ImageService;
//import org.stylehub.backend.e_commerce.product.dto.ProductDto;
//import org.stylehub.backend.e_commerce.product.dto.ProductDtoRequest;
//import org.stylehub.backend.e_commerce.product.dto.ProductDtoResponse;
//import org.stylehub.backend.e_commerce.product.entity.Product;
//import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemDto;
//import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;
//import org.stylehub.backend.e_commerce.product.product_item.repository.ProductItemRepository;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class ProductRepoImpl implements ProductRepo {
//
//    private final EntityManagerFactory entityManagerFactory;
//    private final ImageService imageService;
//    private final ProductItemRepository productItemRepository;
//
//    private final Logger log = LoggerFactory.getLogger(ProductRepoImpl.class);
//
//    @Override
//    public ProductDtoResponse addNewProduct(ProductDtoRequest productDtoRequest) {
//       try(EntityManager entityManager =this.getEntityManager()){
//           entityManager.getTransaction().begin();
//           Product product=this.mappedProduct(productDtoRequest.productDto());
//           List<ProductItemDto>productDtoItemList=productDtoRequest.productItemList();
//
//           // for each product item we need to save it to db
//           productDtoItemList.forEach(productItemDto->{
//                 ProductItem pi=new ProductItem();
//                 pi.setProduct(product);
//                 pi.setSize(productItemDto.size());
//                 pi.setColor(productItemDto.color());
//                 pi.setPrice(productItemDto.price());
//                 pi.setStock(productItemDto.stock());
//                 pi.setSku(productItemDto.sku());
//                 pi.setProductItemImages(this.getProductItemImages(productItemDto.imagesOfProductItem(),
//                         pi));
//                 productItemRepository.save(pi);
//           });
//           entityManager.getTransaction().commit();
//           return new ProductDtoResponse(product);
//       }
//    }
//
//
//    private EntityManager getEntityManager() {
//        return entityManagerFactory.createEntityManager();
//    }
//
//
//}
