package dev.chaiming.lumen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.chaiming.lumen.domain.AccessRequest;
import dev.chaiming.lumen.domain.AccessRequestStatus;
import dev.chaiming.lumen.domain.Capability;
import dev.chaiming.lumen.dto.AccessRequestDtos;
import dev.chaiming.lumen.exception.BusinessException;
import dev.chaiming.lumen.repository.AccessRequestRepository;

@ExtendWith(MockitoExtension.class)
class AccessRequestServiceTest {

    @Mock
    private AccessRequestRepository accessRequestRepository;

    @Mock
    private CapabilityService capabilityService;

    private AccessRequestService accessRequestService;

    @BeforeEach
    void setUp() {
        accessRequestService = new AccessRequestService(accessRequestRepository, capabilityService);
    }

    @Test
    void createAssignsYearlyRequestNumber() {
        Capability capability = new Capability();
        capability.setCode("BOOK-SEARCH");
        capability.setName("图书检索（演示）");
        when(capabilityService.require(1L)).thenReturn(capability);
        when(accessRequestRepository.countByRequestNoStartingWith(startsWith("REQ-"))).thenReturn(2L);
        when(accessRequestRepository.save(any(AccessRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new AccessRequestDtos.CreateRequest(1L, "演示账号-陈可", "演示组-内容", "编写列表页演示数据。");
        AccessRequestDtos.Response response = accessRequestService.create(request);

        int year = Year.now(ZoneId.of("Asia/Shanghai")).getValue();
        assertThat(response.requestNo()).isEqualTo("REQ-%d-0003".formatted(year));
        assertThat(response.status()).isEqualTo(AccessRequestStatus.PENDING);
        assertThat(response.applicant()).isEqualTo("演示账号-陈可");
    }

    @Test
    void reviewRejectsNonPendingTarget() {
        AccessRequest entity = new AccessRequest();
        entity.setStatus(AccessRequestStatus.APPROVED);
        when(accessRequestRepository.findById(8L)).thenReturn(Optional.of(entity));

        var request = new AccessRequestDtos.ReviewRequest(AccessRequestStatus.REJECTED, "演示驳回");

        assertThatThrownBy(() -> accessRequestService.review(8L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已审核");
    }

    @Test
    void reviewRejectsPendingAsResult() {
        var request = new AccessRequestDtos.ReviewRequest(AccessRequestStatus.PENDING, null);

        assertThatThrownBy(() -> accessRequestService.review(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待审核");
    }
}
