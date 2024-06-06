$(document).ready(function () {

    if (displayChangePasswordSuccessModal !== null && displayChangePasswordSuccessModal.toString() === "1") {
        $('#changePasswordSuccessModal').modal('show');
    }

    if (displayChangeEmailModal !== null && displayChangeEmailModal.toString() === "1") {
        $('#changeEmailModal').modal('show');
    }

    $('#deleteAccountBtn').on('click', function () {
        $('#deleteAccountModal').modal('show');
    })



})