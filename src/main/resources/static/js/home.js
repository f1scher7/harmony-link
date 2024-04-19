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

window.onload = adjustMainContainer;
window.onresize = adjustMainContainer;


const videoElement = document.getElementById('user-camera');
const audioElement = document.getElementById('user-audio');

navigator.mediaDevices.getUserMedia( {video: true})
    .then(stream => {
        videoElement.srcObject = stream;
    })
    .catch(error => {
        console.error("Error accessing camera", error);
    });

navigator.mediaDevices.getUserMedia({audio: true})
    .then(stream => {
        audioElement.srcObject = stream;
    })
    .catch(error => {
       console.error("Error accessing microphone", error);
    });