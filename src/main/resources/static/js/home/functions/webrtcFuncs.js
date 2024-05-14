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
