import { fetchUsersActivityStatus, setDefaultInfoDiv, setMainInfoDivAfterCall } from "./utils.js";
import { sendWebRTCConnectionError } from "./websocketFuncs.js";

let mainInfoDiv = $('.main-info-div');

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

    try {
        window.localPeerConnection = new RTCPeerConnection(configIceServers);
    } catch (error) {
        displayWebRTCConnectionErrorModal(error);
    }

    navigator.mediaDevices.getUserMedia( {video: true, audio: true})
        .then(stream => {
            stream.getTracks().forEach(track => {
                window.localPeerConnection.addTrack(track, stream);
            })
        })
        .catch(error => {
            displayWebRTCConnectionErrorModal(error);
        })

    window.localPeerConnection.onicecandidate = function(event) {
        try {
            if (event.candidate) {
                sendCandidateToPeer(event.candidate);
            }
        } catch (error) {
            displayWebRTCConnectionErrorModal(error);
        }
    };

    window.localPeerConnection.ontrack = function (event) {
        mainInfoDiv.empty();
        mainInfoDiv.html(`
            <div id="user-remote-camera-div" class="embed-responsive embed-responsive-16by9 d-flex w-100 h-100">
                <video id="remote-camera" class="w-100 h-100 embed-responsive-item remote-user-camera" autoplay controls></video>
                <audio id="remote-audio" autoplay></audio>
            </div>
        `);

        const remoteStream = event.streams[0];
        const remoteVideoElement = $('#remote-camera').get(0);
        const remoteAudioElement = $('#remote-audio').get(0);

        remoteVideoElement.srcObject = remoteStream;
        remoteAudioElement.srcObject = remoteStream;
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

                setMainInfoDivAfterCall(mainInfoDiv);
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
    try {
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
                displayWebRTCConnectionErrorModal(error);
            });
    } catch (error) {
        displayWebRTCConnectionErrorModal(error);
    }
}


export function sendCandidateToPeer(candidate) {
    const candidateMsg = {
        type: 'candidate',
        candidate: candidate
    };

    try {
        window.websocket.send(JSON.stringify(candidateMsg));
    } catch (error) {
        displayWebRTCConnectionErrorModal(error);
    }
}


export function handleVideoOfferMsg(msg) {
    const remoteDesc = new RTCSessionDescription({
        type: msg.type,
        sdp: msg.sdp
    });

    try {
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
                displayWebRTCConnectionErrorModal(error);
            });
    } catch (error) {
        displayWebRTCConnectionErrorModal(error);
    }
}


export function handleVideoAnswerMsg(msg) {
    const sdpData = {
        type: 'answer',
        sdp: msg.sdp
    };

    try {
        const remoteDesc = new RTCSessionDescription(sdpData);
        window.localPeerConnection.setRemoteDescription(remoteDesc)
            .catch(error => {
                displayWebRTCConnectionErrorModal(error);
            });
    } catch (error) {
        displayWebRTCConnectionErrorModal(error);
    }
}


export function handleNewICECandidateMsg(msg) {
    try {
        const candidateData = {
            candidate: msg.candidate.candidate.candidate,
            sdpMid: msg.candidate.candidate.sdpMid,
            sdpMLineIndex: msg.candidate.candidate.sdpMLineIndex
        };

        const candidate = new RTCIceCandidate(candidateData);

        window.localPeerConnection.addIceCandidate(candidate)
            .catch(error => {
                displayWebRTCConnectionErrorModal(error);
            })
    } catch (error) {
        displayWebRTCConnectionErrorModal(error);
    }
}

export function displayWebRTCConnectionErrorModal(error) {
    sendWebRTCConnectionError(window.websocket);

    window.location.reload()

    sessionStorage.setItem('showWebRTCConnectionErrorModal', 'true');

    console.error('Error during handleVideoAnswerMsg:', error);
}
