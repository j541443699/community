package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Date:2022/8/2 20:10
 * Author:jyq
 * Description:
 */

@Mapper
public interface CommentMapper {
    // 获取评论或回复
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    // 评论或回复的数量
    int selectCountByEntity(int entityType, int entityId);

    // 添加评论
    int insertComment(Comment comment);

    // 查询评论或回复
    Comment selectCommentById(int id);

}
