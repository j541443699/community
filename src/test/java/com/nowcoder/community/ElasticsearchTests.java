package com.nowcoder.community;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Date:2022/8/21 23:04
 * Author:jyq
 * Description:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    // @Autowired
    // private ElasticsearchTemplate elasticsearchTemplate;// es6
    @Autowired
    private RestHighLevelClient restHighLevelClient;// es7

    // 一次插入单条数据
    @Test
    public void testInsert() {
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));
    }

    // 一次插入多条数据
    @Test
    public void testInsertList() {
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134, 0, 100, 0));
    }

    // 修改数据
    @Test
    public void testUpdate() {
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水。");
        discussRepository.save(post);
    }

    // 删除数据
    @Test
    public void testDelete() {
        // discussRepository.deleteById(231);
        discussRepository.deleteAll();
    }

    // 带高亮的查询
    @Test
    public void /*testSearchByRepository()*/highlightQuery() throws IOException {
        // 构建搜索条件（es7.x中SearchQuery以及search方法已被废弃）
        // SearchQuery searchQuery = new NativeSearchQueryBuilder()
        //         .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
        //         .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
        //         .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
        //         .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
        //         .withPageable(PageRequest.of(0, 10))
        //         .withHighlightFields(
        //                 new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
        //                 new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
        //         ).build();
        // Page<DiscussPost> page = discussRepository.search(searchQuery);// 底层执行的代码为'elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)'，获取到了高亮显示的值，但没有加到page中，需要我们手动处理高亮显示。


        // 构建搜索条件（es7.x的写法，不分discussRepository和elasticTemplate，参考https://blog.csdn.net/wpw2000/article/details/115704320?spm=1001.2014.3001.5502）
        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        // 构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0)// 指定从哪条开始查询
                .size(10)// 需要查出的总记录条数
                .highlighter(highlightBuilder);// 高亮

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);// title和content字段内容都是原始内容，没有经过高亮显示处理

            // 手动处理高亮显示的结果
            HighlightField titleField = hit.getHighlightFields().get("title");// 返回的是一个数组，包含多个高亮显示的匹配字段
            if(titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());// 只存储第一个高亮显示的匹配字段
            }
            HighlightField contentField = hit.getHighlightFields().get("content");// 返回的是一个数组，包含多个高亮显示的匹配字段
            if(contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());// 只存储第一个高亮显示的匹配字段
            }
            System.out.println(discussPost);
            list.add(discussPost);
        }
    }
}
