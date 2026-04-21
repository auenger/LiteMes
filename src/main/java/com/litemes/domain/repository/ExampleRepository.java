package com.litemes.domain.repository;

import com.litemes.domain.entity.ExampleEntity;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for ExampleEntity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface ExampleRepository {

    ExampleEntity save(ExampleEntity entity);

    Optional<ExampleEntity> findById(Long id);

    List<ExampleEntity> findAll();

    ExampleEntity update(ExampleEntity entity);

    void deleteById(Long id);
}
