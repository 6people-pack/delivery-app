package com.sparta.delivery.inquiry.event;

import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.user.domain.User;
import lombok.Getter;

@Getter
public class InquiryCreateEvent {
    private final User user;
    private final String title;
    private final String Content;

    public InquiryCreateEvent(Inquiry inquiry, User user) {
        this.user = user;
        this.title = inquiry.getTitle();
        Content = inquiry.getContent();
    }
}
