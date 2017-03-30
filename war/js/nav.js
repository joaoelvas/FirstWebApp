$(document).ready(function(){
    $("#nav-menu").html(
        '<div class="container-fluid">' +
            '<div class="navbar-header">' +
                '<a class="navbar-brand" href="#">APDC</a>' +
            '</div>' +
            '<ul class="nav navbar-nav">' +
                '<li><a href="../"><span class="glyphicon glyphicon-home"></span>  Home</a></li>' +
                '<li class="active"><a href="apdc.html"><span class="glyphicon glyphicon-briefcase"></span>  APDC</a></li>' +
            '</ul>' +
            '<ul class="nav navbar-nav navbar-right">' +
                '<li><a href="login.html" id="login-link"><span class="glyphicon glyphicon-log-in"></span>  Login</a></li>' +
                '<li><a href="" id="logout-link"><span class="glyphicon glyphicon-log-out"></span>  Logout</a></li>' +
            '</ul>' +
        '</div>');
});