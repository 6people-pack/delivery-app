package com.sparta.delivery.inquiry.event;

import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.user.domain.User;
import lombok.Getter;

@Getter
public class InquiryCreateEvent {
    private final User user;
    private final Inquiry inquiry;

    public InquiryCreateEvent(Inquiry inquiry, User user) {
        this.user = user;
        this.inquiry = inquiry;
    }
}
