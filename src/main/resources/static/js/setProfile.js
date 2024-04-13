$(document).ready(function () {

    function updateHobbyIdsField() {
        document.getElementById('hobbyValuesField').value = addedHobbies.join(",");
    }

    $('#set-profile-form').on('submit', function () {
        updateHobbyIdsField();
    })

    $('#city').on('input', function () {
        let prefix = $(this).val();
        $.ajax({
            url: '/cities',
            data: {prefix: prefix},
            success: function (data) {
                let dataList = $("#cityList");
                dataList.empty();

                $.each(data, function (index, city) {
                    dataList.append($("<option>").val(city))
                })
            }
        })
    })

    var addedHobbies = [];
    $('#hobbies').on('input', function () {
        let prefix = $(this).val();
        let dataList = $("#hobbyList");
        let selectedHobby = $(this).val();
        let options = dataList.find('option').map(function() {
            return $(this).val();
        }).get();

        if (prefix) {
            $.ajax({
                url: '/hobbies',
                data: {prefix: prefix},
                success: function (data) {
                    dataList.empty();
                    $.each(data, function (index, hobby) {
                        dataList.append($("<option>").val(hobby));
                    });
                }
            });
        }

        if (options.includes(selectedHobby) && !addedHobbies.includes(prefix)) {
            addedHobbies.push(prefix);
            let hobbyDiv = $('<div>').addClass('selected-hobby badge mx-1 d-inline-flex align-items-center').text(selectedHobby);
            let removeBtn = $('<span>').addClass('remove-hobby ms-1 bi bi-x').css({
                'cursor': 'pointer',
                'font-size': '1rem'
            });

            removeBtn.on('click', function () {
                addedHobbies = addedHobbies.filter(function(hobby) {
                    return hobby !== selectedHobby;
                });
                hobbyDiv.remove();
            });

            hobbyDiv.append(removeBtn);
            $('#selectedHobbies').append(hobbyDiv);
            $(this).val('');
        }
    });

})