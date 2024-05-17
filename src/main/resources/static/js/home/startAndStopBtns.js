import { fetchUsersActivityStatus, setDefaultInfoDiv } from "./functions/utils.js";
import {
    sendGetTalkerNickname,
    sendInSearchStatusByWebsocket,
    sendStopSearchingByWebsocket,
    sendStopWebRTCConn
} from "./functions/websocketFuncs.js";
import { createPeerConnection } from "./functions/webrtcFuncs.js";

$(document).ready(function () {

    const hlLogoInInfoDiv = $('.hlLogoInInfoDiv');
    const statisticDataDiv = $('.statistics-data');
    const startBtn = $('#start-btn');
    const stopBtn = $('#stop-btn');
    const filtersBtn = $('#filters-btn');


    startBtn.on('click', function () {
        sendInSearchStatusByWebsocket(window.websocket);

        clearInterval(window.userActivityInterval);

        if (window.innerHeight > 1550) {
            hlLogoInInfoDiv.removeClass('mb-2');
            hlLogoInInfoDiv.addClass('mb-5');
        } else {
            hlLogoInInfoDiv.removeClass('mb-5');
            hlLogoInInfoDiv.addClass('mb-2');
        }

        statisticDataDiv.empty();
        statisticDataDiv.html(`
            <div class="d-flex flex-column justify-content-center">
                <div class="text-center w-100">
                    <h5 class="font-weight-bold mt-4 text-center">Wyszukujemy idealnego rozmówcy dla Ciebie...</h5>
                </div>
                <div class="w-50 text-center mx-auto mt-2 mb-3">
                    <img src="/img/logo/blueLinkLogoWithoutBg.png" class="w-25 searching-animation" alt="" oncontextmenu="return false;">
                </div>
            </div>
        `);

        startBtn.addClass('disabled');
        stopBtn.removeClass('disabled');
        filtersBtn.addClass('disabled');

    })


    stopBtn.on('click', function () {
        switch (window.localPeerConnection.connectionState) {
            case "new":
                sendStopSearchingByWebsocket(window.websocket)
                break;
            case "connected":
                sendGetTalkerNickname(window.websocket);
                sendStopWebRTCConn(window.websocket);

                window.localPeerConnection.close();
                window.localPeerConnection = null;

                createPeerConnection(hlLogoInInfoDiv, statisticDataDiv, startBtn, stopBtn, filtersBtn);

                $('.main-container').removeClass('mb-2');
                $('.main-info-div').removeClass('d-none');
                $('.main-remote-user-div').addClass('d-none');
        }

        setDefaultInfoDiv(hlLogoInInfoDiv, statisticDataDiv, startBtn, stopBtn, filtersBtn);

        fetchUsersActivityStatus();
        window.userActivityInterval = setInterval(fetchUsersActivityStatus, 10000);
    })

})