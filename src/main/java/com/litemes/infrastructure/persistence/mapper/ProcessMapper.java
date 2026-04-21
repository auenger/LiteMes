package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.Process;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for Process entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface ProcessMapper extends BaseMapper<Process> {
}
