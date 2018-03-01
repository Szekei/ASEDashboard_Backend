function login() {
    var params = {
        sapId: $('#l_id').val(),
        password: $.md5($('#l_pwd').val())
    };
    var result = this.getServiceResponseInJson('../api/login', 'POST', params);
    if (result.status == "Success") {
        alert("Login Successfully");
        window.location.replace("");
        return true;
    }
    else if (result.status == "Fail") {
        alert(result.message);
    }
    return false;
}

function register() {
    var params = {
        userName: $('#n_name').val(),
        sapId: $('#n_id').val(),
        password: $.md5($('#n_pwd').val()),
        email: $('#n_mail').val()
    };
    var isDuplicate = this.getServiceResponseInJson('../api/user/checkSapId/' + $('#n_id').val(), 'GET');
    if (isDuplicate.status == "false") {
        var result = this.getServiceResponseInJson('../api/user', 'POST', params);
        if(result.status=="Success"){
            alert("Register successfully, please login.");
        }
        else if (result.status=="Fail"){
            alert(result.message);
        }
    }
    else if (isDuplicate.status == "true") {
        alert("Duplicate User ID");
        return false;
    }
}

function checkDuplicate() {
    var params = {sapId: $('#n_id').value};
    var result = this.getServiceResponseInJson('', 'GET', params);
}

$('.toggle').click(function () {
    // Switches the Icon
    $(this).children('i').toggleClass('fa-pencil');
    // Switches the forms
    $('.form').animate({
        height: "toggle",
        'padding-top': 'toggle',
        'padding-bottom': 'toggle',
        opacity: "toggle"
    }, "slow");
});


function getServiceResponseInJson(strUrl, method, parameters) {
    var result = '';
    var that = this;
    parameters = JSON.stringify(parameters);
    var request = $.ajax({
        url: strUrl,
        dataType: 'json',
        data: parameters,
        cache: false,
        type: method,
        async: false,
        contentType: 'application/json'
    });
    request.done(function (data, textStatus, xmlhttprequest) {
        try {

            result = data;
        } catch (err) {
            //Hide BusyIndicator if it is being shown
            sap.ui.core.BusyIndicator.hide();
            var messageText = err.message;
            that.showErrorMessageBox(messageText);
            //throw err;
        }
        //Hide BusyIndicator if it is being shown
        sap.ui.core.BusyIndicator.hide();
    });
    request.fail(
        function (jqXHR, textStatus) {
            //Hide BusyIndicator if it is being shown
            that.showErrorMessageBox(oBundle.getText("m_requestFail"));
            sap.ui.core.BusyIndicator.hide();
        });
    return result;
}