package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.EquipmentModel;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for EquipmentModel entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface EquipmentModelRepository {

    EquipmentModel save(EquipmentModel entity);

    Optional<EquipmentModel> findById(Long id);

    Optional<EquipmentModel> findByModelCode(String modelCode);

    IPage<EquipmentModel> findPage(IPage<EquipmentModel> page, String modelCode, String modelName, Long equipmentTypeId, Integer status);

    EquipmentModel update(EquipmentModel entity);

    void deleteById(Long id);

    boolean existsByModelCode(String modelCode);

    boolean existsByEquipmentTypeId(Long equipmentTypeId);

    List<EquipmentModel> findAllActive();

    List<EquipmentModel> findByEquipmentTypeId(Long equipmentTypeId);
}
