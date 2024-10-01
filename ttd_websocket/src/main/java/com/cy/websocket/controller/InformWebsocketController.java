package com.cy.websocket.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.cy.generated.domain.SocketMsg;
import com.cy.generated.mapper.TtdUserMapper;
import com.cy.generated.service.SocketHistoryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Haechi
 */
@ServerEndpoint("/informWsServer/{userId}")
@Component
@Slf4j
@Data
public class InformWebsocketController {
    public static RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        InformWebsocketController.redisTemplate = redisTemplate;
    }
    private static TtdUserMapper ttdUserMapper;
    @Autowired
    public void setTtdUserMapper(TtdUserMapper ttdUserMapper) {
        InformWebsocketController.ttdUserMapper = ttdUserMapper;
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static Map<String, InformWebsocketController> userMap = new ConcurrentHashMap<>();
    private Session session;
    private String userId;
    private boolean isAuth;
    private int errorTimes;
    @OnOpen
    public void onOpen(Session session ,@PathParam("userId") String userId) {

        logger.info("现在来连接用户名："+userId);

        if (redisTemplate.opsForValue().get(userId) == null) {
            logger.info("该用户此前无接单记录");

            session.getAsyncRemote().sendText(JSON.toJSONString(new SocketMsg(0,0L,"该用户此前无接单记录")));

            try {
                session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        this.session = session;
        this.userId = userId;
        this.isAuth = false;
        this.errorTimes= 3;
        userMap.put(userId,this);
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        close();
    }

    private void close(){
        if(userId!=null){
            userMap.remove(userId);
            try {
                this.session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void close(String id){
        InformWebsocketController removed = userMap.remove(id);
        try {
            if(removed!=null){
                removed.session.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(String message  , @PathParam("userId") String userId) {
        sendMessageTo(message,userId);
    }

    /**
     * 链接
     * @param message 消息
     * @param userId 发送人
     */
    public void sendMessageTo(String message, String userId) {
        SocketMsg msg= JSON.parseObject(message,SocketMsg.class);

        if(msg==null||msg.getType()==null||msg.getMsg()==null){
            System.out.println("消息格式错误");
            // 防止尝试链接恶意发送消息
            if(!isAuth&&errorTimes--<=0){
                close();
            }else{
                userMap.get(userId).session.getAsyncRemote().sendText(JSON.toJSONString(new SocketMsg(0,0L,"消息格式错误")));
            }
            return;
        }

        msg.setSenderId(Long.parseLong(userId));

        if(!isAuth){
            if(msg.getType()==0){
                // 检测token
                String loginId = StpUtil.getLoginIdByToken((String) msg.getMsg()).toString();

                Long id=ttdUserMapper.getUserIdByOpenId(loginId);

                if(loginId!=null&&id.toString().equals(userId)){
                    System.out.println("鉴权成功");
                    isAuth=true;
                }else{
                    System.out.println("鉴权失败");
                    userMap.get(userId).session.getAsyncRemote().sendText(JSON.toJSONString(new SocketMsg(0,0L,"token无效")));
                    close();
                }
            }else{
                userMap.get(userId).session.getAsyncRemote().sendText(JSON.toJSONString(new SocketMsg(0,0L,"没有鉴权")));
                System.out.println("没有鉴权");
                close();
            }
        }else if (msg.getType()==0){
            System.out.println("心跳");
        }
    }

    /**
     * 私聊
     * @param message 消息
     * @param toUserId 接收人
     */
    public static void sendUserTo(String message , String toUserId) {
        if (userMap.containsKey(toUserId)) {
            userMap.get(toUserId).session.getAsyncRemote().sendText(message);
        }
    }
}
