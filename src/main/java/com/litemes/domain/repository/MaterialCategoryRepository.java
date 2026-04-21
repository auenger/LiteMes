package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.MaterialCategory;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for MaterialCategory entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface MaterialCategoryRepository {

    MaterialCategory save(MaterialCategory entity);

    Optional<MaterialCategory> findById(Long id);

    Optional<MaterialCategory> findByCategoryCode(String categoryCode);

    Optional<MaterialCategory> findByCategoryName(String categoryName);

    IPage<MaterialCategory> findPage(IPage<MaterialCategory> page, String categoryCode, String categoryName, Integer status);

    MaterialCategory update(MaterialCategory entity);

    void deleteById(Long id);

    boolean existsByCategoryCode(String categoryCode);

    boolean existsByCategoryName(String categoryName);

    boolean hasChildren(Long parentId);

    List<MaterialCategory> findAllActive();

    List<MaterialCategory> findAll();

    long countByParentId(Long parentId);
}
