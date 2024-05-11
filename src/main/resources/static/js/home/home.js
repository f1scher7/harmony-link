import { adjustMainContainer, checkCameraStatus, fetchUsersActivityStatus } from './functions/utils.js';
import { sendUserIdByWebsocket, sendHearBeatByWebsocket } from './functions/websocketFuncs.js';
import { handleVideoOfferMsg, handleVideoAnswerMsg, handleNewICECandidateMsg } from "./functions/webrtcFuncs.js";

$(document).ready(function (url, data) {

    $(window).on('load resize', adjustMainContainer);

    window.userProfileId = $('#user-profile-id').text();


    const wsUri = "ws://localhost:8080/harmony-websocket-handler";
    window.websocket = new WebSocket(wsUri);

    window.websocket.onopen = function (event) {
        console.log("Connected to Websocket server");

        sendUserIdByWebsocket(window.websocket);
    }

    window.websocket.onmessage = function (event) {
        const message = JSON.parse(event.data);

        switch (message.type) {
            case 'VIDEO_OFFER':
                handleVideoOfferMsg(message);
                break;
            case 'VIDEO_ANSWER':
                handleVideoAnswerMsg(message);
                break;
            case 'NEW_ICE_CANDIDATE':
                handleNewICECandidateMsg(message);
                break;
        }
    }

    window.websocket.onclose = function (event) {
        console.log("Disconnected from WebSocket server");
    }

    window.websocket.onerror = function(event) {
        console.error("WebSocket error: " + event);
    };

    setInterval(() => sendHearBeatByWebsocket(window.websocket), 30000);


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
            $('#user-camera-div').addClass('info-div');
            $('#local-camera').remove();
            $('#camera-error').removeClass('d-none');
            $('#start-btn').addClass('disabled');
            $('#filters-btn').addClass('disabled')
        });

    checkCameraStatus(localVideoElement);
    localVideoElement.onplaying = (()=> checkCameraStatus(localVideoElement));


    fetchUsersActivityStatus()
    window.userActivityInterval = setInterval(fetchUsersActivityStatus, 3000);

})
