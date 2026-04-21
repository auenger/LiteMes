package com.litemes.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litemes.domain.entity.MaterialMaster;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus Mapper for MaterialMaster entity.
 * Provides standard CRUD operations via BaseMapper.
 */
@Mapper
public interface MaterialMasterMapper extends BaseMapper<MaterialMaster> {
}
