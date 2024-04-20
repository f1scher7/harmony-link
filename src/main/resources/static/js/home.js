$(document).ready(function (url, data) {

    function adjustMainContainer() {
        if (window.innerWidth > 1600) {
            document.querySelector(".main-container").classList.add('container-fluid');
            document.querySelector(".main-container").classList.add('mt-5');
            document.querySelector('.main-container').classList.remove('container')
        } else {
            document.querySelector('.main-container').classList.remove('container-fluid');
            document.querySelector('.main-container').classList.remove('mt-5');
            document.querySelector('.main-container').classList.add('container')
        }
    }

    $(window).on('load resize', adjustMainContainer);


    const videoElement = $('#user-camera').get(0);
    const audioElement = $('#user-audio').get(0);

    navigator.mediaDevices.getUserMedia( {video: true, audio: true})
        .then(stream => {
            videoElement.srcObject = stream;
            audioElement.srcObject = stream
        })
        .catch(error => {
            console.error("Error accessing camera and microphone", error);
        });


    function fetchUsersActivityStatus() {
        $.ajax({
            url: '/users-activity-status',
            type: 'GET',
            success: function (data) {
                $('#users-online').html(`<i class="fas fa-users"></i> Liczba użytkowników online: ${data[0]}`);
                $('#users-in-search').html(`<i class="fas fa-search"></i> Liczba użytkowników szukających: ${data[1]}`);
                $('#users-in-call').html(`<i class="fas fa-comments"></i> Liczba użytkowników w rozmowie: ${data[2]}`);
            },
            error: function (error) {
                $('#users-online').html(`<i class="fas fa-users"></i> Liczba użytkowników online: null`);
                $('#users-in-search').html(`<i class="fas fa-search"></i> Liczba użytkowników szukających: null`);
                $('#users-in-call').html(`<i class="fas fa-comments"></i> Liczba użytkowników w rozmowie: null`);
            }
        })
    }

    fetchUsersActivityStatus()
    setInterval(fetchUsersActivityStatus, 3000);


    function reportUserStatusOffline (userProfileId) {
        const data = JSON.stringify({userProfileId: userProfileId});
        navigator.sendBeacon('/report-user-offline', new Blob([data], { type: 'application/json' }));

    }

    window.addEventListener('beforeunload', function (event) {
        let userProfileId = $('#user-profile-id').text();
        reportUserStatusOffline(userProfileId);
    });

})
