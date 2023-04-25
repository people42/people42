package com.fourtytwo.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessageResDto {
    private Long messageIdx;
    private String content;
    private LocalDateTime time;
    private String emotion;
}
