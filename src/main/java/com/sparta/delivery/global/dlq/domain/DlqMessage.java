package com.sparta.delivery.global.dlq.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DlqMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String cause;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DlqStatus dlqStatus;

    @Column(nullable = false)
    private UUID inquiryId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime nextRunTime;

    private int retryCount = 0;


    public DlqMessage(DlqStatus dlqStatus, String cause, UUID inquiryId) {
        this.dlqStatus = dlqStatus;
        this.cause = cause;
        this.inquiryId = inquiryId;
    }

    public static DlqMessage createDlq(DlqStatus dlqStatus, String cause, UUID inquiryId) {
        DlqMessage dlqMessage = new DlqMessage(dlqStatus, cause, inquiryId);
        dlqMessage.nextRunTime = null;
        return dlqMessage;
    }

    public void updateDlqStatus(DlqStatus dlqStatus) {
        this.dlqStatus = dlqStatus;
    }
    public void updateNextRunTime(LocalDateTime nextRunTime) {
        this.nextRunTime = nextRunTime;
    }
    public void retryUpCount(){
        retryCount++;
    }


}
