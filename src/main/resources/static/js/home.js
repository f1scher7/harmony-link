const videoElement = document.getElementById('user-camera');
navigator.mediaDevices.getUserMedia( {video: true})
    .then(stream => {
        videoElement.srcObject = stream;
    })
    .catch(error => {
        console.error("Error accessing camera", error);
    });