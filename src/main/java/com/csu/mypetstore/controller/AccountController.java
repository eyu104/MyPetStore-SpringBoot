package com.csu.mypetstore.controller;

import com.csu.mypetstore.config.Result;
import com.csu.mypetstore.domain.Account;
import com.csu.mypetstore.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/find")
    public Result<?> getAccountByUsername(@RequestParam String username) {
        Account account = accountService.getAccount(username);
        return Result.success(account);
    }





}
