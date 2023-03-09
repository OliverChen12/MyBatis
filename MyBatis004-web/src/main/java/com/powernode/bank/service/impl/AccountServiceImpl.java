package com.powernode.bank.service.impl;

import com.powernode.bank.dao.AccountDao;
import com.powernode.bank.exceptions.MoneyNotEnoughException;
import com.powernode.bank.exceptions.TransferException;
import com.powernode.bank.pojo.Account;
import com.powernode.bank.service.AccountService;
import com.powernode.bank.utils.GenerateDaoProxy;
import com.powernode.bank.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

public class AccountServiceImpl implements AccountService {

   // private AccountDao accountDao = (AccountDao) GenerateDaoProxy.generate(AccountDao.class);

    private AccountDao accountDao = SqlSessionUtil.openSession().getMapper(AccountDao.class);

    @Override
    public void transfer(String fromActno, String toActno, double money) throws MoneyNotEnoughException, TransferException {

        SqlSession sqlSession = SqlSessionUtil.openSession();

        Account fromAct = accountDao.selectByActno(fromActno);

        if (fromAct.getBalance() < money) {
            throw new MoneyNotEnoughException("对不起，余额不足！");
        }

        Account toAct = accountDao.selectByActno(toActno);
        fromAct.setBalance(fromAct.getBalance() - money);
        toAct.setBalance(toAct.getBalance() + money);
        int count = accountDao.updateByActno(fromAct);
        count += accountDao.updateByActno(toAct);

        if(count != 2){
            throw new TransferException("转账异常，未知原因");
        }

        sqlSession.commit();
        SqlSessionUtil.close(sqlSession);
    }
}
