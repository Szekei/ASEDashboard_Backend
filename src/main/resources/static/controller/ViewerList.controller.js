sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ase/wt/js/utils"
], function (Controller, utils) {
    "use strict";
    var ListController = Controller.extend("ase.wt.controller.ViewerList", {
        onInit: function () {
            jQuery.sap.require("sap.m.MessageBox");
            this._Component = this.getOwnerComponent();
            this._URL = "../api/access/viewer/";
            this._IS_SAVED = true;
            var oRouter = this._Component.getRouter();
            oRouter.getRoute("viewerManager").attachMatched(function (oEvent) {
                if(!utils.isActiveSession())
                    oRouter.navTo("login");
                this._ID = oEvent.getParameter("arguments").id;
                this._getDashboardUser(this._ID);
            }, this);
            var userModel = new sap.ui.model.json.JSONModel();
            //this._DashboardModel.setData(this.getDashboardListByUser());
            this.getView().setModel(userModel);
        },

        onExit: function () {
            this.oModel.destroy();
        },

        handlePressUser: function(oEvent){
            this._Component.handlePressUser(oEvent, false);
        },

        _getDashboardUser: function (dsId) {
            var data = this.getViewerList(dsId);
            this.getView().getModel().setData(data);
            this.getView().getModel().refresh(true);
        },

        getViewerList: function (dsId) {
            var url = this._URL + this._ID;
            return utils.getServiceResponseInJson(url, "GET");
        },

        onNavBack: function (oEvent) {
            var that = this;
            if(!utils.isObjectNotNull(utils.currentUser)){
                that._Component.getRouter().navTo("login",{});
            }
            this._Component.getRouter().navTo("dashboardManager", {
                userId: utils.currentUser
            });

        },

        handleAddViewer: function (oEvent) {
            var that = this;
            var newViewer = {
                viewerId: ""
            };
            var oData = that.getView().getModel().getData();
            if (oData == undefined || !jQuery.isArray(oData)) {
                oData = [];
                that.getView().getModel().setData(oData);
            }
            oData.push(newViewer);
            that.changeToEditMode();
            that.getView().getModel().refresh(true);
        },

        handleDeleteViewer: function (oEvent) {
            var oList = oEvent.getSource(),
                oItem = oEvent.getParameter("listItem"),
                deletedObject = oItem.getBindingContext().getObject();
            var userModel = this.getView().getModel(),
                userList = userModel.getData();
            for (var i = 0; i < userList.length; i++) {
                if (userList[i] == deletedObject) {
                    userList.splice(i, 1);
                    break;
                }
            }
            this.changeToEditMode();
            userModel.refresh(true);
        },

        onSearch: function (oEvent) {
            var that = this;
            var aFilters = [];
            var sQuery = oEvent.getSource().getValue();
            if (sQuery && sQuery.length > 0) {
                var filter = new sap.ui.model.Filter("userId", sap.ui.model.FilterOperator.Contains, sQuery);
                aFilters.push(filter);
            }

            // update list binding
            var list = that.getView().byId("dsUserList");
            var binding = list.getBinding("items");
            binding.filter(aFilters, "Application");
        },

        onSaveUser: function (oEvent) {
            var that = this;
            var isValidUserList = true;
            var data = that.getView().getModel().getData();
            var url = that._URL + that._ID;
            var params = [];
            data.forEach(function (obj) {
                if (utils.isValidUserId(obj.viewerId))
                    params.push({"dashboardId": that._ID, "viewerId": obj.viewerId});
                else {
                    utils.showErrorMessageBox(utils.oBundle.getText("m_invalidUserId"));
                    isValidUserList = false;
                }

            });
            if (!isValidUserList) {
                return;
            }
            var result = utils.getServiceResponseInJson(url, "POST", params);
            if (result.status == "Success") {
                that.changeToViewMode();
                var dialog = new sap.m.Dialog({
                    title: utils.oBundle.getText("title_success"),
                    type: "Message",
                    state: "Success",
                    content: new sap.m.Text({
                        text: utils.oBundle.getText("m_successSave")
                    }),
                    beginButton: new sap.m.Button({
                        text: utils.oBundle.getText("b_ok"),
                        press: function () {
                            dialog.close();
                            dialog.destroy();
                            that.onNavBack()
                        }
                    })
                });
                dialog.open();
            }
            else {
                utils.showErrorMessageBox(result.message);
            }
        },


        changeToEditMode: function (oEvent) {
            this._IS_SAVED = false;
            this.getView().byId("btn_cancelViewer").setEnabled(true);
            this.getView().byId("btn_saveViewer").setEnabled(true);
        },

        changeToViewMode: function (oEvent) {
            this._IS_SAVED = true;
            this.getView().byId("btn_cancelViewer").setEnabled(false);
            this.getView().byId("btn_saveViewer").setEnabled(false);
        },

        handlePressCancel: function (oEvent) {
            var that = this;
            if (!this._IS_SAVED) {
                sap.m.MessageBox.confirm(utils.oBundle.getText("m_cancelMessage"), {
                    styleClass: "sapUiSizeCompact",
                    actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
                    onClose: function (oAction) {
                        if (oAction === "OK") {
                            that.onNavBack();
                        }
                    }
                });
            }
            else
                that.onNavBack();
        }


    });
    return ListController;
});