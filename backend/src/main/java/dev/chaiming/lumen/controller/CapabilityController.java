package dev.chaiming.lumen.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.chaiming.lumen.common.PageResponse;
import dev.chaiming.lumen.domain.CapabilityStatus;
import dev.chaiming.lumen.dto.CapabilityDtos;
import dev.chaiming.lumen.service.CapabilityService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/capabilities")
public class CapabilityController {

    private final CapabilityService capabilityService;

    public CapabilityController(CapabilityService capabilityService) {
        this.capabilityService = capabilityService;
    }

    @GetMapping
    public PageResponse<CapabilityDtos.Response> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CapabilityStatus status,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return PageResponse.of(capabilityService.search(keyword, status, pageable));
    }

    @GetMapping("/{id}")
    public CapabilityDtos.Response get(@PathVariable Long id) {
        return capabilityService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CapabilityDtos.Response create(@Valid @RequestBody CapabilityDtos.CreateRequest request) {
        return capabilityService.create(request);
    }

    @PutMapping("/{id}")
    public CapabilityDtos.Response update(
            @PathVariable Long id,
            @Valid @RequestBody CapabilityDtos.UpdateRequest request
    ) {
        return capabilityService.update(id, request);
    }
}
