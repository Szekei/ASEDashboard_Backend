/**
 * Created by I072223 on 4/24/2017.
 */
sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ase/wt/js/utils"
], function (Controller, utils) {
    "use strict";
    return Controller.extend("ase.wt.controller.ConfigSonar", {

        onInit: function () {
            this._Component = this.getOwnerComponent();
        },
        handlePingSonarServer: function (oEvent) {
            this.openBusyDialog(oEvent);

            var that = this;
            var params = that.getView().getModel().getProperty("/data/sonarServer");
            that.pingService(params);
        },

        setStatusConnected: function () {
            var objectStatus = this.getView().byId("sonarServerStatus");
            objectStatus.setIcon("sap-icon://accept");
            objectStatus.setState("Success");
            objectStatus.setText(utils.oBundle.getText("m_connected"));
        },

        setStatusNotConnected: function () {
            var objectStatus = this.getView().byId("sonarServerStatus");
            objectStatus.setIcon("sap-icon://alert");
            objectStatus.setState("Error");
            objectStatus.setText(utils.oBundle.getText("m_notConnected"));
        },

        openBusyDialog: function (oEvent) {
            this._dialog = this.getView().byId("busyDialog");
            if (!this._dialog) {
                this._dialog = sap.ui.xmlfragment("ase.wt.view.BusyDialog", this);
                this.getView().addDependent(this._dialog);
            }
            // open dialog
            jQuery.sap.syncStyleClass("sapUiSizeCompact", this.getView(), this._dialog);
            this._dialog.open();

        },
        onDialogClosed: function (oEvent) {
            if (oEvent.getParameter("cancelPressed")) {
                sap.m.MessageToast.show(utils.oBundle.getText("m_cancelDialog"));
            } else {
                sap.m.MessageToast.show(utils.oBundle.getText("m_completeDialog"));
            }
        },
        handleDeleteSonarTask: function (oEvent) {
            var that = this;
            var selObj = oEvent.getSource().getBindingContext().getObject();
            if (selObj.taskId != 0) {
                utils.oDeletedParams.push({type: "sonarTask", id: selObj.taskId});
            }

            if (selObj.type == undefined) {
                var oTable = that.getView().byId("sonarTable");
                var oModel = oEvent.getSource().getBindingContext().getModel();
                var path = oEvent.getSource().getBindingContext().getPath().split("/");
                var index = path.pop();
                path = path.join("/");

                var dataList = oModel.getProperty(path);
                dataList.splice(index, 1);

                oModel.refresh(true);
                oTable.expandToLevel(1)
            }
            else {
                utils.showWarningMessageBox(utils.oBundle.getText("m_disabledDeleteProject"));
                return;
            }
        },

        encryptPassword: function (oEvent) {
            var encrypt = new JSEncrypt();
            encrypt.setPublicKey(utils.getRsaPublicKey());
            var encrypted = encrypt.encrypt(oEvent.getParameter("value"));
            this.getView().getModel().getData().data.sonarServer.password = encrypted;
            this.getView().getModel().refresh(true);

        },

        handleAddSonarTask: function (oEvent) {
            var that = this,
                oTable = that.getView().byId("sonarTable"),
                oModel = that.getView().getModel(),
                sonarServer = oModel.getData().data.sonarServer;
            if (!utils.validateServerInputs(sonarServer)) {
                return;
            }
            var sIndex = oTable.getSelectedIndex();
            if (sIndex === -1) {
                utils.showWarningMessageBox(utils.oBundle.getText("m_noSelection"));
                return;
            }
            var selObj = oTable.getContextByIndex(sIndex).getObject();

            if (selObj.type == undefined) {
                utils.showWarningMessageBox(utils.oBundle.getText("m_selectionWrong"));
                return;
            }
            else {
                var newTask = {
                    taskId: 0,
                    op: utils._NEW,
                    name: "",
                    sonarKey: ""
                };
                selObj.sonarTasks.push(newTask);
                selObj.sonarKey = "";
                oModel.refresh(true);
                oTable.expandToLevel(1);
            }
        },

        changeOperation: function (oEvent) {
            var path = oEvent.getSource().getBindingContext().sPath;
            var data = this.getView().getModel().getProperty(path);
             if (data.taskId > 0) {
                data.op = utils._CHANGE;
            }
            else if (data.moduleId > 0) {
                data.op = utils._CHANGE;
            }
            utils.oDisplayModel.refresh(true);
        },

        pingService: function (parameters) {
            var that = this;
            var result = '';
            if (parameters != undefined)
                parameters = JSON.stringify(parameters);
            var request = $.ajax({
                url: '../api/ping/sonarServer',
                dataType: 'json',
                data: parameters,
                cache: false,
                type: 'POST',
                async: true,
                contentType: 'application/json'
            });
            request.done(function (data, textStatus, xmlhttprequest) {
                try {
                    if (data.status == "Success")
                        that.setStatusConnected();
                    else
                        that.setStatusNotConnected();
                } catch (err) {
                    //Hide BusyIndicator if it is being shown
                    that._dialog.close();
                    var messageText = err.message;
                    utils.showErrorMessageBox(messageText);
                    //throw err;
                }
                //Hide BusyIndicator if it is being shown
                that._dialog.close();
            });
            request.fail(
                function (jqXHR, textStatus) {
                    //Hide BusyIndicator if it is being shown
                    utils.showErrorMessageBox(this.oBundle.getText("m_requestFail"));
                    that._dialog.close();
                });
        }

    });
});