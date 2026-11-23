package dev.chaiming.lumen.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.chaiming.lumen.domain.Capability;
import dev.chaiming.lumen.domain.CapabilityStatus;

public interface CapabilityRepository extends JpaRepository<Capability, Long> {

    boolean existsByCode(String code);

    @Query("""
            SELECT c FROM Capability c
            WHERE (:status IS NULL OR c.status = :status)
              AND (
                    :keyword IS NULL
                    OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.owner) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<Capability> search(
            @Param("keyword") String keyword,
            @Param("status") CapabilityStatus status,
            Pageable pageable
    );
}
