import {fetchUsersActivityStatus, setDefaultInfoDiv} from "./utils.js";

export function createPeerConnection(hlLogoInInfoDiv, statisticDataDiv, startBtn, stopBtn, filtersBtn) {
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

    navigator.mediaDevices.getUserMedia( {video: true, audio: true})
        .then(stream => {
            stream.getTracks().forEach(track => {
                window.localPeerConnection.addTrack(track, stream);
            })
        })

    window.localPeerConnection.onicecandidate = function(event) {
        try {
            if (event.candidate) {
                sendCandidateToPeer(event.candidate);
            }
        } catch (error) {
            console.error("ICE error:", error);
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
        console.log(window.localPeerConnection.connectionState);
        switch (window.localPeerConnection.connectionState) {
            case 'connected':
                break;
            case 'connecting':
                break

            case 'disconnected':
            case "failed":
                $('#disconnectModal').modal('show');

                window.localPeerConnection.close();
                window.localPeerConnection = null;

                createPeerConnection(hlLogoInInfoDiv, statisticDataDiv, startBtn, stopBtn, filtersBtn);

                $('.main-container').removeClass('mb-2');
                $('.main-info-div').removeClass('d-none');
                $('.main-remote-user-div').addClass('d-none');

                setDefaultInfoDiv(hlLogoInInfoDiv, statisticDataDiv, startBtn, stopBtn, filtersBtn);

                fetchUsersActivityStatus();
                window.userActivityInterval = setInterval(fetchUsersActivityStatus, 3000);

                break;

            case "closed":
                break;
        }
    }
}


export function initiateOffer() {
    window.localPeerConnection.createOffer()
        .then(offer => {
            return window.localPeerConnection.setLocalDescription(offer);
        })
        .then(() => {
            window.websocket.send(JSON.stringify({
                type: 'offer',
                sdp: window.localPeerConnection.localDescription.sdp
            }));
        })
        .catch(error => {
            console.error("Error during offer creation:", error);
        });
}


export function sendCandidateToPeer(candidate) {
    const candidateMsg = {
        type: 'candidate',
        candidate: candidate
    };

    window.websocket.send(JSON.stringify(candidateMsg));
}


export function handleVideoOfferMsg(msg) {
    const remoteDesc = new RTCSessionDescription({
        type: msg.type,
        sdp: msg.sdp
    });

    window.localPeerConnection.setRemoteDescription(remoteDesc)
        .then(() => window.localPeerConnection.createAnswer())
        .then(answer => {
            return window.localPeerConnection.setLocalDescription(answer)
        })
        .then(() => {
            const answerMsg = {
                type: 'answer',
                sdp: window.localPeerConnection.localDescription.sdp
            };
            window.websocket.send(JSON.stringify(answerMsg));
        })
        .catch(error => {
            console.error('Error during handleVideoOfferMsg:', error);
        });
}


export function handleVideoAnswerMsg(msg) {
    const sdpData = {
        type: 'answer',
        sdp: msg.sdp
    };

    const remoteDesc = new RTCSessionDescription(sdpData);

    window.localPeerConnection.setRemoteDescription(remoteDesc)
        .catch(error => {
            console.error('Error during handleVideoAnswerMsg:', error);
        });
}


export function handleNewICECandidateMsg(msg) {
    const candidateData = {
        candidate: msg.candidate.candidate.candidate,
        sdpMid: msg.candidate.candidate.sdpMid,
        sdpMLineIndex: msg.candidate.candidate.sdpMLineIndex
    };

    const candidate = new RTCIceCandidate(candidateData);

    window.localPeerConnection.addIceCandidate(candidate)
        .catch(error => {
            console.error('Error during handleNewICECandidateMsg:', error);
        })
}
