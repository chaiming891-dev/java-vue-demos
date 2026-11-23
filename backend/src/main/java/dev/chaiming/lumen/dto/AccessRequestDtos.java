package dev.chaiming.lumen.dto;

import java.time.Instant;

import dev.chaiming.lumen.domain.AccessRequest;
import dev.chaiming.lumen.domain.AccessRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class AccessRequestDtos {

    private AccessRequestDtos() {
    }

    public record CreateRequest(
            @NotNull(message = "请选择能力")
            Long capabilityId,

            @NotBlank(message = "申请人不能为空")
            @Size(max = 40, message = "申请人最多 40 个字符")
            String applicant,

            @NotBlank(message = "团队不能为空")
            @Size(max = 40, message = "团队最多 40 个字符")
            String team,

            @NotBlank(message = "申请说明不能为空")
            @Size(max = 300, message = "申请说明最多 300 个字符")
            String purpose
    ) {
    }

    public record ReviewRequest(
            @NotNull(message = "请选择审核结果")
            AccessRequestStatus status,

            @Size(max = 300, message = "审核备注最多 300 个字符")
            String remark
    ) {
    }

    public record Response(
            Long id,
            String requestNo,
            Long capabilityId,
            String capabilityCode,
            String capabilityName,
            String applicant,
            String team,
            String purpose,
            AccessRequestStatus status,
            String remark,
            Instant createdAt,
            Instant reviewedAt
    ) {
        public static Response from(AccessRequest entity) {
            return new Response(
                    entity.getId(),
                    entity.getRequestNo(),
                    entity.getCapability().getId(),
                    entity.getCapability().getCode(),
                    entity.getCapability().getName(),
                    entity.getApplicant(),
                    entity.getTeam(),
                    entity.getPurpose(),
                    entity.getStatus(),
                    entity.getRemark(),
                    entity.getCreatedAt(),
                    entity.getReviewedAt()
            );
        }
    }
}
