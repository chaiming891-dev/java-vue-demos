package dev.chaiming.lumen.service;

import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.chaiming.lumen.domain.AccessRequest;
import dev.chaiming.lumen.domain.AccessRequestStatus;
import dev.chaiming.lumen.domain.Capability;
import dev.chaiming.lumen.dto.AccessRequestDtos;
import dev.chaiming.lumen.exception.BusinessException;
import dev.chaiming.lumen.exception.ResourceNotFoundException;
import dev.chaiming.lumen.repository.AccessRequestRepository;

@Service
public class AccessRequestService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private final AccessRequestRepository accessRequestRepository;
    private final CapabilityService capabilityService;

    public AccessRequestService(
            AccessRequestRepository accessRequestRepository,
            CapabilityService capabilityService
    ) {
        this.accessRequestRepository = accessRequestRepository;
        this.capabilityService = capabilityService;
    }

    @Transactional(readOnly = true)
    public Page<AccessRequestDtos.Response> search(Long capabilityId, AccessRequestStatus status, Pageable pageable) {
        return accessRequestRepository.search(capabilityId, status, pageable).map(AccessRequestDtos.Response::from);
    }

    @Transactional(readOnly = true)
    public AccessRequestDtos.Response get(Long id) {
        AccessRequest entity = accessRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到开通申请，id=" + id));
        return AccessRequestDtos.Response.from(entity);
    }

    @Transactional
    public AccessRequestDtos.Response create(AccessRequestDtos.CreateRequest request) {
        Capability capability = capabilityService.require(request.capabilityId());
        AccessRequest entity = new AccessRequest();
        entity.setRequestNo(nextRequestNo());
        entity.setCapability(capability);
        entity.setApplicant(request.applicant().trim());
        entity.setTeam(request.team().trim());
        entity.setPurpose(request.purpose().trim());
        entity.setStatus(AccessRequestStatus.PENDING);
        return AccessRequestDtos.Response.from(accessRequestRepository.save(entity));
    }

    @Transactional
    public AccessRequestDtos.Response review(Long id, AccessRequestDtos.ReviewRequest request) {
        if (request.status() == AccessRequestStatus.PENDING) {
            throw new BusinessException("INVALID_REVIEW", "审核结果不能仍为待审核");
        }
        AccessRequest entity = accessRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到开通申请，id=" + id));
        if (entity.getStatus() != AccessRequestStatus.PENDING) {
            throw new BusinessException("ALREADY_REVIEWED", "该申请已审核，不能重复处理");
        }
        entity.setStatus(request.status());
        entity.setRemark(request.remark() == null || request.remark().isBlank() ? null : request.remark().trim());
        entity.setReviewedAt(Instant.now());
        return AccessRequestDtos.Response.from(accessRequestRepository.save(entity));
    }

    private String nextRequestNo() {
        int year = Year.now(ZONE).getValue();
        String prefix = "REQ-" + year + "-";
        long sequence = accessRequestRepository.countByRequestNoStartingWith(prefix) + 1;
        return "%s%04d".formatted(prefix, sequence);
    }
}
