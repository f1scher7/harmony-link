import { createPeerConnection, sendCandidateToPeer } from "./functions/webrtcFuncs.js";
import { fetchUsersActivityStatus } from "./functions/utils.js";

$(document).ready(function () {

    const startBtn = $('#start-btn');
    const stopBtn = $('#stop-btn');
    const filtersBtn = $('#filters-btn');
    const statisticDataDiv = $('.statistics-data');
    const hlLogoInInfoDiv = $('.hlLogoInInfoDiv');

    createPeerConnection();

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
        switch (window.localPeerConnection.connectionState) {
            case 'connected':
                break;

            case 'disconnected':
            case "failed":
                window.localPeerConnection.close();
                window.localPeerConnection = null;

                createPeerConnection();


                $('.main-container').removeClass('mb-2');
                $('.main-info-div').removeClass('d-none');
                $('.main-remote-user-div').addClass('d-none');

                if (window.innerHeight > 1600) {
                    hlLogoInInfoDiv.removeClass('mb-5');
                    hlLogoInInfoDiv.addClass('mb-2');
                } else {
                    hlLogoInInfoDiv.removeClass('mb-2');
                    hlLogoInInfoDiv.addClass('mb-5');
                }

                statisticDataDiv.empty();
                statisticDataDiv.html(`
                        <div class="d-inline-block">
                            <h5 class="font-weight-bold" id="users-online"><i class="fas fa-users"></i> Aktualna liczba użytkowników online: </h5>
                            <h5 class="font-weight-bold" id="users-in-search"><i class="fas fa-sliders-h"></i> Ilość osób odpowiadających Twoim filtrom: </h5>
                            <h5 class="font-weight-bold" id="users-in-call"><i class="fa fa-filter"></i> Ilość użytkowników szukających Ciebie:</h5>
                        </div>
                    `);

                fetchUsersActivityStatus();

                window.userActivityInterval = setInterval(fetchUsersActivityStatus, 3000);

                stopBtn.addClass('disabled');
                startBtn.removeClass('disabled');
                filtersBtn.removeClass('disabled');

                break;

            case "closed":
                break;
        }
    }

})