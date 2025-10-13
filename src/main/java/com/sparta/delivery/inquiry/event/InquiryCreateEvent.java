package com.sparta.delivery.inquiry.event;

import com.sparta.delivery.inquiry.domain.Inquiry;
import lombok.Getter;
import shop.matjalalzz.inquiry.entity.Inquiry;
import shop.matjalalzz.user.entity.User;

@Getter
public class InquiryCreateEvent {
    private final User user;
    private final String title;
    private final String Content;

    public InquiryCreateEvent(Inquiry inquiry) {
        this.user = inquiry.getUser();
        this.title = inquiry.getTitle();
        Content = inquiry.getContent();
    }
}
