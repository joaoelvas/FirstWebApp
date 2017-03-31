var map;

var username = localStorage.getItem("username");

function initMap(){
    
    map = new google.maps.Map(document.getElementById('map'),
        {
            zoom: 8,
            center: {lat: 38.737, lng: -9.143}
        });
    
    $("#user-location").click(function(e) {
    
        $("#all-users-location").removeClass("active");
        $(this).addClass("active");
        
        var street = "";
        var local = "";
        var postalCode = "";
    
        var token = localStorage.getItem('tokenID');
    
        var data = {
            tokenID: token
        }
        $.ajax({
            type: "POST",
            url: "/rest/maps/address",
            contentType: "application/json; charset=utf-8",
            crossDomain: true,
            dataType: "json",
            data: JSON.stringify(data),
            success: function(response) {
                street = response.street;
                local = response.local;
                postalCode = response.postalCode;
                console.log(postalCode + " " + local);
                geocodeAddress(postalCode + " " + local);
            },
            error: function(response) {
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
    
    $("#all-users-location").click(function(e) {
        e.preventDefault();
        
        $("#user-location").removeClass("active");
        $(this).addClass("active");
    });
    
    function geocodeAddress(address) {
        var geocoder = new google.maps.Geocoder();
        geocoder.geocode({'address': address}, function(results, status) {
          if (status === 'OK') {
            map.setCenter(results[0].geometry.location);
            map.setZoom(16);
            marker = new google.maps.Marker({
              map: map,
              position: results[0].geometry.location
            });
          } else {
            alert('Geocode was not successful for the following reason: ' + status);
          }
        }); 
    }
}

$("#signed-user").text("Signed in as " + username);

$("#logout-link").click(function(e){
    e.preventDefault();
        
    var token = localStorage.getItem('tokenID');
    
    var data = {
        tokenID: token
    }
        
    $.ajax({
        type: "POST",
        url: "/rest/logout",
        contentType: "application/json; charset=utf-8",
        crossDomain: true,
        dataType: "json",
        data: JSON.stringify(data),
        success: function(response) {
            window.location.href = "../index.html"
            alert("Logged out")
        },
        error: function(response) {
            console.log("Status: "+ response.statusCode); 
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



