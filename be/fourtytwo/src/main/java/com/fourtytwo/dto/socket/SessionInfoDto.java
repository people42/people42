package com.fourtytwo.dto.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfoDto {
    private String sessionId;
    private String type;
    private Long userIdx;
    private Double latitude;
    private Double longitude;
    private String nickname;
    private String message;
    private String emoji;
    private String status;

}
