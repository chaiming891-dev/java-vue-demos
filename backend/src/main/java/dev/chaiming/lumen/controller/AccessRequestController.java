package dev.chaiming.lumen.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.chaiming.lumen.common.PageResponse;
import dev.chaiming.lumen.domain.AccessRequestStatus;
import dev.chaiming.lumen.dto.AccessRequestDtos;
import dev.chaiming.lumen.service.AccessRequestService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/access-requests")
public class AccessRequestController {

    private final AccessRequestService accessRequestService;

    public AccessRequestController(AccessRequestService accessRequestService) {
        this.accessRequestService = accessRequestService;
    }

    @GetMapping
    public PageResponse<AccessRequestDtos.Response> list(
            @RequestParam(required = false) Long capabilityId,
            @RequestParam(required = false) AccessRequestStatus status,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return PageResponse.of(accessRequestService.search(capabilityId, status, pageable));
    }

    @GetMapping("/{id}")
    public AccessRequestDtos.Response get(@PathVariable Long id) {
        return accessRequestService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccessRequestDtos.Response create(@Valid @RequestBody AccessRequestDtos.CreateRequest request) {
        return accessRequestService.create(request);
    }

    @PostMapping("/{id}/review")
    public AccessRequestDtos.Response review(
            @PathVariable Long id,
            @Valid @RequestBody AccessRequestDtos.ReviewRequest request
    ) {
        return accessRequestService.review(id, request);
    }
}
