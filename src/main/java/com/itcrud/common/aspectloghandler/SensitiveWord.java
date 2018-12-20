package com.itcrud.common.aspectloghandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/12/7 14:17
 * @Modified By:
 * @Project_name: itcrud-commons
 * @Version 1.0
 */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveWord {

}
