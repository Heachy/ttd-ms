package com.cy.generated.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cy.generated.domain.ClientMsg;
import com.cy.generated.domain.StudentMsg;
import com.cy.generated.domain.TtdUser;
import com.cy.generated.domain.UserMsg;
import com.cy.generated.mapper.TtdUserMapper;
import com.cy.generated.service.StudentMsgService;
import com.cy.generated.service.TtdUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Haechi
 * @since 2023-10-14
 */
@Service
public class TtdUserServiceImpl extends ServiceImpl<TtdUserMapper, TtdUser> implements TtdUserService {

    MongoTemplate mongoTemplate;

    TtdUserMapper ttdUserMapper;

    StudentMsgService studentMsgService;

    public TtdUserServiceImpl(MongoTemplate mongoTemplate, TtdUserMapper ttdUserMapper, StudentMsgService studentMsgService) {
        this.mongoTemplate = mongoTemplate;
        this.ttdUserMapper = ttdUserMapper;
        this.studentMsgService = studentMsgService;
    }

    @Override
    public TtdUser getUserByOpenId(String openId) {
        QueryWrapper<TtdUser> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("openid",openId);

        return this.getOne(queryWrapper);
    }

    @Override
    public void addClientMsg(ClientMsg clientMsg) {
        String userId= String.valueOf(ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString()));

        UserMsg userMsg = mongoTemplate.findById(userId, UserMsg.class, "user_msg");

        if(userMsg != null) {
            userMsg.getMsgList().add(clientMsg);

            mongoTemplate.save(userMsg,"user_msg");
        }else{
            List<ClientMsg> list =new ArrayList<>();

            list.add(clientMsg);

            userMsg = new UserMsg(userId,0,list);

            mongoTemplate.save(userMsg,"user_msg");
        }
    }

    @Override
    public void updateClientMsg(ClientMsg clientMsg, int index) {
        String userId= String.valueOf(ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString()));

        UserMsg userMsg = mongoTemplate.findById(userId, UserMsg.class, "user_msg");

        if(userMsg != null){
            userMsg.getMsgList().set(index,clientMsg);

            mongoTemplate.save(userMsg,"user_msg");
        }
    }

    @Override
    public UserMsg getUserMsg() {
        String userId= String.valueOf(ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString()));

        return mongoTemplate.findById(userId, UserMsg.class, "user_msg");
    }

    @Override
    public void deleteClientMsg(int index) {
        String userId= String.valueOf(ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString()));

        UserMsg userMsg = mongoTemplate.findById(userId, UserMsg.class, "user_msg");

        if(userMsg != null){
            userMsg.getMsgList().remove(index);

            mongoTemplate.save(userMsg,"user_msg");
        }
    }

    @Override
    public void setDefaultMsg(int index) {
        String userId= String.valueOf(ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString()));

        UserMsg userMsg = mongoTemplate.findById(userId, UserMsg.class, "user_msg");

        if(userMsg != null){

            userMsg.setDefaultIndex(index);

            mongoTemplate.save(userMsg,"user_msg");
        }
    }

    @Override
    public Map<String, Object> certificate(StudentMsg studentMsg) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        TtdUser ttdUser = ttdUserMapper.selectById(id);

        Map<String,Object> map = new HashMap<>(2);

        if(ttdUser.getMsgId() != 0) {
            map.put("isSuccess", false);

            map.put("msg", "已认证");

            return map;
        }

        QueryWrapper<StudentMsg> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("code",studentMsg.getCode())
                .eq("name",studentMsg.getName())
                .eq("major",studentMsg.getMajor());

        StudentMsg msg = studentMsgService.getOne(queryWrapper);

        if(msg != null) {
            QueryWrapper<TtdUser> userQueryWrapper = new QueryWrapper<>();

            userQueryWrapper.eq("msg_id",studentMsg.getCode());

            TtdUser user = ttdUserMapper.selectOne(userQueryWrapper);

            if(user != null) {
                map.put("isSuccess", false);

                map.put("msg", "认证失败，该信息已被认证");

                return map;
            }

            map.put("isSuccess", true);

            map.put("msg", "认证成功");

            ttdUser.setMsgId(msg.getId());

            ttdUserMapper.updateById(ttdUser);
        }else{
            map.put("isSuccess", false);

            map.put("msg", "信息有误");
        }

        return map;
    }

    @Override
    public StudentMsg getCertificate() {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

            TtdUser ttdUser = ttdUserMapper.selectById(id);

            return studentMsgService.getById(ttdUser.getMsgId());
    }
}
