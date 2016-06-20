!function($) {
    "use strict";

    $(document).ready(function() {

        $('#addUserForm').submit(function(event){
            event.preventDefault();

            var selectedRole = $('#inputRole').find('option:selected');

            var role = {};
            role.name = selectedRole.val();
            role.description = selectedRole.text();

            var user = {};
            user.firstName = $('#inputFirstName').val();
            user.lastName = $('#inputLastName').val();
            user.email = $('#inputEmail').val();
            user.role = role;
            user.status = $('#inputStatus').val();

            $.ajax({
                type: 'POST',
                url: '/rest/users',
                data: JSON.stringify(user),
                processData: false,
                contentType: 'application/json; charset=utf-8',
                headers: {'X-CSRF-TOKEN': csrfToken},
                error: function(error) {
                    $('#addUserModal').modal('hide');
                    swal({
                        title: "Something went wrong!",
                        text: "We were not able to add your new user. Please contact your administrator for help.",
                        type: "error",
                        showCancelButton: false,
                        confirmButtonClass: 'btn-danger waves-effect waves-light',
                        confirmButtonText: 'Close'
                    });
                },
                success: function(data) {
                    $('#inputFirstName').val("");
                    $('#inputLastName').val("");
                    $('#inputEmail').val("");
                    $('#inputRole')[0].selectedIndex = 0;
                    $('#inputStatus')[0].selectedIndex = 0;
                    $('#addUserModal').modal('hide');
                    $('#user-list').DataTable().row.add(data).draw();
                    swal(data.name + " added successfully!", "", "success")
                }
            });

        });

        $('#editUserForm').submit(function(event){
            event.preventDefault();

            var selectedRole = $('#editRole').find('option:selected');

            var role = {};
            role.name = selectedRole.val();
            role.description = selectedRole.text();

            var user = {};
            user.id = $('#editId').val();
            user.firstName = $('#editFirstName').val();
            user.lastName = $('#editLastName').val();
            user.email = $('#editEmail').val();
            user.role = role;
            user.status = $('#editStatus').val();

            $.ajax({
                type: 'POST',
                url: '/rest/user',
                data: JSON.stringify(user),
                processData: false,
                contentType: 'application/json; charset=utf-8',
                headers: {'X-CSRF-TOKEN': csrfToken},
                error: function(error) {
                    $('#editUserModal').modal('hide');
                    swal({
                        title: "Something went wrong!",
                        text: "We were not able to edit your user. Please contact your administrator for help.",
                        type: "error",
                        showCancelButton: false,
                        confirmButtonClass: 'btn-danger waves-effect waves-light',
                        confirmButtonText: 'Close'
                    });
                },
                success: function(data) {
                    var row = $('#user-list').DataTable().row('.edit-mode');
                    row.data(data).draw();
                    $('#editUserModal').modal('hide');
                    swal(data.name + " changes saved successfully!", "", "success");
                }
            });
        });

        var userTable = $('#user-list').DataTable({
            "ajax": {
                "url": "/rest/users",
                "type": "GET",
                "headers": {'X-CSRF-TOKEN': csrfToken},
                "dataSrc": "",
                "error": function(error) {alert(JSON.stringify(error));}
            },
            "columns": [
                {
                    "data": "name",
                    "className": "vertical-middle",
                    "render": function (data, type, row) {
                        return '<img class="img-circle user-img" src="' + row.icon + '"/> ' + data;
                    }
                },
                {
                    "data": "email",
                    "className": "vertical-middle"
                },
                {
                    "data": "role",
                    "className": "vertical-middle",
                    "render": function (data) {
                        return data.description;
                    }
                },
                {
                    "data": "status",
                    "className": "vertical-middle",
                    "render": function (data) {
                        return data == 'ENABLED' ? 'Active' : 'Inactive';
                    }
                },
                {
                    "className": "vertical-middle",
                    "render": function (data, type, row, meta) {
                        return '<button id="editUser-'+meta.row+'" class="btn btn-success btn-sm waves-effect waves-light m-r-5 edit-user-button">Edit User <i class="fa fa-pencil"></i></button>' +
                            '<button id="deleteUser-'+meta.row+'" class="btn btn-danger btn-sm waves-effect waves-light delete-user-button">Delete <i class="fa fa-trash"></i></button>';
                    }
                }
            ]
        });

        userTable.on('draw.dt', function() {

            $(".delete-user-button").click(function() {
                var tr = $(this).closest('tr');
                var row = $('#user-list').DataTable().row( tr );

                var user = {};
                user.id = row.data().id;

                swal({
                        title: "Are you sure?",
                        text: "This action will delete "+user.name+" from your system. and cannot be undone!",
                        type: "warning",
                        showCancelButton: true,
                        confirmButtonText: "Yes, delete user!",
                        confirmButtonColor: "#DD6B55",
                        closeOnConfirm: false
                    },
                    function(){
                        $.ajax({
                            type: 'DELETE',
                            url: '/rest/user',
                            data: JSON.stringify(user),
                            processData: false,
                            contentType: 'application/json; charset=utf-8',
                            headers: {'X-CSRF-TOKEN': csrfToken},
                            error: function(error) {
                                swal({
                                    title: "Something went wrong!",
                                    text: "We were not able to delete your user. Please contact your administrator for help.",
                                    type: "error",
                                    showCancelButton: false,
                                    confirmButtonClass: 'btn-danger waves-effect waves-light',
                                    confirmButtonText: 'Close'
                                });
                            },
                            success: function() {
                                swal(row.data().name + " deleted successfully!", "", "success");
                                row.remove().draw();
                            }
                        });
                    }
                );
            });

            $(".edit-user-button").click(function() {
                var tr = $(this).closest('tr');
                tr.siblings().removeClass('edit-mode');
                tr.addClass('edit-mode');

                var row = $('#user-list').DataTable().row( tr );
                $('#editId').val(row.data().id);
                $('#editFirstName').val(row.data().firstName);
                $('#editLastName').val(row.data().lastName);
                $('#editEmail').val(row.data().email);
                $('#editRole').val(row.data().role.name);
                $('#editStatus').val(row.data().status);
                $('#editUserModal').modal('show');
            })
        });
    } );

}(window.jQuery);
