package com.sparta.delivery.inquiry.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import com.sparta.delivery.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_inquiry")
public class Inquiry extends BaseEntity {

    @Id
    @Column(name = "inquiry_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;


    private Long userId;


    @Builder
    public Inquiry( String title, String content, Long user) {
        this.title = title;
        this.content = content;
        this.userId = user;
    }


}
