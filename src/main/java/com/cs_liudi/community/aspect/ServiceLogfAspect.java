
package com.cs_liudi.community.aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogfAspect {

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogfAspect.class);

    @Pointcut("execution(* com.cs_liudi.community.service.*.*(..))")
    public void pointcut(){

    }
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String className= joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String target = className+"."+methodName;
        logger.info(String.format("用户[%s]在[%s],访问了[%s]方法",ip,date,target));
    }


}
