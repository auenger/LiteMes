package com.litemes.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Uom;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Uom entity.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface UomRepository {

    Uom save(Uom entity);

    Optional<Uom> findById(Long id);

    Optional<Uom> findByUomCode(String uomCode);

    IPage<Uom> findPage(IPage<Uom> page, String uomCode, String uomName, Integer status);

    Uom update(Uom entity);

    void deleteById(Long id);

    boolean existsByUomCode(String uomCode);

    boolean existsByUomName(String uomName);

    List<Uom> findAllActive();
}
