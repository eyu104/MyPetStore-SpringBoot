package com.csu.mypetstore.controller;

import com.csu.mypetstore.config.Result;
import com.csu.mypetstore.domain.Account;
import com.csu.mypetstore.domain.Cart;
import com.csu.mypetstore.domain.vo.AccountVO;
import com.csu.mypetstore.service.AccountService;
import com.csu.mypetstore.service.SmsService;
import com.csu.mypetstore.util.JwtUtil;
import com.csu.mypetstore.util.JwtUtil2;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtil2 jwtUtil2;


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
            AccountVO accountVO = new AccountVO();
            BeanUtils.copyProperties(newAccount,accountVO);
            // 将新的token存入redis
            Map<Object, Object> newToken = new HashMap<>();
            // 用户信息加密token，包含用户电话，用户角色信息
            Map<String, Object> chaim = new HashMap<>();
            List<String> roles = new ArrayList<>();
            roles.add("account");
            chaim.put("username",accountVO.getUsername());
            chaim.put("role",roles);
            String jwtToken = jwtUtil2.encode(accountVO.getUsername(),24 * 60 * 60 * 1000,chaim);
            System.out.println(jwtToken);
            newToken.put("token", jwtToken);
            jwtUtil2.addTokenToCache(newToken);
            accountVO.setToken(jwtToken);
            return Result.success(accountVO);
        }else {
            return Result.error("401","账户不存在");
        }
    }


    /**
     * token获取用户名
     * 存入sessionStore
     * @param request
     * @return
     */
    @GetMapping("/info")
    public Result<?> getInfo(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");

        Claims claims = jwtUtil2.decode(jwtToken);

        String username = (String) claims.get("username");
        Map<Object,Object> newtoken = jwtUtil2.getTokenFromCache(username);
        String t = (String) newtoken.get("token");
        if (jwtToken.equals(t)){
            Account account = accountService.getAccount(username);
            return Result.success(account);
        }else {
            return Result.error("400","已过期");
        }

    }

    /**
     * 编辑用户信息
     * @param account
     * @return
     * @throws SQLException
     */
    @PostMapping("/edit")
    public Result<?> editAccount(@RequestBody Account account) throws SQLException {
        System.out.println(account);

        accountService.updateAccount(account);
        return Result.success(account);
    }

    /**
     * 新增用户
     * @param account
     * @return
     * @throws SQLException
     */
    @PostMapping("/new")
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


    @GetMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");

        Claims claims = jwtUtil2.decode(jwtToken);
        String username = (String) claims.get("username");
        jwtUtil2.deleteToken(username);
        return Result.success("log out success");
    }












}
