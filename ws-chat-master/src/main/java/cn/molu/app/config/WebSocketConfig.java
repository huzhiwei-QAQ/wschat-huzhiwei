package cn.molu.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Desc 首先需要注入 ServerEndpointExporter ， 这个bean会自动注册使用 @ServerEndpoint
 *       的注解来声明WebSocket endpoint。
 *       注意：如果使用独立的Servlet容器，而不是直接使用SpringBoot内置容器，就不需要注入
 *       ServerEndpointExporter，因为他将有容器自己提供和管理。
 * @author 陌路
 * @date 2022-04-16
 */
@Configuration
public class WebSocketConfig {
	/**
	 * @return ServerEndpointExporter
	 * @title 扫描注册使用 @ServerEndpoint 注解的类
	 * @Description TODO
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
}
