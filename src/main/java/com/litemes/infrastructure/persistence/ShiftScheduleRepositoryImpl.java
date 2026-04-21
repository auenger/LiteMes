package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.ShiftSchedule;
import com.litemes.domain.repository.ShiftScheduleRepository;
import com.litemes.infrastructure.persistence.mapper.ShiftScheduleMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of ShiftScheduleRepository.
 */
@ApplicationScoped
public class ShiftScheduleRepositoryImpl implements ShiftScheduleRepository {

    @Inject
    ShiftScheduleMapper shiftScheduleMapper;

    @Override
    public ShiftSchedule save(ShiftSchedule entity) {
        shiftScheduleMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<ShiftSchedule> findById(Long id) {
        return Optional.ofNullable(shiftScheduleMapper.selectById(id));
    }

    @Override
    public List<ShiftSchedule> findAll() {
        return shiftScheduleMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public List<ShiftSchedule> findByCondition(String shiftCode, String name, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<ShiftSchedule> wrapper = buildQueryWrapper(shiftCode, name, status);
        wrapper.orderByDesc(ShiftSchedule::getCreatedAt);
        Page<ShiftSchedule> page = shiftScheduleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.getRecords();
    }

    @Override
    public long countByCondition(String shiftCode, String name, Integer status) {
        LambdaQueryWrapper<ShiftSchedule> wrapper = buildQueryWrapper(shiftCode, name, status);
        return shiftScheduleMapper.selectCount(wrapper);
    }

    @Override
    public ShiftSchedule update(ShiftSchedule entity) {
        shiftScheduleMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        shiftScheduleMapper.deleteById(id);
    }

    @Override
    public Optional<ShiftSchedule> findByShiftCode(String shiftCode) {
        LambdaQueryWrapper<ShiftSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShiftSchedule::getShiftCode, shiftCode);
        return Optional.ofNullable(shiftScheduleMapper.selectOne(wrapper));
    }

    @Override
    public void clearDefault() {
        LambdaQueryWrapper<ShiftSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShiftSchedule::getIsDefault, 1);
        List<ShiftSchedule> defaults = shiftScheduleMapper.selectList(wrapper);
        for (ShiftSchedule schedule : defaults) {
            schedule.setIsDefault(0);
            shiftScheduleMapper.updateById(schedule);
        }
    }

    @Override
    public List<ShiftSchedule> findDefault() {
        LambdaQueryWrapper<ShiftSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShiftSchedule::getIsDefault, 1);
        return shiftScheduleMapper.selectList(wrapper);
    }

    @Override
    public List<ShiftSchedule> findAllActive() {
        LambdaQueryWrapper<ShiftSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShiftSchedule::getStatus, 1)
               .orderByAsc(ShiftSchedule::getShiftCode);
        return shiftScheduleMapper.selectList(wrapper);
    }

    private LambdaQueryWrapper<ShiftSchedule> buildQueryWrapper(String shiftCode, String name, Integer status) {
        LambdaQueryWrapper<ShiftSchedule> wrapper = new LambdaQueryWrapper<>();
        if (shiftCode != null && !shiftCode.isEmpty()) {
            wrapper.like(ShiftSchedule::getShiftCode, shiftCode);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like(ShiftSchedule::getName, name);
        }
        if (status != null) {
            wrapper.eq(ShiftSchedule::getStatus, status);
        }
        return wrapper;
    }
}
