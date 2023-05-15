package com.fourtytwo.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageDeleteReqDto {

    @NotNull
    private Long messageIdx;

}
