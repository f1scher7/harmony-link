export function sendUserIdByWebsocket(websocket) {
    if (websocket && websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'USER_PROFILE_ID', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}

export function sendHeartbeatByWebsocket(websocket) {
    if (websocket && websocket && websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'HEARTBEAT_REQUEST' }));
    }
}

export function sendInSearchStatusByWebsocket(websocket) {
    if (websocket && websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'IN_SEARCH', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}

export function sendStopSearchingByWebsocket(websocket) {
    if (websocket && websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({ type: 'STOP_SEARCHING', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}

export function sendStopWebRTCConn(websocket) {
    if (websocket && (websocket.readyState === WebSocket.OPEN || websocket.readyState === WebSocket.CLOSING)) {
        websocket.send(JSON.stringify({ type: 'STOP_WEBRTC_CONN', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}

export function sendWebRTCConnectionError(websocket) {
    if (websocket && (websocket.readyState === WebSocket.OPEN || websocket.readyState === WebSocket.CLOSING)) {
        websocket.send(JSON.stringify({ type: 'WEBRTC_CONN_ERROR', userProfileId: window.userProfileId }));
    } else {
        console.error("WebSocket is not connected");
    }
}


export function sendGetTalkerNickname(websocket) {
    if (websocket && (websocket.readyState === WebSocket.OPEN || websocket.readyState === WebSocket.CLOSING)) {
        websocket.send(JSON.stringify({ type: 'GET_TALKER_NICKNAME', userProfileId: window.userProfileId }))
    } else {
        console.error("WebSocket is not connected");
    }
}

export function sendTextMess(websocket, mess, to, from) {
    if (websocket && (websocket.readyState === WebSocket.OPEN || websocket.readyState === WebSocket.CLOSING)) {
        websocket.send(JSON.stringify({ type: 'SEND_TEXT_MESS_TO_SERVER', mess: mess, to: to, from: from }));
    } else {
        console.error("WebSocket is not connected");
    }
}