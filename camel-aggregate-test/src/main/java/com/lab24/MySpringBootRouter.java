package com.lab24;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.GroupedMessageAggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MySpringBootRouter {

    private static final Logger log = LoggerFactory.getLogger(MySpringBootRouter.class);

	
	public static void main(String[] args) {
		log.info(" *** Starting Camel Metrics Example Application ***");
        SpringApplication.run(MySpringBootRouter.class, args);
	}
	
	@Bean
    public RouteBuilder slowRoute() {

		return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

            from("timer:trigger?period=1000")
            	.bean(NumberGenerator.class, "generateNumber")
            	.log(simple("${body}").getText())
            	.aggregate(new GroupedMessageAggregationStrategy())
            	.constant(true)
            	.completionTimeout(1500)
            	.completionSize(5)
            	.log("Aggregating item")
            	.to("log:out")
            .end();
            }
		};
    }

    private int counter = 0;

    @Bean
    String myBean() {
    	counter++;
    	return "I'm Spring bean!" + counter; 
    }

    @Bean
    NumberGenerator numGen() {
    	return new NumberGenerator();
    }
    
}
