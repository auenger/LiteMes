package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.Uom;
import com.litemes.domain.repository.UomRepository;
import com.litemes.infrastructure.persistence.mapper.UomMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of UomRepository.
 */
@ApplicationScoped
public class UomRepositoryImpl implements UomRepository {

    @Inject
    UomMapper uomMapper;

    @Override
    public Uom save(Uom entity) {
        uomMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<Uom> findById(Long id) {
        return Optional.ofNullable(uomMapper.selectById(id));
    }

    @Override
    public Optional<Uom> findByUomCode(String uomCode) {
        LambdaQueryWrapper<Uom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Uom::getUomCode, uomCode);
        return Optional.ofNullable(uomMapper.selectOne(wrapper));
    }

    @Override
    public IPage<Uom> findPage(IPage<Uom> page, String uomCode, String uomName, Integer status) {
        LambdaQueryWrapper<Uom> wrapper = new LambdaQueryWrapper<>();
        if (uomCode != null && !uomCode.isBlank()) {
            wrapper.like(Uom::getUomCode, uomCode);
        }
        if (uomName != null && !uomName.isBlank()) {
            wrapper.like(Uom::getUomName, uomName);
        }
        if (status != null) {
            wrapper.eq(Uom::getStatus, status);
        }
        wrapper.orderByDesc(Uom::getCreatedAt);
        return uomMapper.selectPage(page, wrapper);
    }

    @Override
    public Uom update(Uom entity) {
        uomMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        uomMapper.deleteById(id);
    }

    @Override
    public boolean existsByUomCode(String uomCode) {
        LambdaQueryWrapper<Uom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Uom::getUomCode, uomCode);
        return uomMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByUomName(String uomName) {
        LambdaQueryWrapper<Uom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Uom::getUomName, uomName);
        return uomMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Uom> findAllActive() {
        LambdaQueryWrapper<Uom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Uom::getStatus, 1)
               .orderByAsc(Uom::getUomCode);
        return uomMapper.selectList(wrapper);
    }
}
