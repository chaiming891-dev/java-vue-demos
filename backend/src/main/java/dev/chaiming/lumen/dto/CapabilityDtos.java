package dev.chaiming.lumen.dto;

import java.time.Instant;

import dev.chaiming.lumen.domain.Capability;
import dev.chaiming.lumen.domain.CapabilityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class CapabilityDtos {

    private CapabilityDtos() {
    }

    public record CreateRequest(
            @NotBlank(message = "编码不能为空")
            @Size(max = 32, message = "编码最多 32 个字符")
            @Pattern(regexp = "^[A-Z][A-Z0-9-]{1,31}$", message = "编码需使用大写字母、数字或短横线，并以字母开头")
            String code,

            @NotBlank(message = "名称不能为空")
            @Size(max = 80, message = "名称最多 80 个字符")
            String name,

            @NotBlank(message = "版本不能为空")
            @Size(max = 20, message = "版本最多 20 个字符")
            String version,

            @NotBlank(message = "负责人不能为空")
            @Size(max = 40, message = "负责人最多 40 个字符")
            String owner,

            @NotNull(message = "状态不能为空")
            CapabilityStatus status,

            @Size(max = 500, message = "说明最多 500 个字符")
            String description
    ) {
    }

    public record UpdateRequest(
            @NotBlank(message = "名称不能为空")
            @Size(max = 80, message = "名称最多 80 个字符")
            String name,

            @NotBlank(message = "版本不能为空")
            @Size(max = 20, message = "版本最多 20 个字符")
            String version,

            @NotBlank(message = "负责人不能为空")
            @Size(max = 40, message = "负责人最多 40 个字符")
            String owner,

            @NotNull(message = "状态不能为空")
            CapabilityStatus status,

            @Size(max = 500, message = "说明最多 500 个字符")
            String description
    ) {
    }

    public record Response(
            Long id,
            String code,
            String name,
            String version,
            String owner,
            CapabilityStatus status,
            String description,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static Response from(Capability entity) {
            return new Response(
                    entity.getId(),
                    entity.getCode(),
                    entity.getName(),
                    entity.getVersion(),
                    entity.getOwner(),
                    entity.getStatus(),
                    entity.getDescription(),
                    entity.getCreatedAt(),
                    entity.getUpdatedAt()
            );
        }
    }
}
