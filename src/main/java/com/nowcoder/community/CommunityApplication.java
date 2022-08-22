package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

	@PostConstruct
	public void init() {
		// 解决netty启动冲突问题（使用redis需要启动netty，使用elasticsearch也需要启动netty。
		// 在正在使用redis的情况下，netty已启动，之后开始使用es时，会因为扫描到已启动的netty而不再另启动netty，实际上需要另启动一个netty）
		// see Netty4Utils.setAvailableProcessor()
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);//CommunityApplication.class相当于一个配置文件
	}

}
