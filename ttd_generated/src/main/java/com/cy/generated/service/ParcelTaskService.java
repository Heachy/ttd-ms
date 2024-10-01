package com.cy.generated.service;


import com.cy.generated.domain.ParcelTask;
import com.cy.generated.vo.ParcelTaskVO;
import java.util.List;
import java.util.Map;

/**
 * @author Haechi
 */
public interface ParcelTaskService {
    /**
     * 保存任务
     * @param parcelTask 任务
     * @return 任务
     */
    ParcelTask saveParcelTask(ParcelTask parcelTask);

    /**
     * 获取待承接任务列表
     * @param map 参数
     * @return 任务列表
     */
    List<ParcelTaskVO> taskWaitList(Map<String,Object> map);

    /**
     * 删除任务
     * @param taskId 任务id
     * @return 是否成功
     */
    boolean deleteParcelTask(String taskId);

    /**
     * 更新任务
     * @param parcelTask 任务
     */
    void updateParcelTask(ParcelTask parcelTask);

    /**
     * 获取我发布的任务列表
     * @return 任务列表
     */
    List<ParcelTask> myPubList();

    /**
     * 获取我承接的任务列表
     * @return 任务列表
     */
    List<ParcelTaskVO> myAcpList();

    /**
     * 承接任务
     * @param taskId 任务id
     * @return 是否成功
     */
    int acpParcelTask(String taskId,String avatar);

    /**
     * 确认任务完成
     * @param taskId 任务id
     * @return 是否成功
     */
    boolean confirmParcelTask(String taskId);

    /**
     * 取消承接任务
     * @param taskId 任务id
     * @param reason 理由
     * @return 是否成功
     */
    boolean acpCancelParcelTask(String taskId,String reason,String picture);

    /**
     * 获取任务进程详情
     * @param taskId 任务id
     * @return 任务详情
     */
    Map<String,Object> getProcessDetails(String taskId);

    /**
     * 获取任务详情
     * @param taskId 任务id
     * @return 任务详情
     */
    ParcelTask getParcelTaskDetails(String taskId);

    /**
     * 不检测用户id获取任务详情
     * @param taskId 任务id
     * @return 任务详情
     */
    ParcelTask getParcelTaskDetailsNoId(String taskId);

    /**
     * 承接方拿到快递
     * @param taskId 任务id
     * @param picture 图片
     */
    void getParcel(String taskId,String picture);

    /**
     * 确认快递
     * @param taskId 任务id
     */
    void sureParcelTask(String taskId);

    /**
     * 确认送达
     * @param taskId 任务id
     * @param picture 图片
     */
    void completeParcelTask(String taskId,String picture);

    /**
     * 确认完成
     * @param taskId 任务id
     * @param id 用户id
     * @return  是否成功
     */
    boolean finishParcelTask(String taskId,Long id);

    void testXX();
}
