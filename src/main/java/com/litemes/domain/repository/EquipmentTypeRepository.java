package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.EquipmentType;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for EquipmentType entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface EquipmentTypeRepository {

    EquipmentType save(EquipmentType entity);

    Optional<EquipmentType> findById(Long id);

    Optional<EquipmentType> findByTypeCode(String typeCode);

    IPage<EquipmentType> findPage(IPage<EquipmentType> page, String typeCode, String typeName, Integer status);

    EquipmentType update(EquipmentType entity);

    void deleteById(Long id);

    boolean existsByTypeCode(String typeCode);

    List<EquipmentType> findAllActive();
}
