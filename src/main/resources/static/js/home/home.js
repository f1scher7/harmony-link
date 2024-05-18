import { adjustMainContainer, checkCameraStatus, fetchUsersActivityStatus } from './functions/utils.js';
import { sendUserIdByWebsocket, sendHeartbeatByWebsocket } from './functions/websocketFuncs.js';
import { initiateOffer, handleVideoOfferMsg, handleVideoAnswerMsg, handleNewICECandidateMsg } from "./functions/webrtcFuncs.js";

$(document).ready(function () {

    $(window).on('load resize', adjustMainContainer);

    window.userProfileId = $('#user-profile-id').text();

    let heartbeatResponseCount = 0;


    const wsUri = "wss://192.168.0.102:8443/harmony-websocket-handler";
    window.websocket = new WebSocket(wsUri);

    window.websocket.onopen = function (event) {
        console.log("Connected to Websocket server");

        sendUserIdByWebsocket(window.websocket);
    }

    window.websocket.onmessage = function (event) {
        console.log("Received message: " + event.data);

        const message = JSON.parse(event.data);

        if (message.type === 'HEARTBEAT_RESPONSE') {
            heartbeatResponseCount++;

            if (heartbeatResponseCount === 3) {
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
                $('#disconnectModalLabel').text(message.nickname + " zakończył/a spotkanie");
                break;
        }
    }

    window.websocket.onclose = function (event) {
        console.log("Disconnected from WebSocket server" + event);
    }

    window.websocket.onerror = function(event) {
        console.error("WebSocket error: " + event);
    };

    window.addEventListener('beforeunload', function () {
        if (window.websocket) {
            window.websocket.close();
        }
    })


    setInterval(() => sendHeartbeatByWebsocket(window.websocket), 3000);


    const localVideoElement = $('#local-camera').get(0);
    const localAudioElement = $('#local-audio').get(0);

    navigator.mediaDevices.getUserMedia( {video: true, audio: true})
        .then(stream => {
            localVideoElement.srcObject = stream;
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

    checkCameraStatus(localVideoElement);
    localVideoElement.onplaying = (()=> checkCameraStatus(localVideoElement));


    fetchUsersActivityStatus()
    window.userActivityInterval = setInterval(fetchUsersActivityStatus, 10000);

})
