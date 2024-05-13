export function adjustMainContainer() {
    if (window.innerWidth > 1550) {
        document.querySelector(".main-container").classList.add('container-fluid');
        document.querySelector(".main-container").classList.add('mt-5');
        document.querySelector('.main-container').classList.remove('container')
    } else {
        document.querySelector('.main-container').classList.remove('container-fluid');
        document.querySelector('.main-container').classList.remove('mt-5');
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