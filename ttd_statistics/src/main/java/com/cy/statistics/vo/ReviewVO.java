package com.cy.statistics.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Haechi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVO {
    private Byte stars;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date time;
}
