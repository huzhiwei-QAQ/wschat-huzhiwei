package cn.molu.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 陌路
 * @title web配置类 设置默认访问页面
 * @date 2021/8/15
 * @apiNote 配置默认页面
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 自定义静态路径，spring.resource.static-locations=classpath:/static/
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("chat/login");
    }

}