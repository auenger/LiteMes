package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Process;
import com.litemes.domain.entity.WorkCenter;
import com.litemes.domain.repository.ProcessRepository;
import com.litemes.infrastructure.persistence.mapper.ProcessMapper;
import com.litemes.infrastructure.persistence.mapper.WorkCenterMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MyBatis-Plus implementation of ProcessRepository.
 * Supports factory-level cascading queries via WorkCenter join.
 */
@ApplicationScoped
public class ProcessRepositoryImpl implements ProcessRepository {

    @Inject
    ProcessMapper processMapper;

    @Inject
    WorkCenterMapper workCenterMapper;

    @Override
    public Process save(Process entity) {
        processMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<Process> findById(Long id) {
        return Optional.ofNullable(processMapper.selectById(id));
    }

    @Override
    public Optional<Process> findByProcessCode(String processCode) {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Process::getProcessCode, processCode);
        return Optional.ofNullable(processMapper.selectOne(wrapper));
    }

    @Override
    public IPage<Process> findPage(IPage<Process> page, String processCode, String name, Long workCenterId, Long factoryId, Integer status) {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();

        if (processCode != null && !processCode.isBlank()) {
            wrapper.like(Process::getProcessCode, processCode);
        }
        if (name != null && !name.isBlank()) {
            wrapper.like(Process::getName, name);
        }
        if (workCenterId != null) {
            wrapper.eq(Process::getWorkCenterId, workCenterId);
        }
        if (status != null) {
            wrapper.eq(Process::getStatus, status);
        }

        // Factory cascading: find all work center IDs for the factory, then filter
        if (factoryId != null) {
            List<Long> wcIds = findWorkCenterIdsByFactoryId(factoryId);
            if (wcIds.isEmpty()) {
                // No work centers for this factory, return empty page
                return page;
            }
            wrapper.in(Process::getWorkCenterId, wcIds);
        }

        wrapper.orderByDesc(Process::getCreatedAt);
        return processMapper.selectPage(page, wrapper);
    }

    @Override
    public Process update(Process entity) {
        processMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        processMapper.deleteById(id);
    }

    @Override
    public boolean existsByProcessCode(String processCode) {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Process::getProcessCode, processCode);
        return processMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByWorkCenterId(Long workCenterId) {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Process::getWorkCenterId, workCenterId);
        return processMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Process> findByWorkCenterIdAndStatus(Long workCenterId, Integer status) {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Process::getWorkCenterId, workCenterId);
        if (status != null) {
            wrapper.eq(Process::getStatus, status);
        }
        return processMapper.selectList(wrapper);
    }

    @Override
    public List<Process> findAllActive() {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Process::getStatus, 1)
               .orderByAsc(Process::getProcessCode);
        return processMapper.selectList(wrapper);
    }

    /**
     * Find all work center IDs that belong to a given factory.
     */
    private List<Long> findWorkCenterIdsByFactoryId(Long factoryId) {
        LambdaQueryWrapper<WorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCenter::getFactoryId, factoryId);
        wrapper.select(WorkCenter::getId);
        return workCenterMapper.selectList(wrapper).stream()
                .map(WorkCenter::getId)
                .collect(Collectors.toList());
    }
}
