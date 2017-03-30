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


captureData = function(event) {
    var data = $('form[name="login"]').jsonify();
    console.log(data);
    $.ajax({
        type: "POST",
        url: "/rest/login",
        contentType: "application/json; charset=utf-8",
        crossDomain: true,
        dataType: "json",
        success: function(response) {
            if(response) {
                // alert("Got token with id: " + response.tokenID);
                // Store token id for later use in localStorage
                localStorage.setItem('tokenID', response.tokenID);
                window.location.href = "/private-area";
            } else {
                alert("No response");
            }
        },
        error: function(response) {
            alert("Error: "+ response.status);
        },
        data: JSON.stringify(data)
    });
    event.preventDefault();
};

registerData = function(event) {
    var data = $('form[name="register"]').jsonify();
    console.log(data);
    $.ajax({
        type: "POST",
        url: "/rest/register",
        contentType: "application/json; charset=utf-8",
        crossDomain: true,
        dataType: "json",
        success: function(response) {
            if(response.statusCode === 200) {
                alert("User was registered.");
            } else {
                alert("No response");
            }
        },
        error: function(response) {
            if(response.statusCode === 403) {
                alert("User already exists.");
            } else if(response.statusCode === 500) {
                alert("Internal Server Error");
            } else {
                alert("Error: "+ response.status);
            }
        },
        data: JSON.stringify(data)
    });
    event.preventDefault();
}

$('logout-link').click(function() {
        
        var data = {
            tokenID: token
        }
        
        $.ajax({
            type: "POST",
            url: "/rest/logout",
            contentType: "application/json; charset=utf-8",
            crossDomain: true,
            dataType: "json",
            success: function(response) {
                if(response) {
                    alert("Got token with id: " + response.tokenID);
                    // Store token id for later use in localStorage
                    localStorage.setItem('tokenID', response.tokenID);
                } else {
                    alert("No response");
                }
            },
            error: function(response) {
                alert("Error: "+ response.status);
            },
            data: JSON.stringify(data)
    });
});

window.onload = function() {
    var frms = $('form[name="login"]');     //var frms = document.getElementsByName("login");
    var frmr = $('form[name="register"]');
    frms[0].onsubmit = captureData;
    frmr[0].onsubmit = registerData;
    
    var token = localStorage.getItem('tokenID');
    
    $('#logout-link').hide();
    
    if(token === null) {
        
    } else {
        var data = {
            tokenID: token
        }
        
        $.ajax({
            type: "POST",
            url: "/rest/login/check",
            contentType: "application/json; charset=utf-8",
            crossDomain: true,
            dataType: "json",
            data: JSON.stringify(data),
            success: function(response) {
                $('login-link').hide();
                $('logout-link').show();
            },
            error: function(response) {
                if(response.statusCode === 401) {
                    alert("Session expired, please login again!");
                } else if(response.statusCode === 500) {
                    alert("Internal Server Error");
                } else if(response.statusCode === 403) {
                    alert("No Active Session, please login!");
                } else {
                    alert("Error: "+ response.status);   
                }
            }
        });    
    }

}

