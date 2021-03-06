
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
                localStorage.setItem('username', response.username);
                window.location.href = "/dashboard";
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





window.onload = function() {
    var frms = $('form[name="login"]');     //var frms = document.getElementsByName("login");
    var frmr = $('form[name="register"]');
    frms[0].onsubmit = captureData;
    frmr[0].onsubmit = registerData;
    
    
    var token = localStorage.getItem('tokenID');
    
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
            window.location.href = "/dashboard";
        },
        error: function(response) {
            if(response.statusCode === 401) {
                window.location.href = "login.html";
                console.log("Session expired, please login again!");
            } else if(response.statusCode === 500) {
                console.log("Internal Server Error");
            } else if(response.statusCode === 403) {
                window.location.href = "login.html";
                console.log("No Active Session, please login!");
            } else {
                console.log("Error: "+ response.status); 
            }
        }
    });
}

