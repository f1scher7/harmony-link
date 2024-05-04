$(document).ready(function () {

    var addedCities = [...cities];
    var addedHobbies = [...hobbies];
    var addedStudies = [...studies];

    var selectedCities = $('#selectedCities');
    var selectedHobbies = $('#selectedHobbies');
    var selectedStudies = $('#selectedStudies');

    const ageFromInput = $('#ageFrom');
    const ageToInput = $('#ageTo');
    const heightFromInput = $('#heightFrom');
    const heightToInput = $('#heightTo');


    $('#sex').val(sex);
    $('#relationshipStatus').val(relationshipStatus);
    ageFromInput.val(ages[0]);
    ageToInput.val(ages[1]);
    heightFromInput.val(heights[0]);
    heightToInput.val(heights[1]);

    addedCities.forEach(function (city) {
        let citiesDiv = $('<div>').addClass('selected-badge badge mx-1 d-inline-flex align-items-center').text(city);
        let removeBtn = $('<span>').addClass('remove-hobby ms-1 bi bi-x').css({
            'cursor': 'pointer',
            'font-size': '1rem'
        });

        removeBtn.on('click', function () {
            addedCities = addedCities.filter(function(cityForDelete) {
                return cityForDelete !== city;
            });
            citiesDiv.remove();
        });

        citiesDiv.append(removeBtn);
        selectedCities.append(citiesDiv);
    })

    addedHobbies.forEach(function (hobby) {
        let hobbiesDiv = $('<div>').addClass('selected-badge badge mx-1 d-inline-flex align-items-center').text(hobby);
        let removeBtn = $('<span>').addClass('remove-hobby ms-1 bi bi-x').css({
            'cursor': 'pointer',
            'font-size': '1rem'
        });

        removeBtn.on('click', function () {
            addedHobbies = addedHobbies.filter(function(hobbyForDelete) {
                return hobbyForDelete !== hobby;
            });
            hobbiesDiv.remove();
        });

        hobbiesDiv.append(removeBtn);
        selectedHobbies.append(hobbiesDiv);
    })

    addedStudies.forEach(function (study) {
        let studiesDiv = $('<div>').addClass('selected-badge badge mx-1 d-inline-flex align-items-center').text(study);
        let removeBtn = $('<span>').addClass('remove-hobby ms-1 bi bi-x').css({
            'cursor': 'pointer',
            'font-size': '1rem'
        });

        removeBtn.on('click', function () {
            addedStudies = addedStudies.filter(function(studyForDelete) {
                return studyForDelete !== study;
            });
            studiesDiv.remove();
        });

        studiesDiv.append(removeBtn);
        selectedStudies.append(studiesDiv);
    })


    ageFromInput.on('change', function () {
        if (parseInt($(this).val(), 10) < 16) {
            ageFromInput.val(16);
        } else if (parseInt($(this).val(), 10) > 87) {
            ageFromInput.val(87)
        }

        let minValue = parseInt($(this).val(), 10);

        if (parseInt(ageToInput.val(), 10) < minValue && minValue >= 16 && parseInt($(this).val(), 10) >= 16 && parseInt($(this).val(), 10) <= 87) {
            ageToInput.val(minValue);
        }
    })

    ageToInput.on('change', function () {
        if (parseInt($(this).val(), 10) > 87) {
            ageToInput.val(87)
        } else if (parseInt($(this).val(), 10) < 16) {
            ageToInput.val(16)
        }

        let maxValue = parseInt($(this).val(), 10);

        if (parseInt(ageFromInput.val(), 10) > maxValue && maxValue <= 87 && parseInt($(this).val(), 10) <= 87 && parseInt($(this).val(), 10) >= 16) {
            ageFromInput.val(maxValue);
        }
    })


    heightFromInput.on('change', function () {
        if (parseInt($(this).val(), 10) < 150) {
            heightFromInput.val(150);
        } else if (parseInt($(this).val(), 10) > 230) {
            heightFromInput.val(230)
        }

        let minValue = parseInt($(this).val(), 10);

        if (parseInt(heightToInput.val(), 10) < minValue && minValue >= 150 && parseInt($(this).val(), 10) >= 150 && parseInt($(this).val(), 10) <= 230) {
            heightToInput.val(minValue);
        }
    })

    heightToInput.on('change', function () {
        if (parseInt($(this).val(), 10) > 230) {
            heightToInput.val(230)
        } else if (parseInt($(this).val(), 10) < 150) {
            heightToInput.val(150)
        }

        let maxValue = parseInt($(this).val(), 10);

        if (parseInt(heightFromInput.val(), 10) > maxValue && maxValue <= 230 && parseInt($(this).val(), 10) <= 230 && parseInt($(this).val(), 10) >= 150) {
            ageFromInput.val(maxValue);
        }
    })



    $('#cities').on('input', function() {
        let prefix = $(this).val();
        let citiesList = $('#citiesList');
        let citiesOptions = citiesList.find('option').map(function () {
            return $(this).val();
        }).get();

        if (prefix) {
            $.ajax({
                url: '/cities',
                data: {prefix: prefix},
                type: 'GET',
                success: function (data) {
                    citiesList.empty();

                    $.each(data, function (index, city) {
                        citiesList.append($("<option>").val(city));
                    });
                }
            });
        }

        if (citiesOptions.includes(prefix) && !addedCities.includes(prefix)) {
            addedCities.push(prefix);
            let citiesDiv = $('<div>').addClass('selected-badge badge mx-1 d-inline-flex align-items-center').text(prefix);
            let removeBtn = $('<span>').addClass('remove-badge ms-1 bi bi-x').css({
                'cursor': 'pointer',
                'font-size': '1rem'
            });

            removeBtn.on('click', function () {
                addedCities = addedCities.filter(function(city) {
                    return city !== prefix;
                });
                citiesDiv.remove();
            });

            citiesDiv.append(removeBtn);
            selectedCities.append(citiesDiv);
            $(this).val('');
        }
    });

    $('#hobbies').on('input', function () {
        let prefix = $(this).val();
        let hobbiesList = $("#hobbiesList");
        let hobbiesOptions = hobbiesList.find('option').map(function() {
            return $(this).val();
        }).get();

        if (prefix) {
            $.ajax({
                url: '/hobbies',
                data: {prefix: prefix},
                type: 'GET',
                success: function (data) {
                    hobbiesList.empty();
                    $.each(data, function (index, hobby) {
                        hobbiesList.append($("<option>").val(hobby));
                    });
                }
            });
        }

        if (hobbiesOptions.includes(prefix) && !addedHobbies.includes(prefix)) {
            addedHobbies.push(prefix);
            let hobbiesDiv = $('<div>').addClass('selected-badge badge mx-1 d-inline-flex align-items-center').text(prefix);
            let removeBtn = $('<span>').addClass('remove-hobby ms-1 bi bi-x').css({
                'cursor': 'pointer',
                'font-size': '1rem'
            });

            removeBtn.on('click', function () {
                addedHobbies = addedHobbies.filter(function(hobby) {
                    return hobby !== prefix;
                });
                hobbiesDiv.remove();
            });

            hobbiesDiv.append(removeBtn);
            selectedHobbies.append(hobbiesDiv);
            $(this).val('');
        }
    });

    $('#studies').on('input', function() {
        let prefix = $(this).val();
        let studiesList = $('#studiesList');
        let studiesOptions = studiesList.find('option').map(function () {
            return $(this).val();
        }).get();

        if (prefix) {
            $.ajax({
                url: '/studies',
                data: {prefix: prefix},
                type: 'GET',
                success: function (data) {
                    studiesList.empty();

                    $.each(data, function (index, study) {
                        studiesList.append($("<option>").val(study));
                    });
                }
            });
        }

        if (studiesOptions.includes(prefix) && !addedStudies.includes(prefix)) {
            addedStudies.push(prefix);
            let studiesDiv = $('<div>').addClass('selected-badge badge mx-1 d-inline-flex align-items-center').text(prefix);
            let removeBtn = $('<span>').addClass('remove-badge ms-1 bi bi-x').css({
                'cursor': 'pointer',
                'font-size': '1rem'
            });

            removeBtn.on('click', function () {
                addedStudies = addedStudies.filter(function(study) {
                    return study !== prefix;
                });
                studiesDiv.remove();
            });

            studiesDiv.append(removeBtn);
            selectedStudies.append(studiesDiv);
            $(this).val('');
        }
    });


    $('.send-preferences').on('click', function () {
        let userProfileId = $('#user-profile-id').text();

        let selectedRelationshipStatus = $('#relationshipStatus').val();
        let selectedSex = $('#sex').val();

        let ageFrom = $('#ageFrom').val() || 16;
        let ageTo = $('#ageTo').val() || 87;
        let heightFrom = $('#heightFrom').val() || 150;
        let heightTo = $('#heightTo').val() || 230;

        let dataToSend = {
            userProfileId: userProfileId,
            relationshipStatus: selectedRelationshipStatus,
            sex: selectedSex,
            ages: [parseInt(ageFrom, 10), parseInt(ageTo, 10)],
            heights: [parseInt(heightFrom, 10), parseInt(heightTo, 10)],
            cities: addedCities,
            hobbies: addedHobbies,
            studies: addedStudies
        }

        $.ajax({
            url: '/user-preferences-data',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(dataToSend),

            success: function (data) {
                $('#save-preferences-success').removeClass('d-none');
                setTimeout(function() {
                    location.reload();
                }, 1000);

            },
            error: function (data) {
                console.log("Error while sending user preferences data");
            }
        })
    })

})