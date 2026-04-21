package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.MaterialMaster;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for MaterialMaster entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface MaterialMasterRepository {

    MaterialMaster save(MaterialMaster entity);

    Optional<MaterialMaster> findById(Long id);

    Optional<MaterialMaster> findByMaterialCode(String materialCode);

    IPage<MaterialMaster> findPage(IPage<MaterialMaster> page, String materialCode, String materialName,
                                    Long categoryId, String basicCategory, Integer status);

    MaterialMaster update(MaterialMaster entity);

    void deleteById(Long id);

    boolean existsByMaterialCode(String materialCode);

    boolean existsByMaterialName(String materialName);

    List<MaterialMaster> findAllActive();
}
