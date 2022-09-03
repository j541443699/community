package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Date:2022/9/2 16:28
 * Author:jyq
 * Description:
 */

@Configuration
@EnableScheduling// 用于spring定时任务线程池
@EnableAsync// 用于Spring普通线程池(简化)
public class ThreadPoolConfig {
}
