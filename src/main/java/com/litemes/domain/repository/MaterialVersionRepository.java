package com.litemes.domain.repository;

import com.litemes.domain.entity.MaterialVersion;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for MaterialVersion entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface MaterialVersionRepository {

    MaterialVersion save(MaterialVersion entity);

    Optional<MaterialVersion> findById(Long id);

    List<MaterialVersion> findByMaterialId(Long materialId);

    MaterialVersion update(MaterialVersion entity);

    void deleteById(Long id);

    boolean existsByMaterialIdAndVersionNo(Long materialId, String versionNo);

    boolean existsByMaterialId(Long materialId);
}
