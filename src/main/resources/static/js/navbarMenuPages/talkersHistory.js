$(document).ready(function () {

    let talkersHistoryMainDiv = $('.talkers-history-main-div');

    let talkersHistoryList = JSON.parse(talkersHistoryData);

    let indexForAnimation = 0;

    if (talkersHistoryList.length === 0) {
        talkersHistoryMainDiv.append($('<h5 class="text-center">Nie masz jeszcze ostatnich rozmówców</h5>'))
        talkersHistoryMainDiv.append($('<img src="/img/home/zeroTalkers.gif" class="img-fluid w-50 mx-auto d-block" alt="">'))
    }

    talkersHistoryList.forEach(function(userTalker) {
        let talkerDiv = $('<div class="d-flex w-75 p-3 mb-2 mx-auto border justify-content-between align-items-center"></div>');
        let rightPartDiv = $('<div class="d-flex"></div>')

        let nickname = $('<h6 class="mb-0 text-white" style="font-size: 1.1vw;"></h6>').text(userTalker.nickname);

        let parts = userTalker.localDateTime.split('T');
        let datePart = parts[0].split("-");
        let timePart = parts[1].split('.')[0];

        let formattedDateTime = timePart + ' ' + datePart[2] + "-" + datePart[1] + "-" + datePart[0];
        let localDateTime = $('<p class="text-white mb-0" style="font-size: 1.1vw;"></p>').text(formattedDateTime);

        let hobbiesString = userTalker.hobby.join(', ');

        let infoIcon = $('<i class="fas fa-info-circle ms-3" data-toggle="modal" data-target="talkerDataModal" data-nickname="' + userTalker.nickname + '"  data-city="' + userTalker.city + '" data-age="' + userTalker.age + '" data-sex="' + userTalker.sex + '" data-relationship-status="' + userTalker.relationshipStatus + '" data-hobby="' + hobbiesString + '" data-study="' + userTalker.study + '"></i>').css({
            'cursor': 'pointer',
            'color': 'white',
            'font-size': '1.1vw'
        });

        rightPartDiv.append(localDateTime).append(infoIcon);
        talkerDiv.append(nickname).append(rightPartDiv);

        talkerDiv.addClass('fadeIn');
        talkerDiv.css('animation-delay', (indexForAnimation * 0.5) + 's');

        indexForAnimation += 1;

        talkersHistoryMainDiv.append(talkerDiv);
    });

    talkersHistoryMainDiv.append($('<div class="mb-5"></div>'));

    talkersHistoryMainDiv.on('click', '.fa-info-circle', function () {
        let nickname = $(this).data('nickname');
        let city = $(this).data('city');
        let age = $(this).data('age');
        let sex = $(this).data('sex');
        let relationshipStatus = $(this).data('relationship-status');
        let hobby = $(this).data('hobby');
        let study = $(this).data('study');

        $('#talkerDataModalLabel').text('Dodatkowa informacja o ' + nickname);
        $('#talkerDataModal .modal-city').text(city);
        $('#talkerDataModal .modal-age').text(age);
        $('#talkerDataModal .modal-sex').text(sex);
        $('#talkerDataModal .modal-relationshipStatus').text(relationshipStatus);
        $('#talkerDataModal .modal-hobby').text(hobby);
        $('#talkerDataModal .modal-study').text(study);

        $('#talkerDataModal').modal('show');
    })
})