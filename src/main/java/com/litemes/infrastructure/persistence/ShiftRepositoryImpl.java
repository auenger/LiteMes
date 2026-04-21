package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litemes.domain.entity.Shift;
import com.litemes.domain.repository.ShiftRepository;
import com.litemes.infrastructure.persistence.mapper.ShiftMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of ShiftRepository.
 */
@ApplicationScoped
public class ShiftRepositoryImpl implements ShiftRepository {

    @Inject
    ShiftMapper shiftMapper;

    @Override
    public Shift save(Shift entity) {
        shiftMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<Shift> findById(Long id) {
        return Optional.ofNullable(shiftMapper.selectById(id));
    }

    @Override
    public List<Shift> findByShiftScheduleId(Long shiftScheduleId) {
        LambdaQueryWrapper<Shift> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shift::getShiftScheduleId, shiftScheduleId);
        wrapper.orderByAsc(Shift::getStartTime);
        return shiftMapper.selectList(wrapper);
    }

    @Override
    public Shift update(Shift entity) {
        shiftMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        shiftMapper.deleteById(id);
    }

    @Override
    public Optional<Shift> findByShiftCode(String shiftCode) {
        LambdaQueryWrapper<Shift> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shift::getShiftCode, shiftCode);
        return Optional.ofNullable(shiftMapper.selectOne(wrapper));
    }

    @Override
    public boolean existsByShiftScheduleId(Long shiftScheduleId) {
        LambdaQueryWrapper<Shift> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shift::getShiftScheduleId, shiftScheduleId);
        return shiftMapper.selectCount(wrapper) > 0;
    }
}
