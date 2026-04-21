package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.WorkCenter;
import com.litemes.domain.repository.WorkCenterRepository;
import com.litemes.infrastructure.persistence.mapper.WorkCenterMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of WorkCenterRepository.
 */
@ApplicationScoped
public class WorkCenterRepositoryImpl implements WorkCenterRepository {

    @Inject
    WorkCenterMapper workCenterMapper;

    @Override
    public WorkCenter save(WorkCenter entity) {
        workCenterMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<WorkCenter> findById(Long id) {
        return Optional.ofNullable(workCenterMapper.selectById(id));
    }

    @Override
    public Optional<WorkCenter> findByWorkCenterCode(String workCenterCode) {
        LambdaQueryWrapper<WorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCenter::getWorkCenterCode, workCenterCode);
        return Optional.ofNullable(workCenterMapper.selectOne(wrapper));
    }

    @Override
    public IPage<WorkCenter> findPage(IPage<WorkCenter> page, String workCenterCode, String name, Long factoryId, Integer status) {
        LambdaQueryWrapper<WorkCenter> wrapper = new LambdaQueryWrapper<>();
        if (workCenterCode != null && !workCenterCode.isBlank()) {
            wrapper.like(WorkCenter::getWorkCenterCode, workCenterCode);
        }
        if (name != null && !name.isBlank()) {
            wrapper.like(WorkCenter::getName, name);
        }
        if (factoryId != null) {
            wrapper.eq(WorkCenter::getFactoryId, factoryId);
        }
        if (status != null) {
            wrapper.eq(WorkCenter::getStatus, status);
        }
        wrapper.orderByDesc(WorkCenter::getCreatedAt);
        return workCenterMapper.selectPage(page, wrapper);
    }

    @Override
    public WorkCenter update(WorkCenter entity) {
        workCenterMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        workCenterMapper.deleteById(id);
    }

    @Override
    public boolean existsByWorkCenterCode(String workCenterCode) {
        LambdaQueryWrapper<WorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCenter::getWorkCenterCode, workCenterCode);
        return workCenterMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByFactoryId(Long factoryId) {
        LambdaQueryWrapper<WorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCenter::getFactoryId, factoryId);
        return workCenterMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<WorkCenter> findByFactoryIdAndStatus(Long factoryId, Integer status) {
        LambdaQueryWrapper<WorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCenter::getFactoryId, factoryId);
        if (status != null) {
            wrapper.eq(WorkCenter::getStatus, status);
        }
        return workCenterMapper.selectList(wrapper);
    }

    @Override
    public List<WorkCenter> findAllActive() {
        LambdaQueryWrapper<WorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCenter::getStatus, 1)
               .orderByAsc(WorkCenter::getWorkCenterCode);
        return workCenterMapper.selectList(wrapper);
    }
}
