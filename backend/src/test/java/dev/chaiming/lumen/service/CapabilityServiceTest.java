package dev.chaiming.lumen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import dev.chaiming.lumen.domain.Capability;
import dev.chaiming.lumen.domain.CapabilityStatus;
import dev.chaiming.lumen.dto.CapabilityDtos;
import dev.chaiming.lumen.exception.BusinessException;
import dev.chaiming.lumen.exception.ResourceNotFoundException;
import dev.chaiming.lumen.repository.CapabilityRepository;

@ExtendWith(MockitoExtension.class)
class CapabilityServiceTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    private CapabilityService capabilityService;

    @BeforeEach
    void setUp() {
        capabilityService = new CapabilityService(capabilityRepository);
    }

    @Test
    void createRejectsDuplicateCode() {
        var request = new CapabilityDtos.CreateRequest(
                "BOOK-SEARCH",
                "图书检索（演示）",
                "1.0.0",
                "演示账号-林秋",
                CapabilityStatus.DRAFT,
                "演示说明"
        );
        when(capabilityRepository.existsByCode("BOOK-SEARCH")).thenReturn(true);

        assertThatThrownBy(() -> capabilityService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("BOOK-SEARCH");
    }

    @Test
    void createPersistsTrimmedFields() {
        var request = new CapabilityDtos.CreateRequest(
                "ROOM-BOOKING",
                "  会议室预约（演示）  ",
                " 1.0.4 ",
                " 演示账号-林秋 ",
                CapabilityStatus.PUBLISHED,
                "  演示说明  "
        );
        when(capabilityRepository.existsByCode("ROOM-BOOKING")).thenReturn(false);
        when(capabilityRepository.save(any(Capability.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CapabilityDtos.Response response = capabilityService.create(request);

        ArgumentCaptor<Capability> captor = ArgumentCaptor.forClass(Capability.class);
        verify(capabilityRepository).save(captor.capture());
        Capability saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("会议室预约（演示）");
        assertThat(saved.getVersion()).isEqualTo("1.0.4");
        assertThat(saved.getOwner()).isEqualTo("演示账号-林秋");
        assertThat(saved.getDescription()).isEqualTo("演示说明");
        assertThat(response.status()).isEqualTo(CapabilityStatus.PUBLISHED);
    }

    @Test
    void getThrowsWhenMissing() {
        when(capabilityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> capabilityService.get(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void searchTreatsBlankKeywordAsUnfiltered() {
        when(capabilityRepository.search(null, null, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of()));

        var page = capabilityService.search("   ", null, PageRequest.of(0, 10));

        assertThat(page.getContent()).isEmpty();
        verify(capabilityRepository).search(null, null, PageRequest.of(0, 10));
    }
}
