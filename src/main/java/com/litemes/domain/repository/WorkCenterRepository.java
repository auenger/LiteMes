package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.WorkCenter;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for WorkCenter entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface WorkCenterRepository {

    WorkCenter save(WorkCenter entity);

    Optional<WorkCenter> findById(Long id);

    Optional<WorkCenter> findByWorkCenterCode(String workCenterCode);

    IPage<WorkCenter> findPage(IPage<WorkCenter> page, String workCenterCode, String name, Long factoryId, Integer status);

    WorkCenter update(WorkCenter entity);

    void deleteById(Long id);

    boolean existsByWorkCenterCode(String workCenterCode);

    boolean existsByFactoryId(Long factoryId);

    List<WorkCenter> findByFactoryIdAndStatus(Long factoryId, Integer status);
}
