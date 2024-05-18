package com.harmonylink.harmonylink.services.user.useractivity;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserTabsControlService {

    private final ConcurrentHashMap<String, AtomicInteger> tabsCounter = new ConcurrentHashMap<>();


    public void incrementTabsCounter(String userProfileId) {
        tabsCounter.computeIfAbsent(userProfileId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public boolean decrementTabsCounter(String userProfileId) {
        AtomicInteger currentTabs = tabsCounter.get(userProfileId);

        if (currentTabs != null) {
            int newCurrentTabs = currentTabs.decrementAndGet();

            if (newCurrentTabs == 0) {
                tabsCounter.remove(userProfileId);
                return true;
            }
        }

        return false;
    }

    public String displayTabsCounter() {
        StringBuilder builder = new StringBuilder();
        tabsCounter.forEach((key, value) -> builder
                .append("Id: ").append(key)
                .append("; tabs: ").append(value.toString())
                .append("\n"));
        return builder.toString();
    }

}
