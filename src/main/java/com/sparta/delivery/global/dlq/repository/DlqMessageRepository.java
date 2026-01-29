package com.sparta.delivery.global.dlq.repository;

import com.sparta.delivery.global.dlq.domain.DlqMessage;
import com.sparta.delivery.global.dlq.domain.DlqStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DlqMessageRepository extends JpaRepository<DlqMessage, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select d
    from DlqMessage d
    where d.dlqStatus = :status
        and d.retryCount < :maxCount
        and d.nextRunTime is null or d.nextRunTime <= :nowTime
    order by coalesce(d.nextRunTime, d.createdAt) asc, d.createdAt asc
""")
    List<DlqMessage> pendingDlqLock(@Param("status") DlqStatus status,@Param("maxCount")int maxCount, @Param("nowTime") LocalDateTime nowTime, Pageable pageable);
    // 한 번도 시도 안 한 DLQ → 맨 앞에 위치 시키고 이미 시도했지만 백오프된 DLQ → nextRunTime이 이른 순서대로
    // coalesce(A, B)은 A가 null이면 B를 대신 써라 라는 뜻이며 끝에 d.createdAt asc를 한번 더 붙여 값이 같은 경우 이 방식으로 정렬
    // 만약 null이면 createAt를 사용하니 여러 값들이 같은 상태이기 때무


    // 엔티티 객체를 건드리지 않고 DB에 직접 쿼리를 날려 DB와 JPA 캐시의 상태 불일치로 인한 버그를 예방하는 역할
    // 상태를 PROCESSING으로 바꾸고 곧바로 같은 트랜잭션에서 dlqMessage.getDlqStatus()를 호출해도 최신 상태 반영
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update DlqMessage d
    set d.dlqStatus = :to
    where d.id in :ids and d.dlqStatus= :from
""")
    int changeStatus(@Param("ids")List<UUID>ids, @Param("from") DlqStatus from, @Param("to") DlqStatus to);

}
