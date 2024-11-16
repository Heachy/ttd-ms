package com.cy.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import com.cy.common.api.CommonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.message.AuthException;

/**
 * @author Heachy
 */
@RestControllerAdvice
public class SaTokenException {

    /** 全局异常拦截（拦截项目中的NotLoginException异常）
     *
     * @param nle 异常
     * @return 自定义异常信息
     */
    @ExceptionHandler(NotLoginException.class)
    public CommonResult<String> handlerNotLoginException(NotLoginException nle) {

        // 打印堆栈，以供调试
        nle.printStackTrace();

        // 判断场景值，定制化异常信息
        String message = switch (nle.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供token";
            case NotLoginException.INVALID_TOKEN -> "token无效";
            case NotLoginException.TOKEN_TIMEOUT -> "token已过期";
            case NotLoginException.BE_REPLACED -> "token已被顶下线";
            case NotLoginException.KICK_OUT -> "token已被踢下线";
            default -> "当前会话未登录";
        };
        System.out.println("token异常拦截：" + message);
        // 返回给前端
        return CommonResult.unauthorized(message);
    }

    @ExceptionHandler(AuthException.class)
    public CommonResult<String> myException(AuthException ae) {
        ae.printStackTrace();

        return CommonResult.forbidden("没有权限操作");
    }

    @ExceptionHandler(NotRoleException.class)
    public CommonResult<String> myRoleException(NotRoleException ae) {
        ae.printStackTrace();

        return CommonResult.forbidden("没有权限操作");
    }
}
