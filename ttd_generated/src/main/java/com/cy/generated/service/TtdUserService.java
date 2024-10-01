package com.cy.generated.service;

import com.cy.generated.domain.ClientMsg;
import com.cy.generated.domain.StudentMsg;
import com.cy.generated.domain.TtdUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cy.generated.domain.UserMsg;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Haechi
 * @since 2023-10-14
 */
public interface TtdUserService extends IService<TtdUser> {

    /**
     * 根据openId获取用户
     * @param openId openId
     * @return 用户
     */
    TtdUser getUserByOpenId(String openId);

    /** 添加客户端信息
     * @param clientMsg 客户端信息
     */
    void addClientMsg(ClientMsg clientMsg);


    /**
     * 更新客户端信息
     * @param clientMsg 客户端信息
     * @param index 收货信息索引
     */
    void updateClientMsg(ClientMsg clientMsg, int index);

    /**
     * 获取用户收获信息
     * @return 用户收获信息
     */
    UserMsg getUserMsg();

    /**
     * 删除用户收货信息
     * @param index 收货信息索引
     */
    void deleteClientMsg(int index);

    /**
     * 设置默认收货信息
     * @param index 收货信息索引
     */
    void setDefaultMsg(int index);

    /**
     * 认证学生信息
     * @param studentMsg 学生信息
     * @return 认证结果
     */
    Map<String,Object> certificate(StudentMsg studentMsg);

    /**
     * 获取用户信息
     * @return 用户信息
     */
    StudentMsg getCertificate();

}
