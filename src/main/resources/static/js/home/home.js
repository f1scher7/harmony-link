import {
    adjustMainContainer,
    checkCameraStatus,
    displayReceivedMess,
    fetchUsersActivityStatus
} from '../functions/utils.js';

import { sendUserIdByWebsocket, sendHeartbeatByWebsocket, sendStopWebRTCConn } from '../functions/websocketFuncs.js';
import { initiateOffer, handleVideoOfferMsg, handleVideoAnswerMsg, handleNewICECandidateMsg } from "../functions/webrtcFuncs.js";


$(document).ready(function () {

    if (sessionStorage.getItem('showWebRTCConnectionErrorModal') === 'true') {
        $('#webRTCConnErrorModal').modal('show');

        sessionStorage.removeItem('showWebRTCConnectionErrorModal');
    }

    if (showHarmonyInfoModal !== null && showHarmonyInfoModal.toString() === 'true') {
        $('#harmonyInfoModal').modal('show');

    }

    showHarmonyInfoModal = 'false';


    $(window).on('load resize', adjustMainContainer);


    window.userProfileId = $('#user-profile-id').text();


    let heartbeatResponseCount = 0;

    let chatCol = $('.chat-col');
    let chatDiv = $('.chat-div');


    const wsUri = "wss://harmonylink.site/harmony-websocket-handler";
    window.websocket = new WebSocket(wsUri);

    window.websocket.onopen = function (event) {
        console.log("Connected to Websocket server");

        sendUserIdByWebsocket(window.websocket);
    }

    window.websocket.onmessage = function (event) {
        const message = JSON.parse(event.data);

        if (message.type === 'HEARTBEAT_RESPONSE') {
            heartbeatResponseCount++;

            if (heartbeatResponseCount === 2) {
                $('#inactivePageModal').modal('show');

                window.websocket.close();
                window.websocket = null;

                window.localPeerConnection.close();
                window.localPeerConnection = null;

                clearInterval(window.userActivityInterval);

                heartbeatResponseCount = 0;
            }
        } else {
            heartbeatResponseCount = 0;
        }

        switch (message.type) {
            case 'INITIATE_OFFER':
                initiateOffer();
                break;
            case 'offer':
                handleVideoOfferMsg(message);
                break;
            case 'answer':
                handleVideoAnswerMsg(message);
                break;
            case 'candidate':
                handleNewICECandidateMsg(message);
                break;
            case 'TALKER_NICKNAME':
                window.talkerNickname = message.nickname;
                $('#disconnectModalLabel').text(window.talkerNickname + " zakończył/a spotkanie");
                break;
            case 'SEND_MESSAGE_FOR_RECIPIENT':
                displayReceivedMess(chatCol, chatDiv, message.mess);
                break;
        }
    }

    window.websocket.onclose = function (event) {
        console.log("Disconnected from WebSocket server" + event);
    }

    window.websocket.onerror = function(event) {
        console.error("WebSocket error: " + event);
        $('#inactivePageModal').modal('show');
    };


    window.addEventListener('beforeunload', function () {
        if (window.websocket && window.localPeerConnection.connectionState === 'new') {
            window.websocket.close();
        } else if (window.websocket.readyState === WebSocket.OPEN && window.localPeerConnection.connectionState === 'connected') {
            sendStopWebRTCConn(window.websocket);
        }
    })


    setInterval(() => sendHeartbeatByWebsocket(window.websocket), 7000);


    window.localVideoElement = $('#local-camera').get(0);
    const localAudioElement = $('#local-audio').get(0);

    navigator.mediaDevices.getUserMedia( {video: true, audio: true})
        .then(stream => {
            window.localVideoElement.srcObject = stream;
            localAudioElement.srcObject = stream;

            $('#camera-error').addClass('d-none');
        })
        .catch(error => {
            console.error("Error accessing camera and microphone", error);

            $('#user-local-camera-div').addClass('info-div');
            $('#local-camera').remove();
            $('#camera-error').removeClass('d-none');
            $('#start-btn').addClass('disabled');
            $('#filters-btn').addClass('disabled')
        });

    checkCameraStatus(window.localVideoElement);
    window.localVideoElement.onplaying = (()=> checkCameraStatus(window.localVideoElement));


    fetchUsersActivityStatus()
    window.userActivityInterval = setInterval(fetchUsersActivityStatus, 10000);

})
