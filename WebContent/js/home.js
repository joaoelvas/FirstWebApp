window.onload = function() {
    $("#login-link").click(function(e) {
        e.preventDefault();
        
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
                window.location.href = "login.html";
                if(response.statusCode === 401) {
                    console.log("Session expired, please login again!");
                } else if(response.statusCode === 500) {
                    console.log("Internal Server Error");
                } else if(response.statusCode === 403) {
                    console.log("No Active Session, please login!");
                } else {
                    console.log("Error: "+ response.status); 
                }
            }
        });
    });
}