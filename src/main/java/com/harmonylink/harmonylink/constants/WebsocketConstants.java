package com.harmonylink.harmonylink.constants;

public final class WebsocketConstants {

    public static final String HEARTBEAT_REQUEST_JSON = "{\"type\":\"HEARTBEAT_REQUEST\"}";
    public static final String HEARTBEAT_RESPONSE_JSON = "{\"type\":\"HEARTBEAT_RESPONSE\"}";
    public static final String TALKER_NICKNAME_JSON = "{\"type\":\"TALKER_NICKNAME\",\"nickname\":\"%s\"}";
    public static final String SEND_MESSAGE_FOR_RECIPIENT_JSON = "{\"type\":\"SEND_MESSAGE_FOR_RECIPIENT\",\"mess\":\"%s\"}";

}
