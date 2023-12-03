package com.cs_liudi.community.aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class HelloAspect {
    @Pointcut("execution(* com.cs_liudi.community.service.*.*(..))")
    public void pointcut(){

    }
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }
    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }
    @AfterThrowing("pointcut()")
    public void aftereThrowing(){
        System.out.println("aftereThrowing");
    }
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Around Before");
        Object o = joinPoint.proceed();
        System.out.println("Around After");
        return o;
    }

}
