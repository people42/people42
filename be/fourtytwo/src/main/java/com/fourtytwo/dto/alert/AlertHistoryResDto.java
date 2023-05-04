package com.fourtytwo.dto.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertHistoryResDto {

    private String title;
    private String body;
    private String emoji;
    private LocalDateTime createdAt;

}
