export function addBadgesToDiv(addedBadges, selectedBadges) {
    addedBadges.forEach(function (badge) {
        let div = $('<div>').addClass('selected-badge badge mx-1 mt-1 d-inline-flex align-items-center').text(badge);
        let removeBtn = $('<span>').addClass('remove-hobby ms-1 bi bi-x').css({
            'cursor': 'pointer',
            'font-size': '1rem'
        });

        removeBtn.on('click', function () {
            let index = addedBadges.indexOf(badge);

            if (index !== -1) {
                addedBadges.splice(index, 1);
            }
            div.remove();
        });

        div.append(removeBtn);
        selectedBadges.append(div);
    })
}

export function searchCities() {
    $('#city').on('input', function () {
        let prefix = $(this).val();
        $.ajax({
            url: '/cities',
            data: {prefix: prefix},
            type: 'GET',
            success: function (data) {
                let dataList = $("#cityList");
                dataList.empty();

                $.each(data, function (index, city) {
                    dataList.append($("<option>").val(city));
                })
            }
        })
    })
}

export function searchHobbies(addedHobbies) {
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
            let hobbiesDiv = $('<div>').addClass('selected-hobby badge mx-1 d-inline-flex align-items-center').text(prefix);
            let removeBtn = $('<span>').addClass('remove-hobby ms-1 bi bi-x').css({
                'cursor': 'pointer',
                'font-size': '1rem'
            });

            removeBtn.on('click', function () {
                let index = addedHobbies.indexOf(prefix);

                if (index !== -1) {
                    addedHobbies.splice(index, 1);
                }
                hobbiesDiv.remove();
            });

            hobbiesDiv.append(removeBtn);
            $('#selectedHobbies').append(hobbiesDiv);
            $(this).val('');
        }
    });
}

export function searchStudies() {
    $('#study').on('input', function () {
        let prefix = $(this).val();
        $.ajax({
            url: '/studies',
            data: {prefix: prefix},
            type: 'GET',
            success: function (data) {
                let dataList = $('#educationList');
                dataList.empty();

                $.each(data, function (index, education) {
                    dataList.append($("<option>").val(education));
                })
            }
        })
    })
}