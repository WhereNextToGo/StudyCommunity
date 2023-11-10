package com.cs_liudi.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {
     private Logger logger = LoggerFactory.getLogger(LoggerTests.class);

     @Test public void testLogger(){
          System.out.println(logger.getName());
          logger.debug("debug log");
          logger.info("info log");
          logger.warn("warn log");
          logger.error("error log");
     }
}
