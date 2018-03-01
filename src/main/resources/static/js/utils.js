sap.ui.define([], function () {
        "use strict";
        jQuery.sap.require("sap.m.MessageBox");

        return {
            oBundle: null,
            _DELETE: 3,
            _NO_CHANGE: 0,
            _NEW: 1,
            _CHANGE: 2,
            currentUser: "",
            oPublicKey: "",
            oDisplayModel: null,
            oDeletedParams: [],
            isNewDashboard: true,

            setRsaPublicKey: function (publicKey) {
                this.oPublicKey = publicKey;
            },

            getRsaPublicKey: function () {
                return this.oPublicKey;
            },

            setCurrentUser: function (userId) {
                this.currentUser = userId;
            },
            getCurrentUser: function () {
                return this.currentUser;
            },

            getSessionUser: function(){
                var result = this.getServiceResponseInJson("../api/user/session");
                if(result.status == "true")
                    return result.message;
                else
                    return "";
            },

            isActiveSession: function(){
                var result = this.getServiceResponseInJson("../api/user/session");
                if(result.status == "true")
                    return true;
                else
                    return false;
            },

            generatePublicKey: function(){
                var url = "../api/rsa/publicKey";
                var result  = this.getServiceResponseInJson(url, "GET");
                return result.key;
            },

            getMessageBundle: function () {
                var locale = sap.ui.getCore().getConfiguration().getLocale().getLanguage();
                jQuery.sap.require("jquery.sap.resources");
                var bundle = jQuery.sap.resources({url: "i18n/i18n.properties", locale: locale});
                return bundle;
            },

            isObjectNotNull: function (object) {
                if (object !== undefined && object != null && object !== "")
                    return true;

                return false;
            },

            isValidUserId: function (userId) {
                var regex = new RegExp("^[IDC]*[0-9]{6}", "i");
                return regex.test(userId) && userId.length == 7;
            },



            showSuccessMessageBox: function (mesg) {
                sap.m.MessageBox.success(mesg, {
                    icon: sap.m.MessageBox.Icon.SUCCESS,
                    styleClass: "sapUiSizeCompact"
                });
            },

            showWarningMessageBox: function (mesg) {
                sap.m.MessageBox.warning(mesg, {
                    icon: sap.m.MessageBox.Icon.warning,
                    styleClass: "sapUiSizeCompact"
                });
            },

            showErrorMessageBox: function (mesg) {
                sap.m.MessageBox.error(mesg, {
                    icon: sap.m.MessageBox.Icon.ERROR,
                    styleClass: "sapUiSizeCompact"
                });
            },
            getServiceResponseInJson: function (strUrl, method, parameters) {
                var result = '';
                var that = this;
                var request;
                if(method ==undefined)
                    method = "GET";
                if(parameters!=undefined) {
                    parameters = JSON.stringify(parameters);
                    request = $.ajax({
                        url: strUrl,
                        dataType: 'json',
                        data: parameters,
                        cache: false,
                        type: method,
                        async: false,
                        contentType: 'application/json'
                    });
                }
                else
                {
                    request = $.ajax({
                        url: strUrl,
                        dataType: 'json',
                        cache: false,
                        type: method,
                        async: false,
                        contentType: 'application/json'
                    });
                }

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
                        that.showErrorMessageBox(that.oBundle.getText("m_requestFail"));
                        sap.ui.core.BusyIndicator.hide();
                    });
                return result;
            },

            validateServerInputs: function (obj) {
                var result = true;
                if (!(this.isObjectNotNull(obj.url) && this.isObjectNotNull(obj.userName) && this.isObjectNotNull(obj.password))) {
                    this.showWarningMessageBox(this.oBundle.getText("m_noServerInfo"));
                    result = false;
                }

                return result;
            },

            ping: function (params, serverName) {
                var serviceUrl = "../api/ping/" + serverName;
                var result = this.getServiceResponseInJson(serviceUrl, "POST",params);

            }


        }
    }
);



