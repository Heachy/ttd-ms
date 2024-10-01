package com.cy.generated.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcelTaskVO {
    @Id
    private String id;
    private String from;
    private int building;
    private int layer;
    private Long publisherId;
    private String elseTo;
    private String size;
    private String status;
    private String remark;
    private double price;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    private String company;
    private String userAvatar;
}
