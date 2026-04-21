package com.litemes.domain.repository;

import com.litemes.domain.entity.Shift;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Shift.
 * Infrastructure layer provides the MyBatis-Plus implementation.
 */
public interface ShiftRepository {

    Shift save(Shift entity);

    Optional<Shift> findById(Long id);

    List<Shift> findByShiftScheduleId(Long shiftScheduleId);

    Shift update(Shift entity);

    void deleteById(Long id);

    Optional<Shift> findByShiftCode(String shiftCode);

    boolean existsByShiftScheduleId(Long shiftScheduleId);
}
