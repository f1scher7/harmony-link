export function initiateOffer() {
    window.localPeerConnection.createOffer()
        .then(offer => {
            return window.localPeerConnection.setLocalDescription(offer);
        })
        .then(() => {
            window.websocket.send(JSON.stringify({
                type: 'VIDEO_OFFER',
                sdp: window.localPeerConnection.localDescription
            }));
        })
        .catch(error => {
            console.error("Error during offer creation:", error);
        });
}

export function sendCandidateToPeer(candidate) {
    const candidateMsg = {
        type: 'NEW_ICE_CANDIDATE',
        candidate: candidate
    };

    window.websocket.send(JSON.stringify(candidateMsg));
}


export function handleVideoOfferMsg(msg) {
    const desc = new RTCSessionDescription(msg.sdp);

    window.localPeerConnection.setRemoteDescription(desc)
        .then(() => window.localPeerConnection.createAnswer())
        .then(answer => window.localPeerConnection.setLocalDescription(answer))
        .then(() => {
            const answerMsg = {
                type: 'VIDEO_ANSWER',
                sdp: window.localPeerConnection.localDescription
            };
            window.websocket.send(JSON.stringify(answerMsg));
        })
        .catch(error => {
            console.error('Error during handleVideoOfferMsg:', error);
        });
}

export function handleVideoAnswerMsg(msg) {
    const desc = new RTCSessionDescription(msg.sdp);

    window.localPeerConnection.setRemoteDescription(desc)
        .catch(error => {
            console.error('Error during handleVideoAnswerMsg:', error);
        });
}

export function handleNewICECandidateMsg(msg) {
    const candidate = new RTCIceCandidate(msg.candidate);

    window.localPeerConnection.addIceCandidate(candidate)
        .catch(error => {
            console.error('Error during handleNewICECandidateMsg:', error);
        })
}
