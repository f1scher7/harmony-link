import { createPeerConnection } from "./functions/webrtcFuncs.js";

$(document).ready(function () {

    const hlLogoInInfoDiv = $('.hlLogoInInfoDiv');
    const statisticDataDiv = $('.statistics-data');
    const startBtn = $('#start-btn');
    const stopBtn = $('#stop-btn');
    const filtersBtn = $('#filters-btn');

    createPeerConnection(hlLogoInInfoDiv, statisticDataDiv, startBtn, stopBtn, filtersBtn);

})