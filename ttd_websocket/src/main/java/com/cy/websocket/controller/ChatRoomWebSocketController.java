package com.cy.websocket.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.cy.generated.domain.*;
import com.cy.generated.mapper.TtdUserMapper;
import com.cy.generated.service.ChatMsgService;
import com.cy.generated.service.ParcelTaskService;
import com.cy.generated.service.SocketHistoryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Haechi
 * &#064;date  2022/5/23 16:27
 *&#064;Description  WebSocket操作类
 */
@ServerEndpoint("/websocketServer/{roomId}/{userId}")
@Component
@Slf4j
@Data
public class ChatRoomWebSocketController {

    private static SocketHistoryService socketHistoryService;

    private static TtdUserMapper ttdUserMapper;

    private static ParcelTaskService parcelTaskService;

    private static ChatMsgService chatMsgService;
    @Autowired
    public void setSocketHistoryService(SocketHistoryService socketHistoryService) {
        ChatRoomWebSocketController.socketHistoryService = socketHistoryService;
    }

    @Autowired
    public void setTtdUserMapper(TtdUserMapper ttdUserMapper) {
        ChatRoomWebSocketController.ttdUserMapper = ttdUserMapper;
    }

    @Autowired
    public void setParcelTaskService(ParcelTaskService parcelTaskService){
        ChatRoomWebSocketController.parcelTaskService = parcelTaskService;
    }

    @Autowired
    public void setChatMsgService(ChatMsgService chatMsgService){
        ChatRoomWebSocketController.chatMsgService = chatMsgService;
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static Map<String, ChatRoomWebSocketController> userMap = new ConcurrentHashMap<>();
    private static Map<String, Set<ChatRoomWebSocketController>> roomMap = new ConcurrentHashMap<>();
    private Session session;
    private String userId;
    private boolean isAuth;
    private int errorTimes;
    private ParcelTask task;

    @OnOpen
    public void onOpen(Session session , @PathParam("roomId") String roomId , @PathParam("userId") String userId) {
        logger.info("现在来连接的房间id："+roomId+"用户名："+userId);
        this.session = session;
        this.userId = userId;
        this.isAuth = false;
        this.errorTimes= 3;
        userMap.put(userId,this);
        if (!roomMap.containsKey(roomId)) {
            Set<ChatRoomWebSocketController> set = new HashSet<>();
            set.add(userMap.get(userId));
            roomMap.put(roomId,set);
        } else {
            roomMap.get(roomId).add(this);
        }

        task = parcelTaskService.getParcelTaskDetailsNoId(roomId);
        if(task==null){
            userMap.get(userId).session.getAsyncRemote().sendText(JSON.toJSONString(new SocketMsg(0,0L,"不明订单")));
            close(roomId);
            return;
        }

        if(!task.getReceiverId().toString().equals(userId)&&!task.getPublisherId().toString().equals(userId)){
            userMap.get(userId).session.getAsyncRemote().sendText(JSON.toJSONString(new SocketMsg(0,0L,"不是本单相关人员")));
            close(roomId);
        }
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId , @PathParam("roomId") String roomId) {

        System.out.println("断开的用户id："+userId);

        close(roomId);
    }

    private void close(String roomId){
        if (roomMap.containsKey(roomId)) {
            Set<ChatRoomWebSocketController> set = roomMap.get(roomId);
            set.removeIf(item -> item.userId.equals(userId));
        }

        try {
            this.session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userMap.remove(userId);
    }

    @OnMessage
    public void onMessage(String message , @PathParam("roomId") String roomId , @PathParam("userId") String userId) {
        sendMessageTo(message,roomId,userId);
    }

    /**
     * 群聊
     * @param message 消息
     * @param roomId 房间号
     * @param userId 发送人
     */
    public void sendMessageTo(String message , String roomId , String userId) {
        System.out.println(userId+"发送消息到: "+message);

        SocketMsg msg= JSON.parseObject(message,SocketMsg.class);

        if(msg==null||msg.getType()==null||msg.getMsg()==null){
            System.out.println("消息格式错误");
            // 防止尝试链接恶意发送消息
            if(!isAuth&&errorTimes--<=0){
                close(roomId);
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
                    SocketHistory history = socketHistoryService.getSocketHistoryById(roomId);

                    if (history != null) {
                        List<SocketMsg> socketMsgList = history.getSocketMsgList();

                        socketMsgList.add(new SocketMsg(4,0L,history.getStatus()));

                        session.getAsyncRemote().sendText(JSON.toJSONString(socketMsgList));

                        chatMsgService.clearChatMsg(roomId,Long.parseLong(userId));
                    }
                }else{
                    System.out.println("鉴权失败");
                    userMap.get(userId).session.getAsyncRemote().sendText(JSON.toJSONString(new SocketMsg(0,0L,"token无效")));
                    close(roomId);
                }
            }else{
                userMap.get(userId).session.getAsyncRemote().sendText(JSON.toJSONString(new SocketMsg(0,0L,"没有鉴权")));
                System.out.println("没有鉴权");
                close(roomId);
            }
            return;
        }else if(msg.getType()==0){
            System.out.println("心跳");
        }


        if(msg.getType()>0){
            if(!socketHistoryService.saveSocketHistory(roomId,msg)){
                userMap.get(userId).session.getAsyncRemote().sendText(JSON.toJSONString(new SocketMsg(0,0L,"发送失败")));
                return;
            }

            if(userId.equals(task.getPublisherId().toString())){
                if(userMap.containsKey(task.getReceiverId().toString())){
                    userMap.get(task.getReceiverId().toString()).session.getAsyncRemote().sendText(message);
                    chatMsgService.addChatMsg(new ChatMsg(null,roomId,"receiver",true,task.getReceiverId(),0,msg,new Date()));
                }else{
                    chatMsgService.addChatMsg(new ChatMsg(null,roomId,"receiver",false,task.getReceiverId(),1,msg,new Date()));
                }
                chatMsgService.addChatMsg(new ChatMsg(null,roomId,"publisher",true,task.getPublisherId(),0,msg,new Date()));
            }else{
                if(userMap.containsKey(task.getPublisherId().toString())){
                    userMap.get(task.getPublisherId().toString()).session.getAsyncRemote().sendText(message);
                    chatMsgService.addChatMsg(new ChatMsg(null,roomId,"publisher",true,task.getPublisherId(),0,msg,new Date()));
                }else{
                    chatMsgService.addChatMsg(new ChatMsg(null,roomId,"publisher",false,task.getPublisherId(),1,msg,new Date()));
                }
                chatMsgService.addChatMsg(new ChatMsg(null,roomId,"receiver",true,task.getReceiverId(),0,msg,new Date()));
            }
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

