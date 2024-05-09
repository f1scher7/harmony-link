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

export function sendStopActivityByWebsocket(websocket) {
    if (websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'STOP_ACTIVITY', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}