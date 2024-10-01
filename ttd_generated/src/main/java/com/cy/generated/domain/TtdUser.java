package com.cy.generated.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Haechi
 * @since 2023-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TtdUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 微信用户id
     */
    private String openid;

    /**
     * 微信session
     */
    private String sessionKey;

    /**
     * 绑定学生信息
     */
    private Long msgId;

    /**
     * 货币
     */
    private Double money;

    /**
     * 信誉分
     */
    private Integer creditScore;

}
