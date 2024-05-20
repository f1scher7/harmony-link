package com.harmonylink.harmonylink.services.realtime;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebSocketService {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Object lock = new Object();


    public void sendSynchronizedMessage(WebSocketSession session, String message) {
        executorService.submit(() -> {
            synchronized (lock) {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(message));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
