package com.cy.generated.domain;

/**
 * @author Haechi
 */

public enum StatusCode {
    // 成功
    PUBLISH(1, "订单提交"),
    ACCEPT(2, "有骑手接单"),
    GET_PARCEL(3, "已取货"),
    SURE_PARCEL(4, "确认货物"),
    SEND_ING(5, "正在派送"),
    CANCEL_ACP(6, "取消接单"),
    COMPLETE(7, "已送达"),
    SURE_GET(8, "确认取货"),
    CANCEL_TASK(9, "取消订单");

    private final long type;
    private final String message;

    StatusCode(long type, String message) {
        this.type = type;
        this.message = message;
    }

    public long getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
