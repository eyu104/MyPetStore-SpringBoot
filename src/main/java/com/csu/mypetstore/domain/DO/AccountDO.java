package com.csu.mypetstore.domain.DO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csu.mypetstore.domain.Account;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("profile")
public class AccountDO implements Serializable {
    private String favcategory;
    private String langpref;
    private int myListOpt;
    private int bannerOpt;
    @TableId
    private String userid;

    public AccountDO(Account account) {
        this.favcategory = account.getFavouriteCategoryId();
        this.userid = account.getUsername();
        this.langpref = account.getLanguagePreference();
        this.myListOpt = account.isListOption() == true ? 1 : 0;
        this.bannerOpt = account.isBannerOption() == true ? 1 : 0;
    }
}
