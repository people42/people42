package com.fourtytwo.dto.socket;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class MessageDto {

    private MethodType method;
    private Map<String, Object> data;

    public MessageDto(MethodType method) {
        this.method = method;
    }
}
