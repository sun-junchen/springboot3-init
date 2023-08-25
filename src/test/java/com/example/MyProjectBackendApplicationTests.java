package com.example;

import com.example.entity.BaseData;
import com.example.entity.dto.AccountDTO;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.utils.BeanHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class MyProjectBackendApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }


    @Test
    void contextLoads1() {
        AccountDTO accountDTO = new AccountDTO(1L,"test","123456","112@qq.com","user",new Date());
        AuthorizeVO authorizeVO = accountDTO.asViewObject(AuthorizeVO.class);
        System.out.println(authorizeVO);
    }


    @Test
    void contextLoads2() {
        AccountDTO accountDTO = new AccountDTO(1L,"test","123456","112@qq.com","user",new Date());
        AccountDTO accountDTO2 = new AccountDTO(2L,"test","123456","112@qq.com","user",new Date());
        List<AccountDTO> objects = new ArrayList<>();
        objects.add(accountDTO);
        objects.add(accountDTO2);
        List<AuthorizeVO> list1 = objects.stream().map(object -> object.asViewObject(AuthorizeVO.class)).toList();
        list1.forEach(System.out::println);
    }
}
