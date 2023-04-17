package com.csu.mypetstore.shiro;

import cn.hutool.core.util.StrUtil;
import com.csu.mypetstore.util.JwtUtil2;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JwtRealm extends AuthorizingRealm {

    @Autowired
    private JwtUtil2 jwtUtil2;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String jwt = (String) principals.getPrimaryPrincipal();
        String username = (String) jwtUtil2.decode(jwt).get("username");

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        authorizationInfo.addRole("account");
        return authorizationInfo;
    }

    /**
     * 认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String jwt = (String) token.getPrincipal();
        if (StrUtil.isEmpty(jwt)){
            throw new IncorrectCredentialsException("AuthenticationToken is invalid");
        }
        Claims claims = jwtUtil2.decode(jwt);
        String username = (String) claims.get("username");
        Map<Object,Object> authorization = jwtUtil2.getTokenFromCache(username);
        if (authorization == null || authorization.get("token") == null || !jwt.equals(authorization.get("token"))){
            throw new IncorrectCredentialsException("AuthenticationToken is invalid");
        }
        if (!jwtUtil2.isVerify(jwt)) {
            throw new IncorrectCredentialsException("AuthenticationToken is invalid");
        }

        return new SimpleAuthenticationInfo(jwt,jwt,"JwtRealm");
    }
}
