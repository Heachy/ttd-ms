package com.cy.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cy.common.api.CommonResult;
import com.cy.dto.WxMsgDto;
import com.cy.generated.domain.StudentMsg;
import com.cy.generated.domain.TtdUser;
import com.cy.generated.service.StudentMsgService;
import com.cy.generated.service.TtdUserService;
import com.cy.service.WxService;
import com.cy.statistics.service.ScoreService;
import com.cy.statistics.service.StatisticsService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Haechi
 */
@Service
public class WxServiceImpl implements WxService {
    private static final String APPID="xxxx";
    private static final String APP_SECRET="xxxx";

    TtdUserService ttdUserService;

    StatisticsService statisticsService;

    StudentMsgService studentMsgService;

    MongoTemplate mongoTemplate;

    ScoreService scoreService;

    public WxServiceImpl(TtdUserService ttdUserService, StatisticsService statisticsService, StudentMsgService studentMsgService, MongoTemplate mongoTemplate, ScoreService scoreService) {
        this.ttdUserService = ttdUserService;
        this.statisticsService = statisticsService;
        this.studentMsgService = studentMsgService;
        this.mongoTemplate = mongoTemplate;
        this.scoreService = scoreService;
    }

    @Override
    public CommonResult<Map<String, Object>> login(String code) {
        //得到微信返回code临时票据
        //拿着code + 小程序id + 小程序密钥 请求微信接口服务
        //请求微信接口服务，返回两个值session_key和openId
        String url = "https://api.weixin.qq.com/sns/jscode2session" +
                "?appid=%s" +
                "&secret=%s" +
                "&js_code=%s" +
                "&grant_type=authorization_code";
        String tokenUrl = String.format(url, APPID, APP_SECRET, code);

        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject(tokenUrl, String.class);

        JSONObject jsonObject = JSONObject.parseObject(result);

        System.out.println(jsonObject);

        if (jsonObject == null || jsonObject.getString("session_key") == null) {
            return CommonResult.failed("登录失败");
        }
        String sessionKey = jsonObject.getString("session_key");

        String openid = SaSecureUtil.md5(jsonObject.getString("openid"));
        //添加微信用户的信息到数据库中 判断是否是第一使用微信授权登录

        TtdUser user = ttdUserService.getUserByOpenId(openid);

        if (user == null) {
            user = new TtdUser();
            user.setOpenid(openid);
            user.setSessionKey(SaSecureUtil.md5(sessionKey));
            user.setMsgId(0L);
            ttdUserService.save(user);

            scoreService.createScore(user.getId());

            statisticsService.createStatistics(user.getId());
        }
        //根据登录鉴权自行返回登录结果
        Map<String,Object> map=new HashMap<>();

        StpUtil.login(openid);

        map.put("token",StpUtil.getTokenInfo().tokenValue);

        map.put("userId",user.getId());

        if (user.getMsgId()!=0) {
            map.put("isCertificate",true);

            map.put("msg",studentMsgService.getById(user.getMsgId()));
        }else{
            map.put("isCertificate",false);
        }

        WxMsgDto wxMsg = mongoTemplate.findById(user.getId().toString(), WxMsgDto.class, "wx_msg");

        if(wxMsg!=null){
            map.put("wxMsg",wxMsg);
            map.put("userInfo",true);
        }else{
            map.put("userInfo",false);
        }

        return CommonResult.success(map);
    }

    @Override
    public boolean uploadMsg(String id,String name, String avatar) {

        WxMsgDto wxMsgDto = new WxMsgDto(id, name, avatar);

        mongoTemplate.save(wxMsgDto,"wx_msg");

        return true;
    }

    @Override
    public WxMsgDto getMsg(String id) {
        return mongoTemplate.findById(id, WxMsgDto.class, "wx_msg");
    }

    public CommonResult<Map<String, Object>> loginSe(String openid) {

        TtdUser user = ttdUserService.getUserByOpenId(openid);

        if (user == null) {
            user = new TtdUser();
            user.setOpenid(openid);
            user.setSessionKey(SaSecureUtil.md5("xxxx"));
            user.setMsgId(0L);
            ttdUserService.save(user);

            scoreService.createScore(user.getId());

            statisticsService.createStatistics(user.getId());
        }
        //根据登录鉴权自行返回登录结果
        Map<String,Object> map=new HashMap<>();

        StpUtil.login(openid);

        map.put("token",StpUtil.getTokenInfo().tokenValue);

        map.put("userId",user.getId());

        if (user.getMsgId()!=0) {
            map.put("isCertificate",true);

            map.put("msg",studentMsgService.getById(user.getMsgId()));
        }else{
            map.put("isCertificate",false);
        }

        WxMsgDto wxMsg = mongoTemplate.findById(user.getId().toString(), WxMsgDto.class, "wx_msg");

        if(wxMsg!=null){
            map.put("wxMsg",wxMsg);
            map.put("userInfo",true);
        }else{
            map.put("userInfo",false);
        }

        return CommonResult.success(map);
    }


    public void addMsg(){
        for(int i=0;i<100;i++){
            StudentMsg studentMsg=new StudentMsg();

            studentMsg.setCode(String.valueOf(100000000 + i));

            studentMsg.setName("张三"+i);

            studentMsg.setMajor("计算机类");

            studentMsgService.save(studentMsg);
        }
    }
}
