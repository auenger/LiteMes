package com.litemes.application.service;

import com.litemes.domain.entity.ExampleEntity;
import com.litemes.domain.event.BusinessException;
import com.litemes.domain.repository.ExampleRepository;
import com.litemes.web.dto.ExampleCreateDto;
import com.litemes.web.dto.ExampleDto;
import com.litemes.web.dto.ExampleUpdateDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for ExampleEntity CRUD operations.
 * Handles DTO <-> Entity conversion and business orchestration.
 */
@ApplicationScoped
public class ExampleService {

    private static final Logger LOG = Logger.getLogger(ExampleService.class);

    @Inject
    ExampleRepository exampleRepository;

    @Transactional
    public Long create(ExampleCreateDto dto) {
        LOG.debugf("Creating example entity: %s", dto.getName());
        ExampleEntity entity = new ExampleEntity(dto.getName(), dto.getDescription());
        exampleRepository.save(entity);
        LOG.infof("Created example entity with id: %d", entity.getId());
        return entity.getId();
    }

    public ExampleDto getById(Long id) {
        LOG.debugf("Getting example entity by id: %d", id);
        ExampleEntity entity = exampleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Example entity not found: " + id));
        return toDto(entity);
    }

    public List<ExampleDto> list() {
        LOG.debug("Listing all example entities");
        return exampleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, ExampleUpdateDto dto) {
        LOG.debugf("Updating example entity: %d", id);
        ExampleEntity entity = exampleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Example entity not found: " + id));
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        exampleRepository.update(entity);
        LOG.infof("Updated example entity: %d", id);
    }

    @Transactional
    public void delete(Long id) {
        LOG.debugf("Deleting example entity: %d", id);
        if (exampleRepository.findById(id).isEmpty()) {
            throw new BusinessException("NOT_FOUND", "Example entity not found: " + id);
        }
        exampleRepository.deleteById(id);
        LOG.infof("Deleted example entity: %d", id);
    }

    private ExampleDto toDto(ExampleEntity entity) {
        ExampleDto dto = new ExampleDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
