package com.itcrud.common.web.dto;

import com.itcrud.common.aspectloghandler.SensitiveWord;
import lombok.Data;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/12/7 14:26
 * @Modified By:
 * @Project_name: itcrud-commons
 * @Version 1.0
 */
@Data
public class LogAspectHandlerReqDTO {
    private Integer id;
    @SensitiveWord
    private String name;
    @SensitiveWord
    private String phone;
}
