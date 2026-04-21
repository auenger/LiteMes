package com.litemes.domain.repository;

import com.litemes.domain.entity.ShiftSchedule;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for ShiftSchedule.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface ShiftScheduleRepository {

    ShiftSchedule save(ShiftSchedule entity);

    Optional<ShiftSchedule> findById(Long id);

    List<ShiftSchedule> findAll();

    List<ShiftSchedule> findByCondition(String shiftCode, String name, Integer status, int pageNum, int pageSize);

    long countByCondition(String shiftCode, String name, Integer status);

    ShiftSchedule update(ShiftSchedule entity);

    void deleteById(Long id);

    Optional<ShiftSchedule> findByShiftCode(String shiftCode);

    void clearDefault();

    List<ShiftSchedule> findDefault();

    List<ShiftSchedule> findAllActive();
}
