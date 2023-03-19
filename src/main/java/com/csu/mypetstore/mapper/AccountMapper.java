package com.csu.mypetstore.mapper;

import com.csu.mypetstore.domain.Account;
import org.apache.ibatis.annotations.Mapper;
import java.sql.SQLException;

@Mapper
public interface AccountMapper {

    Account getAccountByUsername(String username);

    Account getAccountByUsernameAndPassword(Account account);

    void insertAccount(Account account) throws SQLException;

    void insertProfile(Account account);

    void insertSignon(Account account);

    void updateAccount(Account account) throws SQLException;

    void updateProfile(Account account);

    void updateSignon(Account account);
}
