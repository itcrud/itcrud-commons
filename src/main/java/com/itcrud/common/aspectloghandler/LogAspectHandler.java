package com.itcrud.common.aspectloghandler;

import com.alibaba.fastjson.JSON;
import com.itcrud.common.web.dto.LogAspectHandlerReqDTO;
import com.itcrud.common.web.vo.LogAspectHandlerVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/12/7 14:06
 * @Modified By:
 * @Project_name: itcrud-commons
 * @Version 1.0
 */
@Aspect
@Component
@Slf4j
public class LogAspectHandler {

    @Pointcut("execution(* com.itcrud.common.web..*Controller.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //请求方法信息
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        StringBuilder logStr = new StringBuilder();
        logStr.append("请求方法：").append(joinPoint.getTarget().getClass().getName())
                .append(".").append(method.getName()).append("()");
        //获取参数，组装参数
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = ms.getParameterNames();
        StringBuilder params = new StringBuilder(" 请求参数：");
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = args[i];
            SensitiveWord sw = parameter.getAnnotation(SensitiveWord.class);
            Object copyValue = null;
            if (sw != null) {//需要脱敏
                if (value instanceof String) {
                    value = sensitive(value);
                } else {
                    //如果是对象的话需要考虑对象内套用对象的问题，也就涉及到递归，这里不具体实现，此处简单举例
                    //对象创建副本，在副本上操作
                    copyValue = value.getClass().newInstance();
                    BeanUtils.copyProperties(value, copyValue);
                    requestParamSensitive(copyValue);
                }
            } else {
                //对象创建副本，在副本上操作
                int modifiers = value.getClass().getModifiers();
                if (!Modifier.isFinal(modifiers)) {
                    copyValue = value.getClass().newInstance();
                    BeanUtils.copyProperties(value, copyValue);
                    requestParamSensitive(copyValue);
                }
            }
            params.append(parameterNames[i]).append(":").append(copyValue == null
                    ? JSON.toJSONString(value) : JSON.toJSONString(copyValue)).append(";");
        }
        //执行操作
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        /*一般controller都是统一一个VO响应类封装响应信息，
        然后里面用data字段来封装具体的数据，这个时候可以根据实际情况来获取具体数据信息进行脱敏操作,
        这里仅做示例*/
        if (result instanceof LogAspectHandlerVO) {
            reflectFields(result);
        }
        logStr.append(params.toString());
        logStr.append(" 执行耗时：").append(stopWatch.getTotalTimeMillis()).append("ms");
        logStr.append(" 响应数据：").append(JSON.toJSONString(result));
        log.info(logStr.toString());
        return result;
    }

    //请求参数处理部分
    private void requestParamSensitive(Object value) throws Exception {
        if (value instanceof List) {
            //TODO dosomthing
        }
        if (value instanceof Map) {
            //TODO doSomthing
        }
        if (value instanceof LogAspectHandlerReqDTO) {
            reflectFields(value);
        }
    }

    //反射类中字段
    private void reflectFields(Object object) throws Exception {
        Class voClazz = object.getClass();
        Field[] fields = voClazz.getDeclaredFields();
        if (fields != null && fields.length != 0) {
            for (Field field : fields) {
                SensitiveWord sw = field.getAnnotation(SensitiveWord.class);
                if (sw != null) {
                    field.setAccessible(true);
                    Object f = field.get(object);
                    if (f instanceof String) field.set(object, sensitive(f));
                }
            }
        }
    }

    //脱敏操作
    private String sensitive(Object value) {
        if (value == null) return "null";
        String str = String.valueOf(value);
        if (StringUtils.isBlank(str)) return "";
        int length = str.length();
        if (length <= 3) {
            str = str.substring(0, 1) + (length == 3 ? "**" : "*");
        } else {
            int v = length >> 1;
            if (v > 7) {
                str = longSensitive(str, 4);
            } else if (v > 4) {
                str = longSensitive(str, 3);
            } else if (v > 3) {
                str = longSensitive(str, 2);
            } else {
                str = longSensitive(str, 1);
            }
        }
        return str;
    }

    //长敏感词
    private String longSensitive(String str, int offset) {
        String s = str.substring(0, offset);
        for (int i = 0; i < str.length() - (offset << 1); i++) s += "*";
        return s + str.substring(str.length() - offset, str.length());
    }
}
