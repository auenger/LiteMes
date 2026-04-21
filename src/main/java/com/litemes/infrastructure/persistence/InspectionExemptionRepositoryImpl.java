package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.InspectionExemption;
import com.litemes.domain.repository.InspectionExemptionRepository;
import com.litemes.infrastructure.persistence.mapper.InspectionExemptionMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of InspectionExemptionRepository.
 */
@ApplicationScoped
public class InspectionExemptionRepositoryImpl implements InspectionExemptionRepository {

    @Inject
    InspectionExemptionMapper inspectionExemptionMapper;

    @Override
    public InspectionExemption save(InspectionExemption entity) {
        inspectionExemptionMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<InspectionExemption> findById(Long id) {
        return Optional.ofNullable(inspectionExemptionMapper.selectById(id));
    }

    @Override
    public IPage<InspectionExemption> findPage(IPage<InspectionExemption> page,
                                                Long materialId, Long supplierId,
                                                Integer status) {
        LambdaQueryWrapper<InspectionExemption> wrapper = new LambdaQueryWrapper<>();
        if (materialId != null) {
            wrapper.eq(InspectionExemption::getMaterialId, materialId);
        }
        if (supplierId != null) {
            wrapper.eq(InspectionExemption::getSupplierId, supplierId);
        }
        if (status != null) {
            wrapper.eq(InspectionExemption::getStatus, status);
        }
        wrapper.orderByDesc(InspectionExemption::getCreatedAt);
        return inspectionExemptionMapper.selectPage(page, wrapper);
    }

    @Override
    public InspectionExemption update(InspectionExemption entity) {
        inspectionExemptionMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        inspectionExemptionMapper.deleteById(id);
    }

    @Override
    public List<InspectionExemption> findExpiredRules() {
        LambdaQueryWrapper<InspectionExemption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionExemption::getStatus, 1)
               .isNotNull(InspectionExemption::getValidTo)
               .lt(InspectionExemption::getValidTo, LocalDate.now());
        return inspectionExemptionMapper.selectList(wrapper);
    }
}
