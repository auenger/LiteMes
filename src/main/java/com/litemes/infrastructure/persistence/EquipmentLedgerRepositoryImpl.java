package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.EquipmentLedger;
import com.litemes.domain.repository.EquipmentLedgerRepository;
import com.litemes.infrastructure.persistence.mapper.EquipmentLedgerMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * MyBatis-Plus implementation of EquipmentLedgerRepository.
 */
@ApplicationScoped
public class EquipmentLedgerRepositoryImpl implements EquipmentLedgerRepository {

    @Inject
    EquipmentLedgerMapper equipmentLedgerMapper;

    @Override
    public EquipmentLedger save(EquipmentLedger entity) {
        equipmentLedgerMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<EquipmentLedger> findById(Long id) {
        return Optional.ofNullable(equipmentLedgerMapper.selectById(id));
    }

    @Override
    public Optional<EquipmentLedger> findByEquipmentCode(String equipmentCode) {
        LambdaQueryWrapper<EquipmentLedger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentLedger::getEquipmentCode, equipmentCode);
        return Optional.ofNullable(equipmentLedgerMapper.selectOne(wrapper));
    }

    @Override
    public IPage<EquipmentLedger> findPage(IPage<EquipmentLedger> page, String equipmentCode,
                                            String equipmentName, Long equipmentTypeId,
                                            Long equipmentModelId, String runningStatus,
                                            String manageStatus, Long factoryId, Integer status) {
        LambdaQueryWrapper<EquipmentLedger> wrapper = new LambdaQueryWrapper<>();
        if (equipmentCode != null && !equipmentCode.isBlank()) {
            wrapper.like(EquipmentLedger::getEquipmentCode, equipmentCode);
        }
        if (equipmentName != null && !equipmentName.isBlank()) {
            wrapper.like(EquipmentLedger::getEquipmentName, equipmentName);
        }
        if (equipmentTypeId != null) {
            wrapper.eq(EquipmentLedger::getEquipmentTypeId, equipmentTypeId);
        }
        if (equipmentModelId != null) {
            wrapper.eq(EquipmentLedger::getEquipmentModelId, equipmentModelId);
        }
        if (runningStatus != null && !runningStatus.isBlank()) {
            wrapper.eq(EquipmentLedger::getRunningStatus, runningStatus);
        }
        if (manageStatus != null && !manageStatus.isBlank()) {
            wrapper.eq(EquipmentLedger::getManageStatus, manageStatus);
        }
        if (factoryId != null) {
            wrapper.eq(EquipmentLedger::getFactoryId, factoryId);
        }
        if (status != null) {
            wrapper.eq(EquipmentLedger::getStatus, status);
        }
        wrapper.orderByDesc(EquipmentLedger::getCreatedAt);
        return equipmentLedgerMapper.selectPage(page, wrapper);
    }

    @Override
    public EquipmentLedger update(EquipmentLedger entity) {
        equipmentLedgerMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        equipmentLedgerMapper.deleteById(id);
    }

    @Override
    public boolean existsByEquipmentCode(String equipmentCode) {
        LambdaQueryWrapper<EquipmentLedger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentLedger::getEquipmentCode, equipmentCode);
        return equipmentLedgerMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByEquipmentModelId(Long equipmentModelId) {
        LambdaQueryWrapper<EquipmentLedger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentLedger::getEquipmentModelId, equipmentModelId);
        return equipmentLedgerMapper.selectCount(wrapper) > 0;
    }
}
