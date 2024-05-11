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

    window.localPeerConnection.createOffer()
        .then(offer => {
            return window.localPeerConnection.setLocalDescription(offer)
        })
        .then(() => {
            const offerMsg = {
                type: 'VIDEO_OFFER',
                sdp: window.localPeerConnection.localDescription
            };

            window.websocket.send(JSON.stringify(offerMsg));
        })
        .catch(error => {
            console.error('Error during offer creation:', error);
        })

})