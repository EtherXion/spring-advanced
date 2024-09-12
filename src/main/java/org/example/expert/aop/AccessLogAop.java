package org.example.expert.aop;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.config.JwtUtil;
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

    private final JwtUtil jwtUtil; // 토큰 관련

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

//            // 이 경우 요청에 사용자 id가 포함되어 있어야만 가져올 수 있음
//            params.put("userId", request.getParameter("userId"));

            // 토큰에서 사용자 Id를 가져오는 방법
            params.put("userId",userIdFromToken(request));

        } catch (Exception e){

            log.error("이후 에러 더 정확한 방식으로 수정할 것");

        }

        log.info("API access log : {}",params);

        Object result = joinPoint.proceed(); // 기준으로 실행 전 후 구분

        return result;

    }

    // 현재 로그인된 사용자가 요청을 할테니 토큰에서 id 를 추출하는 방식?
    private String userIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization"); // Authorization 헤더의 값 가져옴
        if (authHeader != null && authHeader.startsWith("Bearer ")) { // 헤더가 Bearer로 시작하는지

            try {
                String token = jwtUtil.substringToken(authHeader);
                String userId = jwtUtil.extractClaims(token).getSubject();

                return userId;
            } catch (Exception e) {
                log.error("이후 오류 부분 수정할 것");
            }

        }
        return null;
    }

}
