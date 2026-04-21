package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.MaterialCategory;
import com.litemes.domain.repository.MaterialCategoryRepository;
import com.litemes.infrastructure.persistence.mapper.MaterialCategoryMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of MaterialCategoryRepository.
 */
@ApplicationScoped
public class MaterialCategoryRepositoryImpl implements MaterialCategoryRepository {

    @Inject
    MaterialCategoryMapper materialCategoryMapper;

    @Override
    public MaterialCategory save(MaterialCategory entity) {
        materialCategoryMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<MaterialCategory> findById(Long id) {
        return Optional.ofNullable(materialCategoryMapper.selectById(id));
    }

    @Override
    public Optional<MaterialCategory> findByCategoryCode(String categoryCode) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getCategoryCode, categoryCode);
        return Optional.ofNullable(materialCategoryMapper.selectOne(wrapper));
    }

    @Override
    public Optional<MaterialCategory> findByCategoryName(String categoryName) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getCategoryName, categoryName);
        return Optional.ofNullable(materialCategoryMapper.selectOne(wrapper));
    }

    @Override
    public IPage<MaterialCategory> findPage(IPage<MaterialCategory> page, String categoryCode, String categoryName, Integer status) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        if (categoryCode != null && !categoryCode.isBlank()) {
            wrapper.like(MaterialCategory::getCategoryCode, categoryCode);
        }
        if (categoryName != null && !categoryName.isBlank()) {
            wrapper.like(MaterialCategory::getCategoryName, categoryName);
        }
        if (status != null) {
            wrapper.eq(MaterialCategory::getStatus, status);
        }
        wrapper.orderByDesc(MaterialCategory::getCreatedAt);
        return materialCategoryMapper.selectPage(page, wrapper);
    }

    @Override
    public MaterialCategory update(MaterialCategory entity) {
        materialCategoryMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        materialCategoryMapper.deleteById(id);
    }

    @Override
    public boolean existsByCategoryCode(String categoryCode) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getCategoryCode, categoryCode);
        return materialCategoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getCategoryName, categoryName);
        return materialCategoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean hasChildren(Long parentId) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getParentId, parentId);
        return materialCategoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<MaterialCategory> findAllActive() {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getStatus, 1)
               .orderByAsc(MaterialCategory::getCategoryCode);
        return materialCategoryMapper.selectList(wrapper);
    }

    @Override
    public List<MaterialCategory> findAll() {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MaterialCategory::getCategoryCode);
        return materialCategoryMapper.selectList(wrapper);
    }

    @Override
    public long countByParentId(Long parentId) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getParentId, parentId);
        return materialCategoryMapper.selectCount(wrapper);
    }
}
