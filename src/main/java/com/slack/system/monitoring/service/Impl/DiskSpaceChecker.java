package com.slack.system.monitoring.service.Impl;

import com.slack.system.monitoring.service.MetricsChecker;
import com.slack.system.monitoring.service.MetricsIssuesReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class DiskSpaceChecker implements MetricsChecker {
    @Value("${directoryName}")
    private String directoryName;
    @Value("${metrics.limit.dirSpace}")
    private double limit;

    @Autowired
    private MetricsIssuesReporter metricsIssuesReporter;

    @Scheduled(initialDelay = 5_000, fixedDelay = 5_000)
    public void check() {
        try {
            File file = new File(directoryName);
            long totalSpace = file.getTotalSpace();
            long usableSpace = file.getUsableSpace();
            double usablePercentage = ((double) usableSpace) / totalSpace;
            System.out.println(usablePercentage);

            if (totalSpace > 0 && usablePercentage < limit) {
                String issue = String.format("Directory %s only has %d%% usable disk space",
                        directoryName, (int) (usablePercentage * 100));
                metricsIssuesReporter.reportIssue(issue);

            }
        } catch (Exception ex) {
            log.error("Unable to check disk space of dir {}, {}", directoryName, ex.getMessage());
        }

    }
}
