package com.heima.model.wemedia.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ChannelDto {
//    private Date createdTime;

    private String description;

    private Integer id;

//    private Boolean isDefault;

    private String name;

    private Integer ord;

    private Boolean status;
}
