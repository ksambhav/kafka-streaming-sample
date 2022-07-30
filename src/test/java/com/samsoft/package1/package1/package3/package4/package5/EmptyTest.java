package com.samsoft.package1.package1.package3.package4.package5;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
public class EmptyTest {

    @Test
    void test() {
        log.debug("test");
        log.warn("test");
        log.error("test");
        log.info("test");
        log.trace("test");
        log.fatal("fatal");
        log.debug("{},{}", () -> "test", () -> "test2");
        RuntimeException ex = new RuntimeException("message1", new RuntimeException("message2", new RuntimeException("message4", new RuntimeException("message5"))));
        log.error("Testing", ex);
    }
}
