package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * Date:2022/9/3 10:16
 * Author:jyq
 * Description:
 */

// 该配置文件封装的信息第一次初始化到数据库中，之后不再访问该配置文件
// quartz直接访问数据库，读取封装信息来调用任务
@Configuration
public class QuartzConfig {

    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.可将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean
    // 4.该Bean得到的是FactoryBean所管理的对象实例.

    // 配置JobDetail
    // @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);// 声明所管理的Job类
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 配置Trigger的两种方式：SimpleTriggerFactoryBean（简单，适合十分钟执行一次任务）, CronTriggerFactoryBean（复杂，适合每周周五晚上十点执行一次任务，有特殊表达式，可实现复杂逻辑）
    // @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {// 这里的alphaJobDetail注入的是JobDetailFactoryBean所管理的JobDetail
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);// 任务执行周期，毫秒
        factoryBean.setJobDataMap(new JobDataMap());// 指定JobDataMap对象存储Job的一些状态信息
        return factoryBean;
    }
}
