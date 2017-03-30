var map;
function initMap()
{
    map = new google.maps.Map(document.getElementById('map'),
        {
            center: {lat:  38.659784, lng:  -9.202765},
            zoom: 16
        });
    
    var sala116 = new google.maps.LatLng(38.66104, -9.2032);
    
    var marker = new google.maps.Marker({
        position: sala116,
        map: map
    });
    
    var contentString = '<div id="content">'+
        '<h1 id="title">116-II</h1>'+
        '<p>A Sala 116-II é a sala onde decorrem as sessões de formação de APDC PEI</p>' +
        '<div id="media">'+
        '<img src="media/img/DSC_0001.JPG">'+
        '</div>';
    
    var infowindow = new google.maps.InfoWindow({content: contentString});
    
    marker.addListener('click', function() { infowindow.open(map, marker);});
        
}