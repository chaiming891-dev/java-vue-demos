package dev.chaiming.lumen.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.chaiming.lumen.domain.Capability;
import dev.chaiming.lumen.domain.CapabilityStatus;
import dev.chaiming.lumen.dto.CapabilityDtos;
import dev.chaiming.lumen.exception.BusinessException;
import dev.chaiming.lumen.exception.ResourceNotFoundException;
import dev.chaiming.lumen.repository.CapabilityRepository;

@Service
public class CapabilityService {

    private final CapabilityRepository capabilityRepository;

    public CapabilityService(CapabilityRepository capabilityRepository) {
        this.capabilityRepository = capabilityRepository;
    }

    @Transactional(readOnly = true)
    public Page<CapabilityDtos.Response> search(String keyword, CapabilityStatus status, Pageable pageable) {
        String normalized = blankToNull(keyword);
        return capabilityRepository.search(normalized, status, pageable).map(CapabilityDtos.Response::from);
    }

    @Transactional(readOnly = true)
    public CapabilityDtos.Response get(Long id) {
        return CapabilityDtos.Response.from(require(id));
    }

    @Transactional
    public CapabilityDtos.Response create(CapabilityDtos.CreateRequest request) {
        if (capabilityRepository.existsByCode(request.code())) {
            throw new BusinessException("DUPLICATE_CODE", "能力编码已存在：" + request.code());
        }
        Capability entity = new Capability();
        entity.setCode(request.code());
        apply(entity, request.name(), request.version(), request.owner(), request.status(), request.description());
        return CapabilityDtos.Response.from(capabilityRepository.save(entity));
    }

    @Transactional
    public CapabilityDtos.Response update(Long id, CapabilityDtos.UpdateRequest request) {
        Capability entity = require(id);
        apply(entity, request.name(), request.version(), request.owner(), request.status(), request.description());
        return CapabilityDtos.Response.from(capabilityRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Capability require(Long id) {
        return capabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到能力，id=" + id));
    }

    private static void apply(
            Capability entity,
            String name,
            String version,
            String owner,
            CapabilityStatus status,
            String description
    ) {
        entity.setName(name.trim());
        entity.setVersion(version.trim());
        entity.setOwner(owner.trim());
        entity.setStatus(status);
        entity.setDescription(description == null || description.isBlank() ? null : description.trim());
    }

    private static String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
