package com.csu.mypetstore.controller;

import com.csu.mypetstore.config.Result;
import com.csu.mypetstore.domain.Account;
import com.csu.mypetstore.service.AccountService;
import com.csu.mypetstore.service.SmsService;
import com.csu.mypetstore.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * 验证登录返回token
     * @param account
     * @return
     */
    @PostMapping("/login2")
    public Result<?> login(@RequestBody Account account) {
        //验证是否存在
        Account newAccount = accountService.getAccount(account.getUsername(),account.getPassword());
        if (newAccount != null){
            //生成token
            String token = JwtUtil.generateToken(account.getUsername());
            return Result.success(token);
        }else {
            return Result.error("404","账户不存在");
        }
    }

    @PostMapping("/login")
    public Result<?> login1(@RequestBody Account account) {
        //验证是否存在
        Account newAccount = accountService.getAccount(account.getUsername(),account.getPassword());
        if (newAccount != null){
            return Result.success(newAccount);
        }else {
            return Result.error("404","账户不存在");
        }
    }

    /**
     * 通过token获得用户信息
     * @param token
     * @return
     */
    @GetMapping("/info")
    public Result<?> getInfo(@RequestParam String token) {
        String username = JwtUtil.getClaimsByToken(token).getSubject();
        Account account = accountService.getAccount(username);
        return Result.success(account);
    }

    /**
     * 编辑用户信息
     * @param account
     * @return
     * @throws SQLException
     */
    @PostMapping("/edit")
    public Result<?> editAccount(@RequestBody Account account) throws SQLException {
        accountService.updateAccount(account);
        return Result.success(account);
    }

    /**
     * 新增用户
     * @param account
     * @return
     * @throws SQLException
     */
    @PostMapping("new")
    public Result<?> newAccount(@RequestBody Account account) throws SQLException {
        if (accountService.getAccount(account.getUsername()) != null){
            return Result.error("1","账号已存在");
        }else {
            accountService.insertAccount(account);
            return Result.success();
        }
    }

    //注册是使用，检查用户名是否存在
    @PostMapping("isExist")
    public Result<?> accountIsExist(@RequestParam String username) throws SQLException {
        if (accountService.getAccount(username) != null){
            return Result.error("1","账号已存在");
        }else {
            return Result.success();
        }
    }













}
