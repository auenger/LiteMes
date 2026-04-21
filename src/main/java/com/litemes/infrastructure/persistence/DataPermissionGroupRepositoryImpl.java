package com.litemes.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litemes.domain.entity.DataPermissionGroup;
import com.litemes.domain.entity.DataPermissionGroupFactory;
import com.litemes.domain.entity.DataPermissionGroupProcess;
import com.litemes.domain.entity.DataPermissionGroupWorkCenter;
import com.litemes.domain.repository.DataPermissionGroupRepository;
import com.litemes.infrastructure.persistence.mapper.DataPermissionGroupFactoryMapper;
import com.litemes.infrastructure.persistence.mapper.DataPermissionGroupMapper;
import com.litemes.infrastructure.persistence.mapper.DataPermissionGroupProcessMapper;
import com.litemes.infrastructure.persistence.mapper.DataPermissionGroupWorkCenterMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus implementation of DataPermissionGroupRepository.
 */
@ApplicationScoped
public class DataPermissionGroupRepositoryImpl implements DataPermissionGroupRepository {

    @Inject
    DataPermissionGroupMapper groupMapper;

    @Inject
    DataPermissionGroupFactoryMapper factoryMapper;

    @Inject
    DataPermissionGroupWorkCenterMapper workCenterMapper;

    @Inject
    DataPermissionGroupProcessMapper processMapper;

    @Override
    public DataPermissionGroup save(DataPermissionGroup entity) {
        groupMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<DataPermissionGroup> findById(Long id) {
        return Optional.ofNullable(groupMapper.selectById(id));
    }

    @Override
    public IPage<DataPermissionGroup> findPage(IPage<DataPermissionGroup> page, String groupName) {
        LambdaQueryWrapper<DataPermissionGroup> wrapper = new LambdaQueryWrapper<>();
        if (groupName != null && !groupName.isBlank()) {
            wrapper.like(DataPermissionGroup::getGroupName, groupName);
        }
        wrapper.orderByDesc(DataPermissionGroup::getCreatedAt);
        return groupMapper.selectPage(page, wrapper);
    }

    @Override
    public DataPermissionGroup update(DataPermissionGroup entity) {
        groupMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        groupMapper.deleteById(id);
    }

    @Override
    public boolean existsByGroupName(String groupName) {
        LambdaQueryWrapper<DataPermissionGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroup::getGroupName, groupName);
        return groupMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByGroupNameExcludeId(String groupName, Long excludeId) {
        LambdaQueryWrapper<DataPermissionGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroup::getGroupName, groupName);
        wrapper.ne(DataPermissionGroup::getId, excludeId);
        return groupMapper.selectCount(wrapper) > 0;
    }

    // Factory association operations

    @Override
    public List<DataPermissionGroupFactory> findFactoriesByGroupId(Long groupId) {
        LambdaQueryWrapper<DataPermissionGroupFactory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroupFactory::getGroupId, groupId);
        return factoryMapper.selectList(wrapper);
    }

    @Override
    public void saveFactoryAssociation(DataPermissionGroupFactory association) {
        factoryMapper.insert(association);
    }

    @Override
    public void deleteFactoryAssociationsByGroupId(Long groupId) {
        LambdaQueryWrapper<DataPermissionGroupFactory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroupFactory::getGroupId, groupId);
        factoryMapper.delete(wrapper);
    }

    @Override
    public void deleteFactoryAssociationsByGroupIdNotInIds(Long groupId, List<Long> factoryIds) {
        LambdaQueryWrapper<DataPermissionGroupFactory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroupFactory::getGroupId, groupId);
        wrapper.notIn(DataPermissionGroupFactory::getFactoryId, factoryIds);
        factoryMapper.delete(wrapper);
    }

    @Override
    public void saveFactoryAssociationsBatch(List<DataPermissionGroupFactory> associations) {
        for (DataPermissionGroupFactory association : associations) {
            factoryMapper.insert(association);
        }
    }

    // Work Center association operations

    @Override
    public List<DataPermissionGroupWorkCenter> findWorkCentersByGroupId(Long groupId) {
        LambdaQueryWrapper<DataPermissionGroupWorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroupWorkCenter::getGroupId, groupId);
        return workCenterMapper.selectList(wrapper);
    }

    @Override
    public void saveWorkCenterAssociation(DataPermissionGroupWorkCenter association) {
        workCenterMapper.insert(association);
    }

    @Override
    public void deleteWorkCenterAssociationsByGroupId(Long groupId) {
        LambdaQueryWrapper<DataPermissionGroupWorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroupWorkCenter::getGroupId, groupId);
        workCenterMapper.delete(wrapper);
    }

    @Override
    public void deleteWorkCenterAssociationsByGroupIdNotInIds(Long groupId, List<Long> workCenterIds) {
        LambdaQueryWrapper<DataPermissionGroupWorkCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroupWorkCenter::getGroupId, groupId);
        wrapper.notIn(DataPermissionGroupWorkCenter::getWorkCenterId, workCenterIds);
        workCenterMapper.delete(wrapper);
    }

    @Override
    public void saveWorkCenterAssociationsBatch(List<DataPermissionGroupWorkCenter> associations) {
        for (DataPermissionGroupWorkCenter association : associations) {
            workCenterMapper.insert(association);
        }
    }

    // Process association operations

    @Override
    public List<DataPermissionGroupProcess> findProcessesByGroupId(Long groupId) {
        LambdaQueryWrapper<DataPermissionGroupProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroupProcess::getGroupId, groupId);
        return processMapper.selectList(wrapper);
    }

    @Override
    public void saveProcessAssociation(DataPermissionGroupProcess association) {
        processMapper.insert(association);
    }

    @Override
    public void deleteProcessAssociationsByGroupId(Long groupId) {
        LambdaQueryWrapper<DataPermissionGroupProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroupProcess::getGroupId, groupId);
        processMapper.delete(wrapper);
    }

    @Override
    public void deleteProcessAssociationsByGroupIdNotInIds(Long groupId, List<Long> processIds) {
        LambdaQueryWrapper<DataPermissionGroupProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataPermissionGroupProcess::getGroupId, groupId);
        wrapper.notIn(DataPermissionGroupProcess::getProcessId, processIds);
        processMapper.delete(wrapper);
    }

    @Override
    public void saveProcessAssociationsBatch(List<DataPermissionGroupProcess> associations) {
        for (DataPermissionGroupProcess association : associations) {
            processMapper.insert(association);
        }
    }
}
