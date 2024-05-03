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


$(document).ready(function (url, data) {

    $(window).on('load resize', adjustMainContainer);


    const wsUri = "ws://localhost:8080/harmony-websocket";
    const websocket = new WebSocket(wsUri);

    websocket.onopen = function (event) {
        console.log("Connected to Websocket server");

        sendUserId($('#user-profile-id').text());
    }

    websocket.onmessage = function (event) {
        console.log("Received message: " + event.data);
    }

    websocket.onclose = function (event) {
        console.log("Disconnected from WebSocket server");
    }

    websocket.onerror = function(event) {
        console.error("WebSocket error: " + event);
    };

    function sendUserId(userProfileId) {
        if (websocket.readyState === WebSocket.OPEN) {
            websocket.send(JSON.stringify({ type: 'USER_PROFILE_ID', userProfileId: userProfileId }));
        } else {
            console.error("WebSocket is not connected");
        }
    }

    function sendHearBeat() {
        if (websocket.readyState === WebSocket.OPEN) {
            websocket.send(JSON.stringify( { type: 'HEARTBEAT_REQUEST'} ));
        }
    }

    setInterval(sendHearBeat, 30000);


    const videoElement = $('#user-camera').get(0);
    const audioElement = $('#user-audio').get(0);

    navigator.mediaDevices.getUserMedia( {video: true, audio: true})
        .then(stream => {
            videoElement.srcObject = stream;
            audioElement.srcObject = stream;
            $('#camera-error').addClass('d-none');
        })
        .catch(error => {
            console.error("Error accessing camera and microphone", error);
            $('#user-camera-div').addClass('info-div');
            $('#user-camera').remove();
            $('#camera-error').removeClass('d-none');
            $('#start-btn').addClass('disabled');
            //$('#filters-btn').addClass('disabled')
        });

    function checkCameraStatus () {
        if (videoElement && videoElement.srcObject) {
            let tracks = videoElement.srcObject.getTracks();
            if (tracks.length > 0) {
                $('#user-camera-div').removeClass('h-100');
            }
        } else {
            $('#user-camera-div').addClass('h-100')
        }
    }

    checkCameraStatus();
    videoElement.onplaying = checkCameraStatus;


    fetchUsersActivityStatus()
    setInterval(fetchUsersActivityStatus, 3000);

})
