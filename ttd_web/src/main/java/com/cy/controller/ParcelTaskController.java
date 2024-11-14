package com.cy.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.cy.common.api.CommonResult;
import com.cy.common.utils.OssManagerUtil;
import com.cy.dto.WxMsgDto;
import com.cy.generated.domain.IncidentalMsg;
import com.cy.generated.domain.ParcelTask;
import com.cy.generated.mapper.TtdUserMapper;
import com.cy.generated.service.ParcelTaskService;
import com.cy.generated.service.SocketHistoryService;
import com.cy.generated.vo.ParcelTaskVO;
import com.cy.service.WxService;
import com.cy.statistics.dao.Review;
import com.cy.statistics.service.ReviewService;
import com.cy.statistics.service.ScoreService;
import com.cy.statistics.vo.ReviewVO;
import com.cy.ttd.service.ReceiverService;
import com.cy.websocket.service.InformSocketService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Haechi
 */
@RestController
@RequestMapping("/pt")
public class ParcelTaskController {
    TtdUserMapper ttdUserMapper;

    ParcelTaskService parcelTaskService;

    SocketHistoryService socketHistoryService;

    InformSocketService informSocketService;

    ReceiverService receiverService;

    ReviewService reviewService;

    ScoreService scoreService;

    WxService wxService;

    MongoTemplate mongoTemplate;

    public ParcelTaskController(ParcelTaskService parcelTaskService, TtdUserMapper ttdUserMapper,
                                SocketHistoryService socketHistoryService, InformSocketService informSocketService,
                                ReceiverService receiverService, ReviewService reviewService,
                                WxService wxService,ScoreService scoreService,MongoTemplate mongoTemplate) {
        this.parcelTaskService = parcelTaskService;
        this.ttdUserMapper = ttdUserMapper;
        this.socketHistoryService = socketHistoryService;
        this.informSocketService = informSocketService;
        this.receiverService = receiverService;
        this.reviewService = reviewService;
        this.wxService = wxService;
        this.scoreService = scoreService;
        this.mongoTemplate =mongoTemplate;
    }

    @PostMapping("/publish")
    public CommonResult<String> publishParcelTask(ParcelTask parcelTask, IncidentalMsg incidentalMsg, MultipartFile file) {

        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        parcelTask.setPublisherId(id);

        parcelTask.setStatus("待接单");

        if(file != null) {
            String url = OssManagerUtil.getUrl(file);

            incidentalMsg.setPicture(url);
        }
        parcelTask.setIncidentalMsg(incidentalMsg);

        parcelTask.setCreateTime(new Date());

        parcelTaskService.saveParcelTask(parcelTask);

        return CommonResult.success("发布成功");
    }

    @PostMapping("/publish/json")
    public CommonResult<String> publishParcelTaskJson(@RequestBody ParcelTask parcelTask) {

        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        WxMsgDto msg = wxService.getMsg(id.toString());

        parcelTask.setUserAvatar(msg.getAvatar());

        parcelTask.setPublisherId(id);

        parcelTask.setStatus("待接单");

        parcelTask.setCreateTime(new Date());

        parcelTask.getIncidentalMsg().setType(parcelTask.getType());

        ParcelTask saved = parcelTaskService.saveParcelTask(parcelTask);

        if(parcelTask.getBuilding()!=0){
            informSocketService.sendPtInform(saved.getId(),saved.getFrom(),saved.getBuilding(),saved.getLayer());
        }

        return CommonResult.success("发布成功");
    }

    @PostMapping("/list")
    public CommonResult<List<ParcelTaskVO>> listParcelTask(@RequestBody Map<String, Object> map) {
        return CommonResult.success(parcelTaskService.taskWaitList(map));
    }

    @GetMapping("/list/pub")
    public CommonResult<List<ParcelTask>> listMyPubParcelTask() {
        return CommonResult.success(parcelTaskService.myPubList());
    }

    @GetMapping("/list/acp")
    public CommonResult<List<ParcelTaskVO>> listMyAcpParcelTask() {
        return CommonResult.success(parcelTaskService.myAcpList());
    }

    @PostMapping("/acp")
    public CommonResult<String> acpParcelTask(@RequestBody Map<String,Object> map) {

        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        WxMsgDto wxMsgDto = wxService.getMsg(id.toString());
        String taskId = (String) map.get("taskId");
        int result = parcelTaskService.acpParcelTask(taskId,wxMsgDto.getAvatar());
        if(result==1){
            // 利用消息队列5min后删除此信息
            receiverService.putMsg(ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString()).toString());

            return CommonResult.success("接单成功");
        }else if(result==0){
            return CommonResult.failed("该订单已被接走或不存在");
        }else{
            return CommonResult.failed("您无法承接自己发布的订单");
        }
    }

    @PostMapping("/acp/cancel")
    public CommonResult<String> acpCancelParcelTask(@RequestBody Map<String,Object> map) {

        String taskId = (String) map.get("taskId");

        if(taskId == null || "".equals(taskId)) {
            return CommonResult.failed("任务id不能为空");
        }

        String reason = (String) map.get("reason");

        if(reason == null || "".equals(reason)) {
            return CommonResult.failed("没有理由");
        }
        String picture = (String) map.get("picture");

        if(parcelTaskService.acpCancelParcelTask(taskId,reason,picture)){
            return CommonResult.success("取消成功");
        }else{
            return CommonResult.failed("取消失败");
        }
    }

    @PostMapping("/delete")
    public CommonResult<String> deleteParcelTask(@RequestBody Map<String,Object> map) {

        String taskId = (String) map.get("taskId");

        if(parcelTaskService.deleteParcelTask(taskId)){
            return CommonResult.success("删除成功");
        }else{
            return CommonResult.failed("删除失败");
        }
    }

    @PostMapping("/update")
    public CommonResult<String> updateParcelTask(ParcelTask parcelTask, IncidentalMsg incidentalMsg, MultipartFile file) {

        if(file != null) {
            String url = OssManagerUtil.getUrl(file);

            if (incidentalMsg.getPicture()!=null){
                OssManagerUtil.deleteImage(incidentalMsg.getPicture());
            }

            incidentalMsg.setPicture(url);
        }

        parcelTask.setIncidentalMsg(incidentalMsg);

        parcelTaskService.updateParcelTask(parcelTask);

        return CommonResult.success("修改成功");
    }

    @PostMapping("/update/json")
    public CommonResult<String> updateParcelTaskJson(@RequestBody ParcelTask parcelTask) {

        parcelTaskService.updateParcelTask(parcelTask);

        return CommonResult.success("修改成功");
    }

    @PostMapping("/progress/picture")
    public CommonResult<String> uploadProgressPicture(String taskId,MultipartFile file) {

        String url = OssManagerUtil.getUrl(file);

        socketHistoryService.sendPicture(taskId,url);

        return CommonResult.success("上传成功");
    }

    @PostMapping("/progress/picture/json")
    public CommonResult<String> uploadProgressPictureJson(@RequestBody Map<String,Object> map) {

        String taskId = (String) map.get("taskId");

        String url = (String) map.get("picture");

        parcelTaskService.getParcel(taskId,url);

        return CommonResult.success("上传成功");
    }

    @PostMapping("/confirm")
    public CommonResult<String> confirmParcelTask(@RequestBody Map<String,Object> map) {

        String taskId = (String) map.get("taskId");

        if(parcelTaskService.confirmParcelTask(taskId)){
            return CommonResult.success("确认成功");
        }else{
            return CommonResult.failed("确认失败");
        }
    }

    @GetMapping("/process/detail")
    public CommonResult<Map<String,Object>> processDetail(String taskId) {

        if(taskId == null || "".equals(taskId)) {
            return CommonResult.failed("任务id不能为空");
        }

        return CommonResult.success(parcelTaskService.getProcessDetails(taskId));
    }

    @GetMapping("/detail")
    public CommonResult<ParcelTask> taskDetails(String taskId) {

        if(taskId == null || "".equals(taskId)) {
            return CommonResult.failed("任务id不能为空");
        }

        return CommonResult.success(parcelTaskService.getParcelTaskDetails(taskId));
    }

    @PostMapping("/check")
    public CommonResult<String> checkParcelTask(@RequestBody Map<String,Object> map) {

        String taskId = (String) map.get("taskId");

        if(taskId == null || "".equals(taskId)) {
            return CommonResult.failed("任务id不能为空");
        }

        parcelTaskService.sureParcelTask(taskId);

        return CommonResult.success("确认成功");
    }

    @PostMapping("/complete")
    public CommonResult<String> completeParcelTask(@RequestBody Map<String,Object> map) {

        String taskId = (String) map.get("taskId");

        if(taskId == null || "".equals(taskId)) {
            return CommonResult.failed("任务id不能为空");
        }

        String picture = (String) map.get("picture");

        if(picture == null || "".equals(picture)) {
            return CommonResult.failed("图片不能为空");
        }

        parcelTaskService.completeParcelTask(taskId,picture);

        return CommonResult.success("确认成功");
    }

    @PostMapping("/comment")
    public CommonResult<String> comment(@RequestBody Review review){
        System.out.println(review);
        if (review.getStars()==null||review.getStars()<1||review.getStars()>5){
            return CommonResult.failed("请给出正确评分");
        }

        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        review.setPublisherId(id);

        if(!parcelTaskService.finishParcelTask(review.getTaskId(),id)){
            return CommonResult.failed("评论失败");
        }

        review.setTime(new Date());

        reviewService.addReview(review);

        return CommonResult.success("评论成功");
    }

    @GetMapping("/comment")
    public CommonResult<ReviewVO> comment(String taskId){
        return CommonResult.success(reviewService.getReview(taskId));
    }

    @PostMapping("/pub/info")
    CommonResult<Map<String,Object>> getInfo(@RequestBody Map<String,Object> map){
        Long id= ((Integer) map.get("id")).longValue();

        map.put("score",scoreService.getScore(id));

        Criteria criteria = new Criteria();
        criteria.and("publisherId").is(id);


        long count = mongoTemplate.count(new Query(new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("status").is("待接单"),Criteria.where("status").is("待接单2")))), ParcelTask.class, "parcel_task");

        map.put("waitAcpCount",count);

        count = mongoTemplate.count(new Query(criteria),ParcelTask.class,"parcel_task");

        map.put("totalCount",count);

        map.put("complainCount",0);

        return CommonResult.success(map);
    }

    @RequestMapping("/test")
    public CommonResult<String> test() {
        parcelTaskService.testXX();
        return CommonResult.success("test");
    }

}
