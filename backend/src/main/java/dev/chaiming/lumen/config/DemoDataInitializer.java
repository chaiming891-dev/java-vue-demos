package dev.chaiming.lumen.config;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dev.chaiming.lumen.domain.AccessRequest;
import dev.chaiming.lumen.domain.AccessRequestStatus;
import dev.chaiming.lumen.domain.Capability;
import dev.chaiming.lumen.domain.CapabilityStatus;
import dev.chaiming.lumen.repository.AccessRequestRepository;
import dev.chaiming.lumen.repository.CapabilityRepository;

/**
 * Loads fictional seed data so the demo UI is not empty on first run.
 * None of these records represent a real organization or production system.
 */
@Component
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final CapabilityRepository capabilityRepository;
    private final AccessRequestRepository accessRequestRepository;

    public DemoDataInitializer(
            CapabilityRepository capabilityRepository,
            AccessRequestRepository accessRequestRepository
    ) {
        this.capabilityRepository = capabilityRepository;
        this.accessRequestRepository = accessRequestRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (capabilityRepository.count() > 0) {
            return;
        }

        Capability books = capability(
                "BOOK-SEARCH",
                "图书检索（演示）",
                "1.2.0",
                "演示账号-林秋",
                CapabilityStatus.PUBLISHED,
                "虚构的馆藏关键词检索能力，仅用于作品集演示。"
        );
        Capability reports = capability(
                "REPORT-EXPORT",
                "报表导出（演示）",
                "2.0.1",
                "演示账号-周南",
                CapabilityStatus.PUBLISHED,
                "将演示看板导出为 CSV / PDF，不连接真实业务库。"
        );
        Capability transcode = capability(
                "FILE-TRANSCODE",
                "文件转码（演示）",
                "0.9.0",
                "演示账号-沈予",
                CapabilityStatus.DRAFT,
                "演示用的文档格式转换占位能力，尚未发布。"
        );
        Capability rooms = capability(
                "ROOM-BOOKING",
                "会议室预约（演示）",
                "1.0.4",
                "演示账号-林秋",
                CapabilityStatus.PUBLISHED,
                "查询与锁定虚构会议室时段的演示接口。"
        );
        Capability notice = capability(
                "NOTICE-BOARD",
                "公告发布（演示）",
                "3.1.0",
                "演示账号-顾晚",
                CapabilityStatus.DEPRECATED,
                "旧版公告通道，已标记为废弃，仅保留演示查询。"
        );
        Capability thumbs = capability(
                "IMAGE-THUMB",
                "缩略图生成（演示）",
                "1.5.2",
                "演示账号-沈予",
                CapabilityStatus.PUBLISHED,
                "按指定尺寸生成演示图片缩略图。"
        );
        capabilityRepository.saveAll(List.of(books, reports, transcode, rooms, notice, thumbs));

        accessRequestRepository.saveAll(List.of(
                request("REQ-2026-0001", books, "演示账号-陈可", "演示组-内容", "检索演示书目，便于编写前端列表。", AccessRequestStatus.APPROVED, "演示通过", daysAgo(12)),
                request("REQ-2026-0002", reports, "演示账号-韩舟", "演示组-平台", "导出周报样例，用于核对分页字段。", AccessRequestStatus.PENDING, null, null),
                request("REQ-2026-0003", rooms, "演示账号-陈可", "演示组-内容", "预约演示会议室，走一遍审核流。", AccessRequestStatus.APPROVED, "演示通过", daysAgo(4)),
                request("REQ-2026-0004", thumbs, "演示账号-乔白", "演示组-设计", "生成封面缩略图，仅使用占位图。", AccessRequestStatus.PENDING, null, null),
                request("REQ-2026-0005", notice, "演示账号-韩舟", "演示组-平台", "查看已废弃公告通道的只读权限。", AccessRequestStatus.REJECTED, "该能力已废弃，演示驳回。", daysAgo(2)),
                request("REQ-2026-0006", reports, "演示账号-乔白", "演示组-设计", "导出设计走查清单。", AccessRequestStatus.APPROVED, "演示通过", daysAgo(1)),
                request("REQ-2026-0007", transcode, "演示账号-陈可", "演示组-内容", "将样例文稿转为 PDF。", AccessRequestStatus.PENDING, null, null),
                request("REQ-2026-0008", books, "演示账号-顾晚", "演示组-平台", "联调检索高亮效果。", AccessRequestStatus.PENDING, null, null)
        ));

        log.info("已写入演示种子数据：{} 条能力，{} 条开通申请（均为虚构数据）。",
                capabilityRepository.count(), accessRequestRepository.count());
    }

    private static Capability capability(
            String code,
            String name,
            String version,
            String owner,
            CapabilityStatus status,
            String description
    ) {
        Capability entity = new Capability();
        entity.setCode(code);
        entity.setName(name);
        entity.setVersion(version);
        entity.setOwner(owner);
        entity.setStatus(status);
        entity.setDescription(description);
        return entity;
    }

    private static AccessRequest request(
            String requestNo,
            Capability capability,
            String applicant,
            String team,
            String purpose,
            AccessRequestStatus status,
            String remark,
            Instant reviewedAt
    ) {
        AccessRequest entity = new AccessRequest();
        entity.setRequestNo(requestNo);
        entity.setCapability(capability);
        entity.setApplicant(applicant);
        entity.setTeam(team);
        entity.setPurpose(purpose);
        entity.setStatus(status);
        entity.setRemark(remark);
        entity.setReviewedAt(reviewedAt);
        return entity;
    }

    private static Instant daysAgo(int days) {
        return Instant.now().minusSeconds(days * 24L * 3600L);
    }
}
