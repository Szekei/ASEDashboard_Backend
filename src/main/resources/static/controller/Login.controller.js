sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ase/wt/js/utils"
], function (Controller, utils) {
    "use strict";
    return Controller.extend("ase.wt.controller.Login", {
        onInit: function () {
            this._Component = this.getOwnerComponent();
            this._Component.getRouter().getRoute("login").attachMatched(function (oEvent) {
                this.clearForm();
                var userId = utils.getSessionUser();
                if(utils.isObjectNotNull(userId)){
                    this.navToNextPage(userId);
                }
            }, this);

        },

        onBeforeRendering: function(){

        },
        onPressRegister: function(oEvent){
           var navContainer = this.getView().byId("navContainer");
           navContainer.to(this.getView().byId("registerForm"), "slide")
        },

        onPressHaveAccount: function(){
            var navContainer = this.getView().byId("navContainer");
            navContainer.to(this.getView().byId("loginForm"), "slide")
        },

        clearForm: function(){
            this.getView().byId("l_id").setValue("");
            this.getView().byId("l_pwd").setValue("");

            this.getView().byId("n_id").setValue("");
            this.getView().byId("n_pwd").setValue("");
            this.getView().byId("n_mail").setValue("");
            this.getView().byId("n_name").setValue("");
        },


        handleLogin: function (oEvent) {
            var id = this.getView().byId("l_id").getValue();
            if (!utils.isObjectNotNull(id) || !utils.isObjectNotNull(this.getView().byId("l_pwd").getValue())) {
                utils.showErrorMessageBox(utils.oBundle.getText("m_requiedFieldsMissed"));
                return;
            }
            if (!utils.isValidUserId(id)) {
                utils.showErrorMessageBox(utils.oBundle.getText("m_invalidUserId"));
                return;
            }
            var pwd = $.md5(this.getView().byId("l_pwd").getValue());
            var params = {
                sapId: id,
                password: pwd
            };
            var result = utils.getServiceResponseInJson('../api/login', 'POST', params);
            if (result.status == "Success") {
                sap.m.MessageToast.show(utils.oBundle.getText("m_loginSuccess"));
                this.navToNextPage(id);
            }
            else if (result.status == "Fail") {
                utils.showErrorMessageBox(result.message);
            }
        },

        navToNextPage:function(userId){
            var dsId = this.getActiveDashboardId(userId);
            utils.setCurrentUser(userId);
            if(utils.isObjectNotNull(dsId)) {
                this._Component.getRouter().navTo("dashboard", {
                    id: dsId,
                    userId: userId
                });
            }
            else{
                this._Component.getRouter().navTo("dashboardManager", {
                    userId: userId
                });
            }
        },

        getActiveDashboardId: function(userId){
            var url="../api/dashboard/active/"+userId;
            var result = utils.getServiceResponseInJson(url,"GET");
            if(result.id == undefined){
                this._Component.getRouter().navTo("dashboardManager", {
                    userId: userId
                });
            }
            else {
                return result.id;
            }
        },

        handleRegister: function (oEvent) {
            var id = this.getView().byId("n_id").getValue();
            var mail = this.getView().byId("n_mail").getValue();
            var name = this.getView().byId("n_name").getValue();

            if (!utils.isObjectNotNull(name)||!utils.isObjectNotNull(mail) || !utils.isObjectNotNull(id) ||!utils.isObjectNotNull(this.getView().byId("n_pwd").getValue() )) {
                utils.showErrorMessageBox(utils.oBundle.getText("m_requiedFieldsMissed"));
                return;
            }
            if (!utils.isValidUserId(id)) {
                utils.showErrorMessageBox(utils.oBundle.getText("m_invalidUserId"));
                return;
            }

            var mailregex = /^\w+[\w-+\.]*\@\w+([-\.]\w+)*\.[a-zA-Z]{2,}$/;
            if (!mail.match(mailregex)) {
                utils.showErrorMessageBox(utils.oBundle.getText("m_invalidMailAddress"));
                return;
            }
            var params = {
                userName: this.getView().byId("n_name").getValue(),
                sapId: id,
                password: $.md5(this.getView().byId("n_pwd").getValue()),
                email: mail
            };
            var isDuplicate = utils.getServiceResponseInJson('../api/user/checkSapId/' + id, 'GET');
            if (isDuplicate.status == "false") {
                var result = utils.getServiceResponseInJson('../api/user', 'POST', params);
                if (result.status == "Success") {
                    utils.showSuccessMessageBox(utils.oBundle.getText("m_registerSuccess"));
                    this.onPressHaveAccount();
                    this.clearForm();
                }
                else if (result.status == "Fail") {
                    utils.showErrorMessageBox(result.message);
                }
            }
            else if (isDuplicate.status == "true") {
                utils.showWarningMessageBox(utils.oBundle.getText("m_duplicateUserId"));
            }
        }
    });
});
