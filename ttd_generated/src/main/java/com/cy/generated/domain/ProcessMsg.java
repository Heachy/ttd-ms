package com.cy.generated.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Haechi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessMsg {
    private long type;
    private Object message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    public static ProcessMsg publish() {
        return new ProcessMsg(StatusCode.PUBLISH.getType(), StatusCode.PUBLISH.getMessage(),new Date());
    }

    public static ProcessMsg accept() {
        return new ProcessMsg(StatusCode.ACCEPT.getType(), StatusCode.ACCEPT.getMessage(),new Date());
    }

    public static ProcessMsg getParcel(String picture) {
        Map<String,Object> map = new HashMap<>();

        map.put("msg",StatusCode.GET_PARCEL.getMessage());

        map.put("picture",picture);

        map.put("isSure",false);

        return new ProcessMsg(StatusCode.GET_PARCEL.getType(),map,new Date());
    }

    public static ProcessMsg sureParcel() {
        return new ProcessMsg(StatusCode.SURE_PARCEL.getType(), StatusCode.SURE_PARCEL.getMessage(),new Date());
    }

    public static ProcessMsg sendIng() {
        return new ProcessMsg(StatusCode.SEND_ING.getType(), StatusCode.SEND_ING.getMessage(),new Date());
    }

    public static ProcessMsg cancelAcp(String reason,String picture) {
        Map<String,Object> map = new HashMap<>(3);

        map.put("msg",StatusCode.CANCEL_ACP.getMessage());

        map.put("reason",reason);

        if(picture != null) {
            map.put("picture",picture);
        }


        return new ProcessMsg(StatusCode.CANCEL_ACP.getType(), map,new Date());
    }

    public static ProcessMsg complete(String picture) {
        Map<String,Object> map = new HashMap<>();

        map.put("msg",StatusCode.COMPLETE.getMessage());

        map.put("picture",picture);

        map.put("isSure",false);

        return new ProcessMsg(StatusCode.COMPLETE.getType(), map,new Date());
    }

    public static ProcessMsg sureGet() {
        return new ProcessMsg(StatusCode.SURE_GET.getType(), StatusCode.SURE_GET.getMessage(),new Date());
    }

    public static ProcessMsg cancelTask() {
        return new ProcessMsg(StatusCode.CANCEL_TASK.getType(), StatusCode.CANCEL_TASK.getMessage(),new Date());
    }

}
