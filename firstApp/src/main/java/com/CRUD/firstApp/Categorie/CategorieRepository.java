package com.CRUD.firstApp.Categorie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategorieRepository extends JpaRepository<Categorie, Integer> {
    List<Categorie> findByNom(String nom);
}
