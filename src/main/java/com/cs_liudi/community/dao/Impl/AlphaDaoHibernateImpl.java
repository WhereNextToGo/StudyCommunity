package com.cs_liudi.community.dao.Impl;

import com.cs_liudi.community.dao.AlphaDao;
import org.springframework.stereotype.Repository;

@Repository("AlphaDaoHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "hibernate";
    }
}
