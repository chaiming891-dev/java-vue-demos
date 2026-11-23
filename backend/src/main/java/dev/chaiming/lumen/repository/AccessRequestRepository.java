package dev.chaiming.lumen.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.chaiming.lumen.domain.AccessRequest;
import dev.chaiming.lumen.domain.AccessRequestStatus;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {

    long countByRequestNoStartingWith(String prefix);

    @Query(
            value = """
                    SELECT r FROM AccessRequest r
                    JOIN FETCH r.capability c
                    WHERE (:capabilityId IS NULL OR c.id = :capabilityId)
                      AND (:status IS NULL OR r.status = :status)
                    """,
            countQuery = """
                    SELECT COUNT(r) FROM AccessRequest r
                    WHERE (:capabilityId IS NULL OR r.capability.id = :capabilityId)
                      AND (:status IS NULL OR r.status = :status)
                    """
    )
    Page<AccessRequest> search(
            @Param("capabilityId") Long capabilityId,
            @Param("status") AccessRequestStatus status,
            Pageable pageable
    );
}
