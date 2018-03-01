sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ase/wt/js/utils"
], function (Controller, utils) {
    "use strict";
    var _timeout;
    return Controller.extend("ase.wt.controller.ConfigJenkins", {

        onInit: function () {
            this._Component = this.getOwnerComponent();
        },

        handlePingJenkinsServer: function (oEvent) {
            this.openBusyDialog(oEvent);

            var that = this;
            var params = that.getView().getModel().getProperty("/data/jenkinsServer");
            this.pingService(params);

        },


        setStatusConnected: function () {
            var objectStatus = this.getView().byId("jenkinsServerStatus");
            objectStatus.setIcon("sap-icon://accept");
            objectStatus.setState("Success");
            objectStatus.setText(utils.oBundle.getText("m_connected"));
        },

        setStatusNotConnected: function () {
            var objectStatus = this.getView().byId("jenkinsServerStatus");
            objectStatus.setIcon("sap-icon://alert");
            objectStatus.setState("Error");
            objectStatus.setText(utils.oBundle.getText("m_notConnected"));
        },

        handleSelectChange: function (oEvent) {
            this.getView().getModel().refresh(true);
        },

        handleSelectRules: function (oEvent) {
            var oView = this.getView();
            var fragModel = new sap.ui.model.json.JSONModel();
            var selObj;
            if (oEvent.selObj !== undefined) {
                selObj = oEvent.selObj;
            }
            else {
                var selIndex = oEvent.getSource().getBindingContext().sPath;
                selObj = oView.getModel().getProperty(selIndex);
            }
            //Copy the old obj for editing
            var newObj = jQuery.extend(true, {}, selObj);

            var oRulesDialog = oView.byId("rulesDialog");
            if (!oRulesDialog) {
                oRulesDialog = sap.ui.xmlfragment(oView.getId(), "ase.wt.view.JenkinsRule", this);
                oView.addDependent(oRulesDialog);
            }

            oRulesDialog.addButton(new sap.m.Button("okButton", {
                text: utils.oBundle.getText("b_ok"),
                press: function () {
                    selObj.jenkinsJob.rules = newObj.jenkinsJob.rules;
                    oView.getModel().refresh(true);
                    oRulesDialog.close();
                    oRulesDialog.destroyButtons();
                }
            }));

            oRulesDialog.addButton(new sap.m.Button("cancelButton", {
                text: utils.oBundle.getText("b_cancel"),
                press: function () {
                    oRulesDialog.close();
                    oRulesDialog.destroyButtons();
                }
            }));
            fragModel.setData(newObj.jenkinsJob.rules);
            oRulesDialog.setModel(fragModel);
            oRulesDialog.open();
        },

        handleDeleteJob: function (oEvent) {
            var that = this;
            var delObj = oEvent.getSource().getBindingContext().getObject();

            if (delObj.jenkinsJob.jobId != 0) {
                utils.oDeletedParams.push({type: "jenkinsJob", id: delObj.jenkinsJob.jobId});
            }
            delObj.jenkinsJob = {};
            that.getView().getModel().refresh(true);
        },

        onToggleOpenState: function (oEvent) {
            var oTable = this.getView().byId("jobsTable"),
                modules = this.getView().getModel().getData().data.modules[0].modules;
            if (oEvent.getParameter("expanded"))
                oTable.setVisibleRowCount(modules.length + 1);
            else
                oTable.setVisibleRowCount(2);
        },

        encryptPassword: function (oEvent) {
            var encrypt = new JSEncrypt();
            encrypt.setPublicKey(utils.getRsaPublicKey());
            var encrypted = encrypt.encrypt(oEvent.getParameter("value"));
            this.getView().getModel().getData().data.jenkinsServer.password = encrypted;
            this.getView().getModel().refresh(true);

        },

        handleAddJob: function (oEvent) {
            var that = this;
            var oTable = that.getView().byId("jobsTable");
            var sIndex = oTable.getSelectedIndex();
            if (sIndex === -1) {
                utils.showWarningMessageBox(utils.oBundle.getText("m_noSelection"));
                return;
            }
            var dashboardData = that.getView().getModel().getData().data;
            if (!utils.validateServerInputs(dashboardData.jenkinsServer)) {
                return;
            }
            var selObj = oTable.getContextByIndex(sIndex).getObject();
            if (selObj.jenkinsJob != undefined && utils.isObjectNotNull(selObj.jenkinsJob.name)) {
                utils.showWarningMessageBox(utils.oBundle.getText("m_jobExisted"));
                return;
            }
            var newJob = {
                name: "",
                isActive: false,
                jobId: 0,
                op: utils._NEW,
                rules: {
                    frontEndUT: {
                        ruleId: 0,
                        op: utils._NEW,
                        startIdentifier: "",
                        endIdentifier: "",
                        reportFormat: ""
                    },
                    backEndUT: {
                        ruleId: 0,
                        op: utils._NEW,
                        startIdentifier: "",
                        endIdentifier: "",
                        reportFormat: ""
                    },
                    integrationUT: {
                        ruleId: 0,
                        op: utils._NEW,
                        startIdentifier: "",
                        endIdentifier: "",
                        reportFormat: ""
                    },
                    apiTest: {
                        ruleId: 0,
                        op: utils._NEW,
                        startIdentifier: "",
                        endIdentifier: "",
                        reportFormat: ""
                    }
                }
            };
            newJob.jenkinsServer = that.getView().getModel().getData().jenkinsServer;
            selObj.jenkinsJob = newJob;

            that.getView().getModel().refresh(true);
            that.handleSelectRules({"selObj": selObj})
        },


        onChangeJob: function (oEvent) {
            var selObj = oEvent.getSource().getBindingContext().getObject();
            if (selObj.jenkinsJob.jobId != 0) {
                selObj.jenkinsJob.op = utils._CHANGE;
            }
        },

        onChangeRule: function (oEvent) {
            var selObj = oEvent.getSource().getBindingPath("value");
            selObj = selObj.split("/");
            var path = "/" + selObj[1];

            var data = oEvent.getSource().getBinding("value").getModel().getProperty(path);
            if (data.ruleId != 0) {
                data.op = utils._CHANGE;
            }
            utils.oDisplayModel.refresh(true);
        },

        openBusyDialog: function (oEvent) {
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

        pingService: function (parameters) {
            var that = this;
            var result = '';
            if (parameters != undefined)
                parameters = JSON.stringify(parameters);
            var request = $.ajax({
                url: '../api/ping/jenkinsServer',
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
                    utils.showErrorMessageBox(utils.oBundle.getText("m_requestFail"));
                    that._dialog.close();
                });
        }
    });
});