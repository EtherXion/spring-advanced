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

        String methodName = joinPoint.getSignature().getName(); // 어떤 api가 실행된건지
        Map<String, Object> params = new HashMap<>();

        try {

            params.put("methodName", methodName);
            params.put("requestTime", LocalDateTime.now());
            params.put("requestUrl", request.getRequestURL()); // URI URL 어떤거 사용할지
//            params.put("requestUrl", request.getRequestURI());

            // 이 경우 요청에 사용자 id가 포함되어 있어야만 가져올 수 있음
            params.put("userId", request.getParameter("userId"));

        } catch (Exception e){

            log.error("이후 에러 더 정확한 방식으로 수정할 것");

        }

        log.info("{}",params);

        Object result = joinPoint.proceed(); // 기준으로 실행 전 후 구분

        return result;

    }

    // 현재 로그인된 사용자가 요청을 할테니 토큰에서 id 를 추출하는 방식?
    private String userIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization"); // Authorization 헤더의 값 가져옴
        if (authHeader != null && authHeader.startsWith("Bearer ")) { // 헤더가 Bearer로 시작하는지
            String token = authHeader.substring(7); // 앞의 7자 Bearer 부분 제외하고 토큰 추출

            // 토큰에서 id 부분을 가져와야 함
//            String userId =

            return token;
        }
        return null;
    }



}
