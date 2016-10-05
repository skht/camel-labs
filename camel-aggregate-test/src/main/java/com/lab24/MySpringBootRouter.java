package com.lab24;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
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
    public RouteBuilder slowRoute(final NumberGenerator numberGenerator) {

		return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

            from("timer:trigger?period=1000")
            	.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						long number = numberGenerator.generateNumber();
						if(number > 0) {
							exchange.getOut().setHeader("aggregate", true);
						} else {
//							exchange.getOut().setHeader("aggregate", false);
						}
						exchange.getOut().setBody(number);
					}
				})
            	.log(simple("${body}").getText())
            	.aggregate(header("aggregate"), new AggregationStrategy() {
					@Override
					public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
						if(oldExchange == null) {
							List values = new ArrayList<>();
							values.add(newExchange.getIn().getBody());
							newExchange.getIn().setBody(values);
							oldExchange = newExchange;
						} else {
							List values  = (List)oldExchange.getIn().getBody();
							values.add(newExchange.getIn().getBody());
						};
						return oldExchange;
					}
				 })
            	.completionTimeout(2000)
            	.completionSize(5)
            	.ignoreInvalidCorrelationKeys()
            	.throttle(1)
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
