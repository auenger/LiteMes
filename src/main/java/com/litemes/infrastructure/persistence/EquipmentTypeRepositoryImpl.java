package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.EquipmentType;
import com.litemes.domain.repository.EquipmentTypeRepository;
import com.litemes.infrastructure.persistence.mapper.EquipmentTypeMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of EquipmentTypeRepository.
 */
@ApplicationScoped
public class EquipmentTypeRepositoryImpl implements EquipmentTypeRepository {

    @Inject
    EquipmentTypeMapper equipmentTypeMapper;

    @Override
    public EquipmentType save(EquipmentType entity) {
        equipmentTypeMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<EquipmentType> findById(Long id) {
        return Optional.ofNullable(equipmentTypeMapper.selectById(id));
    }

    @Override
    public Optional<EquipmentType> findByTypeCode(String typeCode) {
        LambdaQueryWrapper<EquipmentType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentType::getTypeCode, typeCode);
        return Optional.ofNullable(equipmentTypeMapper.selectOne(wrapper));
    }

    @Override
    public IPage<EquipmentType> findPage(IPage<EquipmentType> page, String typeCode, String typeName, Integer status) {
        LambdaQueryWrapper<EquipmentType> wrapper = new LambdaQueryWrapper<>();
        if (typeCode != null && !typeCode.isBlank()) {
            wrapper.like(EquipmentType::getTypeCode, typeCode);
        }
        if (typeName != null && !typeName.isBlank()) {
            wrapper.like(EquipmentType::getTypeName, typeName);
        }
        if (status != null) {
            wrapper.eq(EquipmentType::getStatus, status);
        }
        wrapper.orderByDesc(EquipmentType::getCreatedAt);
        return equipmentTypeMapper.selectPage(page, wrapper);
    }

    @Override
    public EquipmentType update(EquipmentType entity) {
        equipmentTypeMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        equipmentTypeMapper.deleteById(id);
    }

    @Override
    public boolean existsByTypeCode(String typeCode) {
        LambdaQueryWrapper<EquipmentType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentType::getTypeCode, typeCode);
        return equipmentTypeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<EquipmentType> findAllActive() {
        LambdaQueryWrapper<EquipmentType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentType::getStatus, 1)
               .orderByAsc(EquipmentType::getTypeCode);
        return equipmentTypeMapper.selectList(wrapper);
    }
}
