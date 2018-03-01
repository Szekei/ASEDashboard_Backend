sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ase/wt/js/utils"
], function (Controller, utils) {
    "use strict";
    return Controller.extend("ase.wt.controller.ConfigBasic", {

        onInit: function () {
            this._Component = this.getOwnerComponent();
        },

        changeOperation: function (oEvent) {
            var sPath = oEvent.getSource().getBindingContext().sPath;
            utils.oDisplayModel.getProperty(sPath).name = oEvent.getSource().getValue();
            var obj = utils.oDisplayModel.getProperty(sPath);
            if (!utils.isNewDashboard && obj.moduleId != 0) {
                object.op = utils._CHANGE;
                utils.oDisplayModel.refresh(true);
            }
        },

        handleAddModule: function (oEvent) {
            var that = this,
                newRecord = {
                    name: "",
                    type: "",
                    moduleId: 0,
                    op: utils._NEW,
                    sonarKey: "",
                    sonarTasks: []
                };
            var data = that.getView().getModel().getData().data;
            if (!utils.isObjectNotNull(data.name)) {
                utils.showWarningMessageBox(utils.oBundle.getText("m_noDashboardName"));
                return;
            }
            var oTable = that.getView().byId("moduleTable");
            if (data.modules == undefined)
                data.moduels = [];
            if (data.modules.length == 0) {
                newRecord.type = "Project";
                newRecord.modules = [];
                data.modules.push(newRecord);
            }
            else {
                newRecord.type = "Module";
                newRecord.parentId = 0;
                data.modules[0].modules.push(newRecord);
            }
            that.getView().getModel().refresh(true);
            oTable.setVisibleRowCount(data.modules[0].modules.length + 1);
            oTable.expandToLevel(1);
        },

        onToggleOpenState: function (oEvent) {
            var oTable = this.getView().byId("moduleTable"),
                modules = this.getView().getModel().getData().data.modules[0].modules;
            if (oEvent.getParameter("expanded"))
                oTable.setVisibleRowCount(modules.length + 1);
            else
                oTable.setVisibleRowCount(2);
        },

        handleDeleteModule: function (oEvent) {
            var that = this;
            var oTable = that.getView().byId("moduleTable");
            var selObj = oEvent.getSource().getBindingContext().getObject();
            var oModel = that.getView().getModel();
            var data = oModel.getData().data;
            if (selObj.moduleId != 0) {
                utils.oDeletedParams.push({type: "module", id: selObj.moduleId});
            }
            if (selObj.type == "Project" && utils.isObjectNotNull(selObj.name)) {
                sap.m.MessageBox.confirm(utils.oBundle.getText("m_deleteProject"), {
                    styleClass: "sapUiSizeCompact",
                    actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
                    onClose: function (oAction) {
                        if (oAction === "OK") {
                            data.modules = [];
                        }
                        oModel.refresh(true);
                        oTable.expandToLevel(1)
                    }
                });
            }
            else if (selObj.type == "Module") {
                sap.m.MessageBox.confirm(utils.oBundle.getText("m_deleteModule"), {
                    styleClass: "sapUiSizeCompact",
                    actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
                    onClose: function (oAction) {
                        if (oAction === "OK") {
                            var list = data.modules[0].modules;
                            for (var i = 0; i < list.length; i++) {
                                if (list[i].name == selObj.name) {
                                    list.splice(i, 1);
                                    break;
                                }
                            }
                            oTable.setVisibleRowCount(data.modules[0].modules.length + 1);
                        }
                        oModel.refresh(true);
                        oTable.expandToLevel(1)
                    }
                });

            }
        }
    });
});