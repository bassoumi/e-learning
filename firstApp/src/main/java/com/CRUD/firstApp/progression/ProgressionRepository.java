package com.CRUD.firstApp.progression;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressionRepository extends JpaRepository<Progression, Integer> {
}
