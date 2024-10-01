package com.cy.generated.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.cy.common.utils.OssManagerUtil;
import com.cy.generated.domain.*;
import com.cy.generated.mapper.TtdUserMapper;
import com.cy.generated.service.ChatMsgService;
import com.cy.generated.service.ParcelTaskService;
import com.cy.generated.vo.ParcelTaskVO;
import com.cy.statistics.service.ScoreService;
import com.cy.statistics.service.StatisticsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Haechi
 */
@Service
public class ParcelTaskServiceImpl implements ParcelTaskService{
    MongoTemplate mongoTemplate;

    TtdUserMapper ttdUserMapper;

    ChatMsgService chatMsgService;

    StatisticsService statisticsService;

    ScoreService scoreService;


    private final RedisTemplate<String, Object> redisTemplate;

    public ParcelTaskServiceImpl(MongoTemplate mongoTemplate, TtdUserMapper ttdUserMapper, ChatMsgService chatMsgService, StatisticsService statisticsService, ScoreService scoreService, RedisTemplate<String, Object> redisTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.ttdUserMapper = ttdUserMapper;
        this.chatMsgService = chatMsgService;
        this.statisticsService = statisticsService;
        this.scoreService = scoreService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ParcelTask saveParcelTask(ParcelTask parcelTask) {
        ParcelTask saved = mongoTemplate.save(parcelTask, "parcel_task");

        List<ProcessMsg> msgList = new ArrayList<>();

        msgList.add(ProcessMsg.publish());

        TaskDetails taskDetails = new TaskDetails(saved.getId(),msgList);

        mongoTemplate.save(taskDetails,"task_details");

        return saved;
    }

    @Override
    public List<ParcelTaskVO> taskWaitList(Map<String, Object> map) {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("status").is("待接单"),Criteria.where("status").is("待接单2"));
        Object from= map.get("from");
        if(from!= null) {
            criteria.and("from").is(from.toString());
        }
        Object o = map.get("type");

        if(o!=null){
            criteria.and("type").is(o.toString());
        }

        Object building=  map.get("building");

        if(building!= null) {
            criteria.and("building").is(building);
        }

        Object layer= map.get("layer");

        if(layer!= null) {
            criteria.and("layer").is(layer);
        }

        Object size= map.get("size");

        if(size!= null) {
            criteria.and("size").is(size.toString());
        }

        Object elseTo= map.get("elseTo");

        if(elseTo!= null) {
            criteria.and("elseTo").is(elseTo.toString());
        }

        Object minP = map.get("minP");

        if(minP!=null){
            criteria.and("price").gte(Double.valueOf(minP.toString()));
        }

        Object maxP = map.get("maxP");

        if(maxP!=null){
            criteria.and("price").lte(Double.valueOf(maxP.toString()));
        }

        Integer page= (Integer) map.get("page");

        Integer pageSize= (Integer) map.get("pageSize");

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Query query = Query.query(criteria).with(pageable);

        Object priceOrder = map.get("priceOrder");

        if(priceOrder!=null){
            Sort sort = Sort.by(Sort.Direction.DESC,"price");
            if(Integer.parseInt(priceOrder.toString())==1){
                sort = Sort.by(Sort.Direction.ASC,"price");
            }
            query.with(sort);
        }

        Object timeOrder = map.get("timeOrder");

        if(timeOrder!=null){
            Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
            if(Integer.parseInt(timeOrder.toString())==1){
                sort = Sort.by(Sort.Direction.ASC,"createTime");
            }
            query.with(sort);
        }
        return mongoTemplate.find(query, ParcelTaskVO.class,"parcel_task");
    }

    @Override
    public boolean deleteParcelTask(String taskId) {
        ParcelTask parcelTask = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(taskId)), ParcelTask.class, "parcel_task");

        if (parcelTask != null) {

            String picture = parcelTask.getIncidentalMsg().getPicture();

            if (picture != null && !"".equals(picture)) {
                OssManagerUtil.deleteImage(picture);
            }
            Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());
            if (!id.equals(parcelTask.getPublisherId())) {
                return false;
            }

            mongoTemplate.remove(parcelTask, "parcel_task");

            Criteria criteria = new Criteria();

            criteria.and("taskId").is(taskId);

            criteria.and("receiverId").is(id);

            mongoTemplate.remove(new Query(criteria), "chat_msg");
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void updateParcelTask(ParcelTask parcelTask) {
        ParcelTask task = mongoTemplate.findById(parcelTask.getId(), ParcelTask.class, "parcel_task");

        if (task != null) {
            if(task.getIncidentalMsg().getPicture()!=null&&!task.getIncidentalMsg().getPicture().equals(parcelTask.getIncidentalMsg().getPicture())){
                OssManagerUtil.deleteImage(task.getIncidentalMsg().getPicture());
            }
        }
        mongoTemplate.save(parcelTask,"parcel_task");
    }

    @Override
    public List<ParcelTask> myPubList() {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        Query query= Query.query(Criteria.where("publisherId").is(id));

        return mongoTemplate.find(query, ParcelTask.class,"parcel_task");
    }

    @Override
    public List<ParcelTaskVO> myAcpList() {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());
        Query query= Query.query(Criteria.where("receiverId").is(id));

        return mongoTemplate.find(query, ParcelTaskVO.class,"parcel_task");
    }

    @Override
    public int acpParcelTask(String taskId,String avatar) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        if(parcelTask == null) {
            return 0;
        }else if(!"待接单".equals(parcelTask.getStatus())&&!"待接单2".equals(parcelTask.getStatus())&&!"待接单3".equals(parcelTask.getStatus())) {
            return 0;
        }else if(id.equals(parcelTask.getPublisherId())) {
            return 2;
        }

        Query query = Query.query(Criteria.where("_id").is(taskId));

        Update update = Update.update("receiverId",id).set("status","已接单").set("riderAvatar",avatar);

        mongoTemplate.updateFirst(query,update,"parcel_task");

        SocketMsg socketMsg;

        IncidentalMsg incidentalMsg = parcelTask.getIncidentalMsg();

        incidentalMsg.setTaskId(parcelTask.getId());

        socketMsg = new SocketMsg(3,parcelTask.getPublisherId(), incidentalMsg);

        List<SocketMsg> list=new ArrayList<>();

        list.add(socketMsg);

        SocketHistory socketHistory = new SocketHistory(parcelTask.getId(),list,"ing");

        mongoTemplate.save(socketHistory,"socket_history");

        TaskDetails taskDetails = mongoTemplate.findById(taskId, TaskDetails.class, "task_details");

        if (taskDetails != null) {
            List<ProcessMsg> msgList = taskDetails.getMsgList();

            msgList.add(ProcessMsg.accept());

            taskDetails.setMsgList(msgList);

            mongoTemplate.save(taskDetails,"task_details");
        }

        if(parcelTask.getBuilding()!=0){
            redisTemplate.opsForSet().add(parcelTask.getFrom()+"_"+parcelTask.getBuilding()+"_"+parcelTask.getLayer(),id);

            redisTemplate.opsForValue().set(id.toString(),1,5*60,java.util.concurrent.TimeUnit.SECONDS);

            redisTemplate.opsForSet().add(id+"Set",parcelTask.getFrom()+"_"+parcelTask.getBuilding()+"_"+parcelTask.getLayer());
        }

        ChatMsg msg1 = chatMsgService.getChatMsg(taskId, parcelTask.getPublisherId());

        if (msg1 == null) {
            chatMsgService.addChatMsg(new ChatMsg(null, taskId,"publisher",true, parcelTask.getPublisherId(), 0, socketMsg,new Date()));
        }

        ChatMsg msg2 = chatMsgService.getChatMsg(taskId, id);

        if (msg2 == null) {
            chatMsgService.addChatMsg(new ChatMsg(null, taskId,"receiver",false, id, 1, socketMsg,new Date()));
        }

        statisticsService.incStatistics(id,"totalOrders");

        return 1;
    }

    @Override
    public boolean confirmParcelTask(String taskId) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        if(parcelTask == null) {
            return false;
        }else if(!"已送达".equals(parcelTask.getStatus())||!id.equals(parcelTask.getPublisherId())) {
            return false;
        }

        Query query = Query.query(Criteria.where("_id").is(taskId));

        Update update = Update.update("status","待评价");

        mongoTemplate.updateFirst(query,update,"parcel_task");

        statisticsService.incStatistics(parcelTask.getReceiverId(),"completes");

        Update updateHistory = Update.update("status","end");

        mongoTemplate.updateFirst(query,updateHistory,"socket_history");

        TaskDetails taskDetails = mongoTemplate.findById(taskId, TaskDetails.class, "task_details");

        if (taskDetails != null) {
            List<ProcessMsg> msgList = taskDetails.getMsgList();

            for(int i=msgList.size()-1;i>=0;i--){
                if(msgList.get(i).getType()==StatusCode.COMPLETE.getType()){
                    ProcessMsg processMsg = msgList.get(i);

                    Map<String,Object> map = (Map<String, Object>) processMsg.getMessage();

                    map.put("isSure",true);

                    processMsg.setMessage(map);
                    break;
                }
            }

            msgList.add(ProcessMsg.sureGet());

            taskDetails.setMsgList(msgList);

            mongoTemplate.save(taskDetails,"task_details");

        }

        return true;
    }

    @Override
    public boolean acpCancelParcelTask(String taskId,String reason,String picture) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        System.out.println(parcelTask);
        if (parcelTask == null) {
            return false;
        } else if (!"已接单".equals(parcelTask.getStatus()) || !id.equals(parcelTask.getReceiverId())) {
            return false;
        }

        Query query = Query.query(Criteria.where("_id").is(taskId));

        Update update = Update.update("status","待接单2").set("receiverId",null);

        mongoTemplate.updateFirst(query,update,"parcel_task");

        Criteria criteria = new Criteria();

        criteria.and("taskId").is(taskId)
                .and("receiverId").is(id);

        mongoTemplate.remove(Query.query(criteria),"chat_msg");

        TaskDetails taskDetails = mongoTemplate.findById(taskId, TaskDetails.class, "task_details");

        if (taskDetails != null) {
            List<ProcessMsg> msgList = taskDetails.getMsgList();

            msgList.add(ProcessMsg.cancelAcp(reason,picture));

            taskDetails.setMsgList(msgList);

            mongoTemplate.save(taskDetails,"task_details");
        }

        return true;
    }

    @Override
    public ParcelTask getParcelTaskDetails(String taskId) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        if(parcelTask!=null){
            if(id.equals(parcelTask.getPublisherId())||id.equals(parcelTask.getReceiverId())) {
                return parcelTask;
            }
        }

        return null;
    }

    @Override
    public ParcelTask getParcelTaskDetailsNoId(String taskId) {
        return mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");
    }

    @Override
    public void getParcel(String taskId, String picture) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        if(parcelTask!=null){
            if(id.equals(parcelTask.getReceiverId())) {

                if(!"已接单".equals(parcelTask.getStatus())){
                    return;
                }

                Query query = Query.query(Criteria.where("_id").is(taskId));

                Update update = Update.update("status","拿到货");

                mongoTemplate.updateFirst(query,update,"parcel_task");

                TaskDetails taskDetails = mongoTemplate.findById(taskId, TaskDetails.class, "task_details");

                if (taskDetails != null) {
                    List<ProcessMsg> msgList = taskDetails.getMsgList();

                    msgList.add(ProcessMsg.getParcel(picture));

                    msgList.add(ProcessMsg.sendIng());

                    taskDetails.setMsgList(msgList);

                    mongoTemplate.save(taskDetails,"task_details");
                }
            }
        }
    }

    @Override
    public void sureParcelTask(String taskId) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        if(parcelTask!=null){
            if(id.equals(parcelTask.getPublisherId())) {
                if(!"拿到货".equals(parcelTask.getStatus())&&!"已送达".equals(parcelTask.getStatus())){
                    return;
                }

                Query query = Query.query(Criteria.where("_id").is(taskId));

                if("拿到货".equals(parcelTask.getStatus())){
                    Update update = Update.update("status","确认货物");

                    mongoTemplate.updateFirst(query,update,"parcel_task");

                }

                TaskDetails taskDetails = mongoTemplate.findById(taskId, TaskDetails.class, "task_details");

                if (taskDetails != null) {
                    List<ProcessMsg> msgList = taskDetails.getMsgList();

                    for(int i=msgList.size()-1;i>=0;i--){
                        if(msgList.get(i).getType()==StatusCode.GET_PARCEL.getType()){
                            ProcessMsg processMsg = msgList.get(i);

                            Map<String,Object> map = (Map<String, Object>) processMsg.getMessage();

                            map.put("isSure",true);

                            processMsg.setMessage(map);
                            break;
                        }
                    }

                    msgList.add(ProcessMsg.sureParcel());

                    taskDetails.setMsgList(msgList);

                    mongoTemplate.save(taskDetails,"task_details");
                }
            }
        }
    }

    @Override
    public void completeParcelTask(String taskId,String picture) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        if(parcelTask!=null){
            if(parcelTask.getReceiverId()!=null&&id.equals(parcelTask.getReceiverId())) {
                if(!"确认货物".equals(parcelTask.getStatus())&&!"拿到货".equals(parcelTask.getStatus())){
                    return;
                }

                Query query = Query.query(Criteria.where("_id").is(taskId));

                Update update = Update.update("status","已送达");

                mongoTemplate.updateFirst(query,update,"parcel_task");

                TaskDetails taskDetails = mongoTemplate.findById(taskId, TaskDetails.class, "task_details");

                if (taskDetails != null) {
                    List<ProcessMsg> msgList = taskDetails.getMsgList();

                    msgList.add(ProcessMsg.complete(picture));

                    taskDetails.setMsgList(msgList);

                    mongoTemplate.save(taskDetails,"task_details");
                }
                int score = (int) scoreService.getScore(id);

                if(score<5){
                    scoreService.incScore(id, (byte) (score+1));
                }else{
                    scoreService.incScore(id, (byte) 5);
                }
            }
        }
    }


    @Override
    public boolean finishParcelTask(String taskId,Long id) {

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        if(parcelTask!=null){
            if(parcelTask.getReceiverId()!=null&&id.equals(parcelTask.getPublisherId())) {
                if(!"待评价".equals(parcelTask.getStatus())){
                    return false;
                }

                Query query = Query.query(Criteria.where("_id").is(taskId));

                Update update = Update.update("status","已完成");

                mongoTemplate.updateFirst(query,update,"parcel_task");
                return true;
            }
        }
        return false;
    }

    @Override
    public void testXX() {
        TaskDetails taskDetails = mongoTemplate.findById("65503bad08509e42b059ed92", TaskDetails.class, "task_details");

        if (taskDetails != null) {
            List<ProcessMsg> msgList = taskDetails.getMsgList();

            for (int i = msgList.size() - 1; i >= 0; i--) {
                if (msgList.get(i).getType() == StatusCode.GET_PARCEL.getType()) {
                    ProcessMsg processMsg = msgList.get(i);

                    System.out.println(processMsg);

                    Map<String, Object> map = (Map<String, Object>) processMsg.getMessage();

                    map.put("isSure", true);

                    processMsg.setMessage(map);

                    System.out.println(processMsg);

                    System.out.println(msgList);
                    break;
                }
            }
        }
    }

    @Override
    public Map<String,Object> getProcessDetails(String taskId) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        Map<String,Object> map=new HashMap<>();

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        if(parcelTask!=null){
            if(id.equals(parcelTask.getPublisherId())) {
                map.put("role", "publisher");
            }else if(parcelTask.getReceiverId()!=null&&id.equals(parcelTask.getReceiverId())) {
                map.put("role", "receiver");
            }else{
                return null;
            }
            map.put("taskId",parcelTask.getId());

            map.put("status",parcelTask.getStatus());

            TaskDetails taskDetails = mongoTemplate.findById(taskId, TaskDetails.class, "task_details");

            if (taskDetails != null) {
                map.put("msgList",taskDetails.getMsgList());
            }
        }
        return map;
    }
}
