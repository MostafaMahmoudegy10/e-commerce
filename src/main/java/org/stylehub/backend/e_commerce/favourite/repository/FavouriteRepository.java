package org.stylehub.backend.e_commerce.favourite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.favourite.entity.Favourite;

import java.util.UUID;

public interface FavouriteRepository extends JpaRepository<Favourite, UUID> {



}
