package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {
    /**
     *dddd
     * @return
     */
    @GetMapping("/hello")
    public RestBean<List<UserVO>> hello(){
        UserVO userVO = new UserVO();
        userVO.setName("小明");
        userVO.setDate("2000-08-25");
        userVO.setAddress("北京");

        UserVO userVO1 = new UserVO();
        userVO1.setName("小亮");
        userVO1.setDate("1999-08-25");
        userVO1.setAddress("上海");
        List<UserVO> userVOList = new ArrayList<>();
        userVOList.add(userVO);
        userVOList.add(userVO1);
        return RestBean.success(userVOList);
    }


}
