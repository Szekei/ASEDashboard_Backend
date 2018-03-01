sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ase/wt/js/utils"
], function (Controller, utils) {
    "use strict";
    return Controller.extend("ase.wt.controller.ConfigBcp", {
        onInit: function () {
        },

        handleAddUser: function (oEvent) {
            var newUser = "";
            var newList = [];
            var oModel = this.getView().getModel();
            var oData = oModel.getData().data;

            if (oData.bcpServer.projectMembers == undefined || !jQuery.isArray(oData.bcpServer.projectMembers)) {
                newList.push(newUser);
                oData.bcpServer.projectMembers = newList;
            }
            else {
                oData.bcpServer.projectMembers.push(newUser);
            }
            oModel.refresh(true);
        },

        handleDeleteUser: function (oEvent) {
            var oList = oEvent.getSource(),
                oItem = oEvent.getParameter("listItem"),
                deletedObject = oItem.getBindingContext().getObject();
            var userModel = this.getView().getModel();
            var userList = userModel.getProperty("/data/bcpServer/projectMembers");
            for (var i = 0; i < userList.length; i++) {
                if (userList[i] == deletedObject) {
                    userList.splice(i, 1);
                    break;
                }
            }
            userModel.refresh(true);
        }
    });
});