package com.example.recipes.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationListener<WebServerInitializedEvent> {

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        System.out.println("\n----------------------------------------------------------");
        System.out.println("\t Application is running at: http://localhost:" + port);
        System.out.println("----------------------------------------------------------\n");
    }
}