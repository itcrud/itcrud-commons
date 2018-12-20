package com.itcrud.common.web.vo;

import com.itcrud.common.aspectloghandler.SensitiveWord;
import lombok.Data;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/12/7 14:27
 * @Modified By:
 * @Project_name: itcrud-commons
 * @Version 1.0
 */
@Data
public class LogAspectHandlerVO {
    private   Integer id;
    @SensitiveWord
    private String phone;
    @SensitiveWord
    private String cardNo;
    @SensitiveWord
    private String userName;
}
