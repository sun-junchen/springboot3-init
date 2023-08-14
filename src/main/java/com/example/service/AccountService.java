package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<AccountDTO> , UserDetailsService {
     AccountDTO findAccountByNameOrEmail(String text);
}
