import { searchCities, searchHobbies, searchStudies } from "./functions/userProfileFuncs.js";

$(document).ready(function () {

    function updateHobbyIdsField() {
        $('#hobbyValuesField').val(addedHobbies.join(","));
    }

    $('#set-profile-form').on('submit', function () {
        updateHobbyIdsField();
    })


    let addedHobbies = [];

    searchCities();
    searchStudies();
    searchHobbies(addedHobbies);

})