import { fetchUsersActivityStatus } from "./functions/utils.js";
import { sendInSearchStatusByWebsocket, sendStopActivityByWebsocket } from "./functions/websocketFuncs.js";

$(document).ready(function () {

    let startBtn = $('#start-btn');
    let stopBtn = $('#stop-btn');
    let filtersBtn = $('#filters-btn');
    let statisticDataDiv = $('.statistics-data');
    let hlLogoInInfoDiv = $('.hlLogoInInfoDiv');

    startBtn.on('click', function () {
        sendInSearchStatusByWebsocket(window.websocket);

        clearInterval(window.userActivityInterval);

        startBtn.addClass('disabled');
        stopBtn.removeClass('disabled');
        filtersBtn.addClass('disabled');

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
    })

    stopBtn.on('click', function () {
        if (window.localPeerConnection) {
            switch (window.localPeerConnection.connectionState) {
                case "connected":
                    
            }
        } else {

            sendStopActivityByWebsocket(window.websocket)

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
        }
    })

})