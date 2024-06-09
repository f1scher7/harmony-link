import { fetchUsersActivityStatus, setDefaultInfoDiv, setMainInfoDivAfterCall } from "../functions/utils.js";
import { sendInSearchStatusByWebsocket, sendStopSearchingByWebsocket, sendStopWebRTCConn } from "../functions/websocketFuncs.js";
import { createPeerConnection } from "../functions/webrtcFuncs.js";

$(document).ready(function () {

    let mainInfoDiv = $('.main-info-div');
    let hlLogoInInfoDiv = $('.hlLogoInInfoDiv');
    let statisticDataDiv = $('.statistics-data');
    let startBtn = $('#start-btn');
    let stopBtn = $('#stop-btn');
    let filtersBtn = $('#filters-btn');


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
                    <h5 class="font-weight-bold mt-4 text-center" style="font-size: 1.3vw !important;">Wyszukujemy idealnego rozmówcy dla Ciebie...</h5>
                </div>
                <div class="w-50 text-center mx-auto mt-2 mb-3">
                    <img src="/img/logo/blueLinkLogoWithoutBg.png" class="img-fluid w-25 searching-animation" alt="" oncontextmenu="return false;">
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
                $('.chat-div').addClass('d-none');
                $('.terms-short-desc-div').removeClass('d-none');
                $('.chat-col .w-100.clearfix').remove();

                sendStopWebRTCConn(window.websocket);

                window.localPeerConnection.close();
                window.localPeerConnection = null;

                createPeerConnection(hlLogoInInfoDiv, statisticDataDiv, startBtn, stopBtn, filtersBtn);
                setMainInfoDivAfterCall(mainInfoDiv);
        }

        setDefaultInfoDiv(hlLogoInInfoDiv, statisticDataDiv, startBtn, stopBtn, filtersBtn);

        fetchUsersActivityStatus();
        window.userActivityInterval = setInterval(fetchUsersActivityStatus, 10000);
    })

})