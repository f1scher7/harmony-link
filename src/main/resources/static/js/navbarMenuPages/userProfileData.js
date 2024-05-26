import { addBadgesToDiv, searchCities, searchHobbies, searchStudies } from "../functions/userProfileFuncs.js";

$(document).ready(function () {

    if (displaySuccessModal === "1") {
        $('#successUserProfileModal').modal('show');
    }

    $('#user-profile-data-form').on('submit', function () {
        $('#sexHiddenField').val(sexProfileData);
        $('#hobbyValuesField').val(addedHobbies.join(","));
    })

    let addedHobbies = [...hobbiesProfileData];
    let selectedHobbies = $('#selectedHobbies');

    addBadgesToDiv(addedHobbies, selectedHobbies)

    if (sexProfileData === 'M') {
        $('#sex').val("Mężczyzna");
    } else {
        $('#sex').val("Kobieta");
    }

    let relationshipStatusSelect = $('#relationshipStatus');
    if (relationshipStatusProfileData === 'single') {
        relationshipStatusSelect.val('single')
    } else if (relationshipStatusProfileData === 'relationship') {
        relationshipStatusSelect.val('relationship')
    } else if (relationshipStatusProfileData === 'married') {
        relationshipStatusSelect.val('married')
    } else if (relationshipStatusProfileData === 'divorced') {
        relationshipStatusSelect.val('divorced')
    }

    searchCities();
    searchStudies();
    searchHobbies(addedHobbies);

})