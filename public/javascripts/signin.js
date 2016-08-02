$(function() {

	$('#login-form-link').click(function(e) {
		$("#login-form").delay(100).fadeIn(100);
		$("#register-form").fadeOut(100);
		$('#register-form-link').removeClass('active');
		$(this).addClass('active');
		e.preventDefault();
	});
	$('#register-form-link').click(function(e) {
		$("#register-form").delay(100).fadeIn(100);
		$("#login-form").fadeOut(100);
		$('#login-form-link').removeClass('active');
		$(this).addClass('active');
		e.preventDefault();
	});

    $("#username").keyup(function(event){
        if(event.keyCode == 13){
            $("#login-submit").click();
        }
    });

    $("#password").keyup(function(event){
        if(event.keyCode == 13){
            $("#login-submit").click();
        }
    });

    $("#usernameReg").keyup(function(event){
        if(event.keyCode == 13){
            $("#register-submit").click();
        }
    });

    $("#passwordReg").keyup(function(event){
        if(event.keyCode == 13){
            $("#register-submit").click();
        }
    });

    $("#emailReg").keyup(function(event){
        if(event.keyCode == 13){
            $("#register-submit").click();
        }
    });

    $("#confirmPasswordReg").keyup(function(event){
        if(event.keyCode == 13){
            $("#register-submit").click();
        }
    });

});

function signin() {
    var username = $("#username").val();
    var password = SHA512($("#password").val());
	var data = {"password": password, "username": username};
	$.post("/signinInitCheck", data).done(function (res) {
		if(res != "ok") {
			var x =  $("#errorLogin");
			x.show();
			x.text(res);
		}
		else {
			$("#dummyPassword").val(password);
			$("#dummyUsername").val(username);
			$("#dummyForm").submit();
		}
	});
}


function register() {
	var username = $("#usernameReg").val();
	var passowrd = SHA512($("#passwordReg").val());
	var email = $("#emailReg").val();
	var confirm = SHA512($("#confirmPasswordReg").val());
	var errorBox = $("#errorReg");
	if(passowrd != confirm) {
		errorBox.show();
		errorBox.text("Bekræft koden matcher ikke din kode.");
	}
	else {
		var data = {"username": username, "email": email};
		$.get("/checkUsername", data).done(function (res) {
			if(res != "ok") {
				errorBox.show();
				errorBox.text(res);
			} else {
				$("#dummyPasswordReg").val(passowrd);
				$("#dummyUsernameReg").val(username);
				$("#dummyEmailReg").val(email);
				$("#dummyFormReg").submit();
			}
		})
	}
}
