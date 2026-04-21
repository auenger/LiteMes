package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.EquipmentLedger;

import java.util.Optional;

/**
 * Domain repository interface for EquipmentLedger entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface EquipmentLedgerRepository {

    EquipmentLedger save(EquipmentLedger entity);

    Optional<EquipmentLedger> findById(Long id);

    Optional<EquipmentLedger> findByEquipmentCode(String equipmentCode);

    IPage<EquipmentLedger> findPage(IPage<EquipmentLedger> page, String equipmentCode, String equipmentName,
                                     Long equipmentTypeId, Long equipmentModelId, String runningStatus,
                                     String manageStatus, Long factoryId, Integer status);

    EquipmentLedger update(EquipmentLedger entity);

    void deleteById(Long id);

    boolean existsByEquipmentCode(String equipmentCode);

    boolean existsByEquipmentModelId(Long equipmentModelId);
}
