package com.huzhiwei.rabbitmq.config;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {

    private static final String QUEUE1 = "queue_direct01";
    private static final String QUEUE2 = "queue_direct02";



    //交换机
    private static final String EXCHANGE = "exchange";

    //路由
    private static final String routing_key01 = "queue.red";
    private static final String routing_key02 = "queue.green";

    @Bean
   public Queue queue1(){
       return new Queue(QUEUE1);
   }
    @Bean
    public Queue queue2(){
        return new Queue(QUEUE2);
    }


//    @Bean
//    public FanoutExchange exchange(){
//       return new FanoutExchange(EXCHANGE);
//    }

//    @Bean
//    public Binding binding1(){
//       return BindingBuilder.bind(queue1()).to(exchange());
//    }
//    @Bean
//    public Binding binding2(){
//       return BindingBuilder.bind(queue2()).to(exchange());
//    }
//}

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding binding1(){
        return BindingBuilder.bind(queue1()).to(exchange()).with(routing_key01);
    }
    @Bean
    public Binding binding2(){
        return BindingBuilder.bind(queue2()).to(exchange()).with(routing_key02);
    }
}
