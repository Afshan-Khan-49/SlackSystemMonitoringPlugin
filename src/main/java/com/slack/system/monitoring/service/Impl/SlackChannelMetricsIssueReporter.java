package com.slack.system.monitoring.service.Impl;

import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.slack.system.monitoring.Exception.SlackPluginException;
import com.slack.system.monitoring.service.MetricsIssuesReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SlackChannelMetricsIssueReporter implements MetricsIssuesReporter {
    @Autowired
    private SlackClient slackClient;
    @Value("${slack.channel.name}")
    private String channel;

    @Override
    public void reportIssue(String issue) {

        log.info("Sending message to slack :  {}", issue);


        slackClient.postMessage(
                ChatPostMessageParams.builder()
                        .setText(issue)
                        .setChannelId(channel)
                        .build()
        ).join().ifErr(slackError -> {
            throw new SlackPluginException("Unable to publish message to slack : " + slackError.getError());
        });

    }

}
