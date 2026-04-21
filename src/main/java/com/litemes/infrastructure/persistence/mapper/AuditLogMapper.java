package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for AuditLog entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
