import { sendCandidateToPeer } from "./functions/webrtcFuncs.js";

$(document).ready(function () {
    const configIceServers = {
        iceServers: [
            {
                urls: 'stun:stun.l.google.com:19302'
            },

            {
                urls: 'turn:openrelay.metered.ca:443',
                username: 'openrelayproject',
                credential: 'openrelayproject'
            }
        ]
    }

    window.localPeerConnection = new RTCPeerConnection(configIceServers);

    window.localPeerConnection.onicecandidate = function(event) {
        if (event.candidate) {
            sendCandidateToPeer(event.candidate);
        }
    };

    window.localPeerConnection.ontrack = function (event) {
        const remoteStream = event.streams[0];

        const remoteVideoElement = $('#remote-camera').get(0);
        const remoteAudioElement = $('#remote-audio').get(0);

        remoteVideoElement.srcObject = remoteStream;
        remoteAudioElement.srcObject = remoteStream;

        $('.main-container').addClass('mb-2');

        $('.main-info-div').addClass('d-none');
        $('.main-remote-user-div').removeClass('d-none');

    }

    window.localPeerConnection.onconnectionstatechange = function (event) {
        switch (window.localPeerConnection.connectionState) {
            case 'connected':
                break;
            case 'disconnected':
            case "failed":
                $('.main-container').removeClass('mb-2');
                $('.main-info-div').removeClass('d-none');
                $('.main-remote-user-div').addClass('d-none');
                break;
            case "closed":
                break;
        }
    }

})