package com.itcrud.common.web;

import com.alibaba.fastjson.JSON;
import com.itcrud.common.aspectloghandler.SensitiveWord;
import com.itcrud.common.web.dto.LogAspectHandlerReqDTO;
import com.itcrud.common.web.vo.LogAspectHandlerVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/12/7 14:24
 * @Modified By:
 * @Project_name: itcrud-commons
 * @Version 1.0
 */
@Controller
@Slf4j
@RequestMapping("/itcrud")
public class LogAspectController {

    //GET请求
    @GetMapping("/aspectLogHandlerForGet")
    @ResponseBody
    public LogAspectHandlerVO aspectLogHandlerForGet(@RequestParam("phone") @SensitiveWord String phone,
                                                     @Param("name") String name) {
        MDC.put("mdcLogId","12344556");
        LogAspectHandlerVO vo = new LogAspectHandlerVO();
        vo.setId(1);
        vo.setPhone("15618292833");
        vo.setCardNo("342401199911095678");
        vo.setUserName("张三丰丰");
        return vo;
    }

    //POST请求
    @PostMapping("/aspectLogHandlerForPost")
    @ResponseBody
    public LogAspectHandlerVO aspectLogHandlerForPost(@RequestBody @SensitiveWord LogAspectHandlerReqDTO reqDTO) {
        log.info("innerPrint-->{}", reqDTO);
        LogAspectHandlerVO vo = new LogAspectHandlerVO();
        vo.setId(2);
        vo.setPhone("15618292832");
        vo.setCardNo("342401199911095679");
        vo.setUserName("张三丰");
        return vo;
    }
}
