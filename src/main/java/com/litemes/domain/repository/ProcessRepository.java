package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Process;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Process entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface ProcessRepository {

    Process save(Process entity);

    Optional<Process> findById(Long id);

    Optional<Process> findByProcessCode(String processCode);

    IPage<Process> findPage(IPage<Process> page, String processCode, String name, Long workCenterId, Long factoryId, Integer status);

    Process update(Process entity);

    void deleteById(Long id);

    boolean existsByProcessCode(String processCode);

    boolean existsByWorkCenterId(Long workCenterId);

    List<Process> findByWorkCenterIdAndStatus(Long workCenterId, Integer status);
}
