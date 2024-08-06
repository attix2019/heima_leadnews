package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class SensitiveWordPageQueryDto extends PageRequestDto {

    private String keyword;
}
