package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litemes.domain.entity.MaterialVersion;
import com.litemes.domain.repository.MaterialVersionRepository;
import com.litemes.infrastructure.persistence.mapper.MaterialVersionMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of MaterialVersionRepository.
 */
@ApplicationScoped
public class MaterialVersionRepositoryImpl implements MaterialVersionRepository {

    @Inject
    MaterialVersionMapper materialVersionMapper;

    @Override
    public MaterialVersion save(MaterialVersion entity) {
        materialVersionMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<MaterialVersion> findById(Long id) {
        return Optional.ofNullable(materialVersionMapper.selectById(id));
    }

    @Override
    public List<MaterialVersion> findByMaterialId(Long materialId) {
        LambdaQueryWrapper<MaterialVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialVersion::getMaterialId, materialId)
               .orderByDesc(MaterialVersion::getCreatedAt);
        return materialVersionMapper.selectList(wrapper);
    }

    @Override
    public MaterialVersion update(MaterialVersion entity) {
        materialVersionMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        materialVersionMapper.deleteById(id);
    }

    @Override
    public boolean existsByMaterialIdAndVersionNo(Long materialId, String versionNo) {
        LambdaQueryWrapper<MaterialVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialVersion::getMaterialId, materialId)
               .eq(MaterialVersion::getVersionNo, versionNo);
        return materialVersionMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByMaterialId(Long materialId) {
        LambdaQueryWrapper<MaterialVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialVersion::getMaterialId, materialId);
        return materialVersionMapper.selectCount(wrapper) > 0;
    }
}
