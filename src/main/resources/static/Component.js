sap.ui.define([
    "sap/ui/core/UIComponent",
    "sap/ui/model/json/JSONModel",
    "sap/ui/model/resource/ResourceModel",
    "ase/wt/js/utils"
], function (UIComponent, JSONModel, ResourceModel, utils) {
    "use strict";
    return UIComponent.extend("ase.wt.Component", {
        metadata: {
            manifest: "json"
        },
        currentView: "",
        init: function () {
            jQuery.sap.require("sap.m.MessageBox");
            // call the init function of the parent
            UIComponent.prototype.init.apply(this, arguments);

            // set i18n model
            utils.oBundle = utils.getMessageBundle();
            var i18nModel = new ResourceModel({
                bundleName: "ase.wt.i18n.i18n"
            });
            this.setModel(i18nModel, "i18n");

            //crete the view based on the url/hash
            this.getRouter().initialize();
            utils.setRsaPublicKey(utils.generatePublicKey());
        },

        handlePressUser: function(oEvent, isDashboardPage){
            var that = this;
            var popover = new sap.m.Popover({
                showHeader: false,
                placement: sap.m.PlacementType.Bottom,
                content: [
                    new sap.m.Button({
                        text: utils.oBundle.getText("b_manage"),
                        icon: "sap-icon://action-settings",
                        type: sap.m.ButtonType.Transparent,
                        visible: isDashboardPage,
                        press: function () {
                            that.handlePressDashboardManagement(oEvent);
                        }
                    }),
                    new sap.m.Button({
                        text: utils.oBundle.getText("b_logout"),
                        icon: "sap-icon://log",
                        type: sap.m.ButtonType.Transparent,
                        press: function () {
                            that.handlePressLogout(oEvent)
                        }
                    })
                ]
            }).addStyleClass("sapMOTAPopover sapTntToolHeaderPopover");

            popover.openBy(oEvent.getSource());
        },

        handlePressLogout: function(){
            utils.setCurrentUser("");
            var url = "../api/user/logout";
            utils.getServiceResponseInJson(url);
            sap.m.MessageToast.show(utils.oBundle.getText("m_logoutSuccess"));
            this.getRouter().navTo("login",{});
        },

        handlePressDashboardManagement: function (oEvent) {
            var currentUser = utils.getSessionUser();
            if(utils.isObjectNotNull(currentUser)) {
                this.getRouter().navTo("dashboardManager", {
                    userId: currentUser
                });
            }
            else
                this.getRouter().navTo("login");
        }

    });

});