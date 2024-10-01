package com.cy.generated.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Haechi
 */
@Document(collection = "parcel_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelTask implements Serializable {

    @Id
    private String id;

    @Indexed
    private String from;

    @Indexed
    private int building;

    @Indexed
    int layer;

    @Indexed
    private String elseTo;

    @Indexed
    private Long publisherId;

    @Indexed
    private Long receiverId;

    @Indexed
    private String size;

    @Indexed
    private String status;

    @Indexed
    private double price;

    @Indexed
    private String type;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private String company;

    private IncidentalMsg incidentalMsg;

    private String userAvatar;

    private String riderAvatar;

}
