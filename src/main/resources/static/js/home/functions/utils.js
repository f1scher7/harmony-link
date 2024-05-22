export function adjustMainContainer() {
    if (window.innerWidth > 1550) {
        document.querySelector(".main-container").classList.add('container-fluid');
        document.querySelector('.main-container').classList.remove('container')
    } else {
        document.querySelector('.main-container').classList.remove('container-fluid');
        document.querySelector('.main-container').classList.add('container')
    }
}

export function checkCameraStatus (videoElement) {
    if (videoElement && videoElement.srcObject) {
        let tracks = videoElement.srcObject.getTracks();
        if (tracks.length > 0) {
            $('#user-local-camera-div').removeClass('h-100');
        }
    } else {
        $('#user-local-camera-div').addClass('h-100')
    }
}

export function fetchUsersActivityStatus() {
    $.ajax({
        url: '/users-activity-status',
        type: 'GET',
        success: function (data) {
            $('#users-online').html(`<i class="fas fa-users"></i> Aktualna liczba użytkowników online: ${data[0]}`);
            $('#users-in-search').html(`<i class="fas fa-sliders-h"></i> Ilość osób odpowiadających Twoim filtrom: ${data[1]}`);
            $('#users-in-call').html(`<i class="fa fa-filter"></i> Ilość użytkowników szukających Ciebie: ${data[2]}`);
        },
        error: function (error) {
            $('#users-online').html(`<i class="fas fa-users"></i> Aktualna liczba użytkowników online: error`);
            $('#users-in-search').html(`<i class="fas fa-sliders-h"></i> Ilość osób odpowiadających Twoim filtrom: error`);
            $('#users-in-call').html(`<i class="fa fa-filter"></i> Ilość użytkowników szukających Ciebie: error`);
        }
    })
}

export function setMainInfoDivAfterCall(mainInfoDiv) {
    mainInfoDiv.empty();
    mainInfoDiv.html(`
        <div class="info-div h-100 d-flex justify-content-center align-items-center">
           <div class="row">
               <div class="w-100 hlLogoInInfoDiv d-flex justify-content-center mt-3 mb-5">
                   <img src="/img/logo/coloredLogoHarmonyLink.png" class="img-fluid w-50" alt="" oncontextmenu="return false;">
               </div>
               <div class="col-12 statistics-data d-flex justify-content-center mt-5 mb-5">
                   <div class="d-inline-block">
                       <h5 class="font-weight-bold" id="users-online">
                       </h5>
                       <h5 class="font-weight-bold" id="users-in-search">
                       </h5>
                       <h5 class="font-weight-bold" id="users-in-call">
                       </h5>
                   </div>
               </div>
               <div class="col-md-12 mb-5 mt-4">
               </div>
               <div class="w-100 d-flex justify-content-center mt-5">
                   <div class="text-center w-25">
                       <img src="/img/logo/blueLogoFischer.png" class="img-fluid w-75" alt="" oncontextmenu="return false;">
                       <h6 class="text-center mt-2">2024</h6>
                   </div>
               </div>
           </div>
       </div>
    `);
}

export function setDefaultInfoDiv(hlLogoInInfoDiv, statisticDataDiv, startBtn, stopBtn, filtersBtn) {
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

    stopBtn.addClass('disabled');
    startBtn.removeClass('disabled');
    filtersBtn.removeClass('disabled');
}