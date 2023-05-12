package com.fourtytwo.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ReportReqDto {

    @NotNull
    Long messageIdx;

    @NotBlank
    String content;

}
