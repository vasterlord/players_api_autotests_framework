package com.api.tests.listeners;

import lombok.extern.slf4j.Slf4j;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import java.util.List;

@Slf4j
public class ThreadCountChangerListener implements IAlterSuiteListener {

    @Override
    public void alter(List<XmlSuite> suites) {
        log.info("Set custom testNg suites threads count");
        int count = Integer.parseInt(System.getProperty("threads.count", "1"));
        if (count <= 0) {
            log.warn("'threads.count' should be positive number value. 'threads.count' was set to default: 1");
            count = 1;
        }
        for (XmlSuite suite : suites) {
            suite.setThreadCount(count);
        }
    }

}
