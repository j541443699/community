package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Date:2022/8/21 22:51
 * Author:jyq
 * Description:
 */

@Repository// 是spring提供的针对数据访问层的注解，而@Mapper是mybatis专有的数据访问层注解
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {// 泛型指定接口要处理的实体类，以及实体类中主键的数据类型

    // 父接口ElasticsearchRepository事先定义好对es服务器访问的增删改查等各种方法，声明完DiscussPostRepository并且加上@Repository之后，
    // spring会自动实现相关方法，我们直接调用即可。

}
