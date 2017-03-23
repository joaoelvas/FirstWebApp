captureData = function(event) {
    var data = $('form[name="login"]').jsonify();
    console.log(data);
    $.ajax({
        type: "POST",
        url: "http://firstwebapp-159414.appspot.com/rest/login/v1",
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
    event.preventDefault();
};

registerData = function(event) {
    var data = $('form[name="register"]').jsonify();
    console.log(data);
    $.ajax({
        type: "POST",
        url: "http://firstwebapp-159414.appspot.com/rest/register/v3",
        contentType: "application/json; charset=utf-8",
        crossDomain: true,
        dataType: "json",
        success: function(response) {
            if(response.statusCode === 200) {
                alert("User was registered.");
            } else if(response.statusCode === 403) {
                alert("User already exists.");
            } else if(response.statusCode === 500) {
                alert("Internal Server Error");
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
}

window.onload = function() {
    var frms = $('form[name="login"]');     //var frms = document.getElementsByName("login");
    var frmr = $('form[name="register"]');
    frms[0].onsubmit = captureData;
    frmr[0].onsubmit = registerData;
}

