package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * Date:2022/7/12 19:45
 * Author:jyq
 * Description:
 */
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
