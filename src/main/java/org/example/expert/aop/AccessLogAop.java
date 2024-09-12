package org.example.expert.aop;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "AccessLogAop")
@Aspect
@Component
@RequiredArgsConstructor
public class AccessLogAop {



    @Pointcut("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..))")
    private void commentPointcut() {}

    @Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    private void userPointcut() {}

    @Around("commentPointcut() || userPointcut()")
    public Object accessLog(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();

        Map<String, Object> params = new HashMap<>();

        try {

            params.put("requestTime", LocalDateTime.now());
            params.put("requestUrl", request.getRequestURL()); // URI URL 어떤거 사용할지

//            params.put("requestUrl", request.getRequestURI());

        } catch (Exception e){

            log.error("이후 에러 더 정확한 방식으로 수정할 것");

        }

        log.info("{}",params);

        Object result = joinPoint.proceed(); // 기준으로 실행 전 후 구분

        return result;

    }



}
