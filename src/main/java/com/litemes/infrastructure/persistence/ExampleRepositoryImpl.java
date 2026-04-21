package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litemes.domain.entity.ExampleEntity;
import com.litemes.domain.repository.ExampleRepository;
import com.litemes.infrastructure.persistence.mapper.ExampleMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of ExampleRepository.
 */
@ApplicationScoped
public class ExampleRepositoryImpl implements ExampleRepository {

    @Inject
    ExampleMapper exampleMapper;

    @Override
    public ExampleEntity save(ExampleEntity entity) {
        exampleMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<ExampleEntity> findById(Long id) {
        return Optional.ofNullable(exampleMapper.selectById(id));
    }

    @Override
    public List<ExampleEntity> findAll() {
        return exampleMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public ExampleEntity update(ExampleEntity entity) {
        exampleMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        exampleMapper.deleteById(id);
    }
}
