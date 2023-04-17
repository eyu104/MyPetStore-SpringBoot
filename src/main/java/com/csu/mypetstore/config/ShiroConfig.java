package com.csu.mypetstore.config;

import com.csu.mypetstore.filter.JwtFilter;
import com.csu.mypetstore.filter.MyAnonymousFilter;
import com.csu.mypetstore.shiro.JwtDefaultSubjectFactory;
import com.csu.mypetstore.shiro.JwtRealm;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public JwtRealm getJwtRealm(){
        return new JwtRealm();
    }

    //不使用defaultSubject创建对象
    @Bean
    public SubjectFactory subjectFactory(){
        return new JwtDefaultSubjectFactory();
    }

    @Bean
    public SecurityManager getSecurityManager(){
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        defaultSecurityManager.setRealm(getJwtRealm());

        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        defaultSecurityManager.setSubjectDAO(subjectDAO);

        defaultSecurityManager.setSubjectFactory(subjectFactory());

        return defaultSecurityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/account/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/account/login");

        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("anon", new MyAnonymousFilter());
        filterMap.put("jwt",new JwtFilter());

        shiroFilterFactoryBean.setFilters(filterMap);


        Map<String, String> filterChainMap = new LinkedHashMap<>();

        filterChainMap.put("/swagger-ui.html", "anon");
        filterChainMap.put("/swagger-resources", "anon");
        filterChainMap.put("/swagger-resources/configuration/security", "anon");
        filterChainMap.put("/swagger-resources/configuration/ui", "anon");
        filterChainMap.put("/v2/api-docs", "anon");
        filterChainMap.put("/webjars/springfox-swagger-ui/**", "anon");

        filterChainMap.put("/account/info","jwt");
        filterChainMap.put("/account/edit","jwt");
        filterChainMap.put("/account/info","jwt");
        filterChainMap.put("/cart/**","jwt");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =  new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(getSecurityManager());
        return authorizationAttributeSourceAdvisor;
    }



}
