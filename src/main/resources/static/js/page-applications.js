!function($) {
    "use strict";

    $('.permissionCheck').change(function() {
        var name = $(this).attr("name").split("@");
        var data = JSON.stringify({applicationName: name[1], roleName: name[0], permission: this.checked});
        $.ajax({
            type: "POST",
            url: 'rest/permission',
            data: data,
            headers: {'X-CSRF-TOKEN': csrfToken},
            contentType: 'application/json',
            success: function(data) {

            },
            error: function(error) {
                alert("An error occurred, pleas contact your system administrator");
            }
        });
    });

}(window.jQuery);
