export function sendUserIdByWebsocket(websocket) {
    if (websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'USER_PROFILE_ID', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}

export function sendHearBeatByWebsocket(websocket) {
    if (websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'HEARTBEAT_REQUEST' }));
    } else {
        console.error("WebSocket is not connected");
    }
}

export function sendInSearchStatusByWebsocket(websocket) {
    if (websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'IN_SEARCH', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}

export function sendStopSearchingByWebsocket(websocket) {
    if (websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'STOP_SEARCHING', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}

export function sendStopWebRTCConn(websocket) {
    if (websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'STOP_WEBRTC_CONN', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}