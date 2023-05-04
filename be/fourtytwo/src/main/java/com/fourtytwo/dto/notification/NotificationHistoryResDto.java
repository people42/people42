package com.fourtytwo.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NotificationHistoryResDto {

    private String title;
    private String body;
    private String emoji;
    private LocalDateTime createdAt;

}
