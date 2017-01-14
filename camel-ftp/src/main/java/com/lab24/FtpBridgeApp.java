package com.lab24;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

/**
 * Created by skht on 13.01.17.
 */
@SpringBootApplication
public class FtpBridgeApp {

    private static Logger log = LoggerFactory.getLogger(FtpBridgeApp.class.getClass());

    public static void  main(String[] args) {
        log.info("Starting FTP Bridge");
        SpringApplication.run(FtpBridgeApp.class, args);
    }

//    @Bean
    public RouteBuilder prepareFileToFileRoute() {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("file://input")
                .log("Passing the file")
                .to("file://output");
            }
        };
    }

    //${date:now:yyyyMMdd}/

    @Bean
    public RouteBuilder prepareFromFtpToFileRoute() {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("ftp://appuser@localhost/input?password=qwer1234&consumer.delay=1000m&filter=#myFilter")
                        .log("Passing the file $simple{in.header.CamelFileName}")
                        .to("file://output");
            }
        };
    }

    @Bean
    public RouteBuilder prepareToFtpFileRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file://inputFtp")
                        .log("Sending file $simple{in.header.CamelFileName}")
//                        .unmarshal().gzip()
                        .to("ftp://appuser@localhost/output?password=qwer1234&passiveMode=true");
            }
        };
    }

    @Bean
    public GenericFileFilter myFilter() {
        return new TodayFileNameFilter();
    }

    private class TodayFileNameFilter implements GenericFileFilter {
        public boolean accept(GenericFile file) {
            LocalDate today = LocalDate.now();

            String monthDay = Integer.toString(today.getMonthValue());
            if(today.getMonthValue() < 10) {
                monthDay = "0" + monthDay;
            }
            String day = Integer.toString(today.getDayOfMonth());
            if(today.getDayOfMonth() < 10) {
                day = "0" + day;
            }
            String todayDate = new StringBuilder()
                                .append(Integer.toString(today.getYear()).substring(2,4))
                                .append(monthDay)
                                .append(day)
                                .toString();


            log.info("Filtering by files with date {}", todayDate);
            return file.getFileName().contains(todayDate);
        }
    }
}
