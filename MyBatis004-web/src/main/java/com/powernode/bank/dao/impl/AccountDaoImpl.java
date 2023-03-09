package com.powernode.bank.dao.impl;

import com.powernode.bank.dao.AccountDao;
import com.powernode.bank.pojo.Account;
import com.powernode.bank.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

public class AccountDaoImpl implements AccountDao {
    @Override
    public Account selectByActno(String actno) {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        return sqlSession.selectOne("account.selectByActno", actno);
    }

    @Override
    public int updateByActno(Account act) {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        return sqlSession.update("account.updateByActno", act);
    }


}
