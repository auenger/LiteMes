package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.EquipmentModel;
import com.litemes.domain.repository.EquipmentModelRepository;
import com.litemes.infrastructure.persistence.mapper.EquipmentModelMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of EquipmentModelRepository.
 */
@ApplicationScoped
public class EquipmentModelRepositoryImpl implements EquipmentModelRepository {

    @Inject
    EquipmentModelMapper equipmentModelMapper;

    @Override
    public EquipmentModel save(EquipmentModel entity) {
        equipmentModelMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<EquipmentModel> findById(Long id) {
        return Optional.ofNullable(equipmentModelMapper.selectById(id));
    }

    @Override
    public Optional<EquipmentModel> findByModelCode(String modelCode) {
        LambdaQueryWrapper<EquipmentModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentModel::getModelCode, modelCode);
        return Optional.ofNullable(equipmentModelMapper.selectOne(wrapper));
    }

    @Override
    public IPage<EquipmentModel> findPage(IPage<EquipmentModel> page, String modelCode, String modelName, Long equipmentTypeId, Integer status) {
        LambdaQueryWrapper<EquipmentModel> wrapper = new LambdaQueryWrapper<>();
        if (modelCode != null && !modelCode.isBlank()) {
            wrapper.like(EquipmentModel::getModelCode, modelCode);
        }
        if (modelName != null && !modelName.isBlank()) {
            wrapper.like(EquipmentModel::getModelName, modelName);
        }
        if (equipmentTypeId != null) {
            wrapper.eq(EquipmentModel::getEquipmentTypeId, equipmentTypeId);
        }
        if (status != null) {
            wrapper.eq(EquipmentModel::getStatus, status);
        }
        wrapper.orderByDesc(EquipmentModel::getCreatedAt);
        return equipmentModelMapper.selectPage(page, wrapper);
    }

    @Override
    public EquipmentModel update(EquipmentModel entity) {
        equipmentModelMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        equipmentModelMapper.deleteById(id);
    }

    @Override
    public boolean existsByModelCode(String modelCode) {
        LambdaQueryWrapper<EquipmentModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentModel::getModelCode, modelCode);
        return equipmentModelMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByEquipmentTypeId(Long equipmentTypeId) {
        LambdaQueryWrapper<EquipmentModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentModel::getEquipmentTypeId, equipmentTypeId);
        return equipmentModelMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<EquipmentModel> findAllActive() {
        LambdaQueryWrapper<EquipmentModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentModel::getStatus, 1)
               .orderByAsc(EquipmentModel::getModelCode);
        return equipmentModelMapper.selectList(wrapper);
    }

    @Override
    public List<EquipmentModel> findByEquipmentTypeId(Long equipmentTypeId) {
        LambdaQueryWrapper<EquipmentModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentModel::getEquipmentTypeId, equipmentTypeId)
               .eq(EquipmentModel::getStatus, 1)
               .orderByAsc(EquipmentModel::getModelCode);
        return equipmentModelMapper.selectList(wrapper);
    }
}
