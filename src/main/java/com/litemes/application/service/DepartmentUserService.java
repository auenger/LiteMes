package com.litemes.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litemes.domain.entity.Department;
import com.litemes.domain.entity.DepartmentUser;
import com.litemes.domain.entity.User;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.DepartmentRepository;
import com.litemes.domain.repository.DepartmentUserRepository;
import com.litemes.domain.repository.UserRepository;
import com.litemes.web.dto.AssignUserDto;
import com.litemes.web.dto.DepartmentUserDto;
import com.litemes.web.dto.PagedResult;
import com.litemes.web.dto.UserDto;
import com.litemes.web.dto.UserQueryDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Application service for DepartmentUser relationship management.
 * Handles user assignment, removal, and querying.
 */
@ApplicationScoped
public class DepartmentUserService {

    private static final Logger LOG = Logger.getLogger(DepartmentUserService.class);

    @Inject
    DepartmentUserRepository departmentUserRepository;

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    UserRepository userRepository;

    /**
     * List all users assigned to a department.
     */
    public List<DepartmentUserDto> listByDepartment(Long departmentId) {
        LOG.debugf("Listing users for department: %d", departmentId);

        // Verify department exists
        findDepartmentOrThrow(departmentId);

        List<DepartmentUser> relations = departmentUserRepository.findByDepartmentId(departmentId);
        if (relations.isEmpty()) {
            return List.of();
        }

        // Batch load user info
        List<Long> userIds = relations.stream()
                .map(DepartmentUser::getUserId)
                .collect(Collectors.toList());
        Map<Long, User> userMap = userRepository.findByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return relations.stream()
                .map(rel -> toDto(rel, userMap.get(rel.getUserId())))
                .collect(Collectors.toList());
    }

    /**
     * Assign users to a department.
     * Duplicates are skipped with a warning.
     */
    @Transactional
    public void assignUsers(Long departmentId, AssignUserDto dto) {
        LOG.debug("Assigning users to department: " + departmentId + ", user count: " + dto.getUserIds().size());

        // Verify department exists
        findDepartmentOrThrow(departmentId);

        List<String> duplicateUsers = new ArrayList<>();
        int assigned = 0;

        for (Long userId : dto.getUserIds()) {
            // Verify user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "用户不存在: " + userId));

            // Check for duplicate assignment
            if (departmentUserRepository.existsByDepartmentIdAndUserId(departmentId, userId)) {
                duplicateUsers.add(user.getUsername());
                continue;
            }

            DepartmentUser du = new DepartmentUser(departmentId, userId);
            departmentUserRepository.save(du);
            assigned++;
        }

        if (!duplicateUsers.isEmpty()) {
            LOG.warnf("Duplicate assignments skipped for department %d: %s", departmentId, String.join(", ", duplicateUsers));
            if (assigned == 0) {
                throw new BusinessException("USER_ALREADY_ASSIGNED",
                        "该用户已在此部门中: " + String.join(", ", duplicateUsers));
            }
        }

        LOG.infof("Assigned %d users to department %d", assigned, departmentId);
    }

    /**
     * Remove a user from a department (physical delete).
     */
    @Transactional
    public void removeUser(Long departmentId, Long userId) {
        LOG.debugf("Removing user %d from department %d", userId, departmentId);

        // Verify department exists
        findDepartmentOrThrow(departmentId);

        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "用户不存在"));

        // Check if the relationship exists
        if (!departmentUserRepository.existsByDepartmentIdAndUserId(departmentId, userId)) {
            throw new BusinessException("USER_NOT_IN_DEPARTMENT", "该用户不在此部门中");
        }

        departmentUserRepository.deleteByDepartmentIdAndUserId(departmentId, userId);
        LOG.infof("Removed user %d from department %d", userId, departmentId);
    }

    /**
     * Query available users for selection (with optional filtering).
     */
    public PagedResult<UserDto> queryUsers(UserQueryDto query) {
        LOG.debugf("Querying users: username=%s, realName=%s", query.getUsername(), query.getRealName());

        IPage<User> page = new Page<>(query.getPage(), query.getSize());
        IPage<User> result = userRepository.findPage(page, query.getUsername(), query.getRealName());

        List<UserDto> records = result.getRecords().stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());

        return new PagedResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    /**
     * Delete all DepartmentUser records for a department (used when department is deleted).
     */
    @Transactional
    public void deleteByDepartmentId(Long departmentId) {
        LOG.debugf("Deleting all user assignments for department: %d", departmentId);
        departmentUserRepository.deleteByDepartmentId(departmentId);
    }

    /**
     * Check if a department has any user assignments.
     */
    public boolean hasUsers(Long departmentId) {
        return departmentUserRepository.countByDepartmentId(departmentId) > 0;
    }

    private Department findDepartmentOrThrow(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessException("DEPARTMENT_NOT_FOUND", "部门不存在: " + departmentId));
    }

    private DepartmentUserDto toDto(DepartmentUser rel, User user) {
        DepartmentUserDto dto = new DepartmentUserDto();
        dto.setId(rel.getId());
        dto.setDepartmentId(rel.getDepartmentId());
        dto.setUserId(rel.getUserId());
        dto.setCreatedBy(rel.getCreatedBy());
        dto.setCreatedAt(rel.getCreatedAt());

        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setRealName(user.getRealName());
        }

        return dto;
    }

    private UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setStatus(user.getStatus());
        dto.setCreatedBy(user.getCreatedBy());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
