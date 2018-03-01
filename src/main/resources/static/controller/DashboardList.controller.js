sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ase/wt/js/utils"
], function (Controller, utils) {
    "use strict";
    var ListController = Controller.extend("ase.wt.controller.DashboardList", {
        onInit: function () {
            jQuery.sap.require("sap.m.MessageBox");
            this._Component = this.getOwnerComponent();
            this._URL = "../api/dashboardList/";
            var oRouter = this._Component.getRouter();
            oRouter.getRoute("dashboardManager").attachMatched(function (oEvent) {
                this._USERID = oEvent.getParameter("arguments").userId;
                var sessionUser = utils.getSessionUser();
                if(!utils.isObjectNotNull(sessionUser)||this._USERID != sessionUser){
                    this._Component.getRouter().navTo("login");
                }
                this._selectDashboardsWithId(this._USERID);
            }, this);
            this._DashboardModel = new sap.ui.model.json.JSONModel();
            //this._DashboardModel.setData(this.getDashboardListByUser());
            this.getView().setModel(this._DashboardModel);
        },

        onExit: function () {
            this.oModel.destroy();
        },

        handlePressUser: function (oEvent) {
            this._Component.handlePressUser(oEvent, false);
        },

        _selectDashboardsWithId: function (userId) {
            var data = this.getDashboardListByUser(userId);
            this._DashboardModel.setData(data);
            this._DashboardModel.refresh(true);
        },

        reloadDashboardList: function () {
            var data = this.getDashboardListByUser(this._USERID);
            this._DashboardModel.setData(data);
            this._DashboardModel.refresh(true);
        },


        handleDeleteDashboard: function (oEvent) {
            var that = this;
            var source = oEvent.getSource();
            sap.m.MessageBox.confirm(utils.oBundle.getText("m_deleteDashboard"), {
                styleClass: "sapUiSizeCompact",
                actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
                onClose: function (oAction) {
                    if (oAction === "OK") {
                        var deletedId = source.getBindingContext().getObject().id;
                        that.deleteDashboardById(deletedId);
                    }
                }
            });
        },

        handleActiveChange: function (oEvent) {
            var that = this;
            var source = oEvent.getSource();
            var activeRecord = source.getBindingContext().getObject();
            if (oEvent.getParameters("selected").selected === true) {
                sap.m.MessageBox.information(utils.oBundle.getText("m_changeActiveDashboard"), {
                    styleClass: "sapUiSizeCompact",
                    actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
                    onClose: function (oAction) {
                        if (oAction === "OK") {
                            var data = that._DashboardModel.getData();
                            that.activeDashboard(activeRecord);

                        }
                        that.reloadDashboardList();

                    }
                });
            }
        },

        onNavBack: function (oEvent) {
            var oHistory, sPreviousHash;
            oHistory = sap.ui.core.routing.History.getInstance();
            sPreviousHash = oHistory.getPreviousHash();
            var dsId = this.getActiveDashboardId(this._USERID);
            if (dsId !== undefined) {
                this._Component.getRouter().navTo("dashboard", {
                    id: dsId,
                    userId: this._USERID,
                    type: "Module"
                }, true);
            }
        },

        getActiveDashboardId: function (userId) {
            var url = "../api/dashboard/active/" + userId;
            var result = utils.getServiceResponseInJson(url, "GET");
            if (result.id == undefined) {
                sap.m.MessageToast.show(utils.oBundle.getText("m_noDashboard"));
            }
            else {
                return result.id;
            }
        },
        onSearch: function (oEvent) {
            var aFilters = [];
            var sQuery = oEvent.getSource().getValue();
            if (sQuery && sQuery.length > 0) {
                var filter = new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, sQuery);
                aFilters.push(filter);
            }

            // update list binding
            var list = this.getView().byId("dsTableList");
            var binding = list.getBinding("items");
            binding.filter(aFilters, "Application");
        },
        onSearchViewedDs: function (oEvent) {
            var aFilters = [];
            var sQuery = oEvent.getSource().getValue();
            if (sQuery && sQuery.length > 0) {
                var filter = new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, sQuery);
                aFilters.push(filter);
            }

            // update list binding
            var list = this.getView().byId("dsTableList_viewer");
            var binding = list.getBinding("items");
            binding.filter(aFilters, "Application");
        },

        getDashboardListByUser: function (userId) {
            var url = this._URL + userId;
            var dashboardList = utils.getServiceResponseInJson(url, "GET");
            for (var i = 0; i < dashboardList.length; i++) {
                dashboardList[i].createdTime = new Date(dashboardList[i].createdTime).toJSON();
            }
            return dashboardList;

        },
        deleteDashboardById: function (id) {
            var that = this;
            var url = "../api/dashboard/" + id;
            var result = utils.getServiceResponseInJson(url, "DELETE");
            if (result.status.toLowerCase() === "success") {
                sap.m.MessageToast.show(utils.oBundle.getText("m_deleteSuccess"));
                that.reloadDashboardList();
            }
            else if (result.stauts.toLowerCase() === "fail") {
                utils.showErrorMessageBox(utils.oBundle.getText("m_deleteFail"));
            }
        },

        activeDashboard: function (data) {
            var url = "../api/dashboard/" + data.id;
            var result = utils.getServiceResponseInJson(url, "POST", data);

        },

        handleAddDashboard: function (oEvent) {
            this._Component.getRouter().navTo("configPage", {
                userId: this._USERID,
                id: 0
            }, true);
        },

        handleDashboardItemPressed: function (oEvent) {
            //sap.ui.core.BusyIndicator.show(1);
            var oSelItem = oEvent.getSource().getBindingContext().getObject();
            utils.isNewDashboard =true;
            this._Component.getRouter().navTo("configPage", {
                userId: this._USERID,
                id: oSelItem.id
            }, true);
        },

        handleManageUsers: function (oEvent) {
            var that = this;
            var oSelItem = oEvent.getSource().getBindingContext().getObject();
            this._Component.getRouter().navTo("viewerManager", {
                id: oSelItem.id
            }, true);
        },

        handleViewLog: function (oEvent) {
            var oView = this.getView(),
                oSelItem = oEvent.getSource().getBindingContext().getObject(),
                dsId = oSelItem.id,
                dsName = oSelItem.name;
            var logDialog = oView.byId("logDialog");

            var result = utils.getServiceResponseInJson("../api/log/" + dsId, "GET");

            if (!logDialog) {
                // create dialog via fragment factory
                var logModel = new sap.ui.model.json.JSONModel();
                logModel.setData(result);
                logDialog = sap.ui.xmlfragment(oView.getId(), "ase.wt.view.Logs", this);
                logDialog.setTitle(dsName);
                logDialog.setModel(logModel);
                logDialog.setState("Warning");
                // connect dialog to view (models, lifecycle)
                oView.addDependent(logDialog);
            }
            logDialog.open();
        },

        onCloseDialog: function () {
            this.getView().byId("logDialog").destroyContent();
            this.getView().byId("logDialog").destroy();
        },

        handleLaunchDashboard: function (oEvent) {
            var source = oEvent.getSource();
            var dsId = source.getBindingContext().getObject().id;
            var url = "/#/dashboard/" + dsId + "/view/" + this._USERID;
            sap.m.URLHelper.redirect(url, true);
        }

    });
    return ListController;
});