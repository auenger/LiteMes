package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.MaterialMaster;
import com.litemes.domain.repository.MaterialMasterRepository;
import com.litemes.infrastructure.persistence.mapper.MaterialMasterMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * MyBatis-Plus implementation of MaterialMasterRepository.
 */
@ApplicationScoped
public class MaterialMasterRepositoryImpl implements MaterialMasterRepository {

    @Inject
    MaterialMasterMapper materialMasterMapper;

    @Override
    public MaterialMaster save(MaterialMaster entity) {
        materialMasterMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<MaterialMaster> findById(Long id) {
        return Optional.ofNullable(materialMasterMapper.selectById(id));
    }

    @Override
    public Optional<MaterialMaster> findByMaterialCode(String materialCode) {
        LambdaQueryWrapper<MaterialMaster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialMaster::getMaterialCode, materialCode);
        return Optional.ofNullable(materialMasterMapper.selectOne(wrapper));
    }

    @Override
    public IPage<MaterialMaster> findPage(IPage<MaterialMaster> page, String materialCode, String materialName,
                                            Long categoryId, String basicCategory, Integer status) {
        LambdaQueryWrapper<MaterialMaster> wrapper = new LambdaQueryWrapper<>();
        if (materialCode != null && !materialCode.isBlank()) {
            wrapper.like(MaterialMaster::getMaterialCode, materialCode);
        }
        if (materialName != null && !materialName.isBlank()) {
            wrapper.like(MaterialMaster::getMaterialName, materialName);
        }
        if (categoryId != null) {
            wrapper.eq(MaterialMaster::getCategoryId, categoryId);
        }
        if (basicCategory != null && !basicCategory.isBlank()) {
            wrapper.eq(MaterialMaster::getBasicCategory, basicCategory);
        }
        if (status != null) {
            wrapper.eq(MaterialMaster::getStatus, status);
        }
        wrapper.orderByDesc(MaterialMaster::getCreatedAt);
        return materialMasterMapper.selectPage(page, wrapper);
    }

    @Override
    public MaterialMaster update(MaterialMaster entity) {
        materialMasterMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        materialMasterMapper.deleteById(id);
    }

    @Override
    public boolean existsByMaterialCode(String materialCode) {
        LambdaQueryWrapper<MaterialMaster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialMaster::getMaterialCode, materialCode);
        return materialMasterMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByMaterialName(String materialName) {
        LambdaQueryWrapper<MaterialMaster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialMaster::getMaterialName, materialName);
        return materialMasterMapper.selectCount(wrapper) > 0;
    }
}
