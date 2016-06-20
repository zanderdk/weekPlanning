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

});

function signin() {
    var username = $("#username").val();
    var password = $("#password").val();
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
	var passowrd = $("#passwordReg").val();
	var email = $("#emailReg").val();
	var confirm = $("#confirmPasswordReg").val();
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
