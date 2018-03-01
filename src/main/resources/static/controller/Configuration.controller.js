sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ase/wt/js/utils"
], function (Controller, utils) {
    "use strict";
    var ConfigController = Controller.extend("ase.wt.controller.Configuration", {
        onInit: function () {
            jQuery.sap.require("sap.m.MessageBox");
            this._URL = "../api/dashboard/config/";
            this._dsId = 0;
            utils.oDisplayModel = new sap.ui.model.json.JSONModel();
            this._Component = this.getOwnerComponent();
            this._USERID = "";

            var oRouter = this._Component.getRouter();
            oRouter.getRoute("configPage").attachMatched(function (oEvent) {
                this._USERID = (oEvent.getParameter("arguments").userId);
                var sessionUser = utils.getSessionUser();
                if(!utils.isObjectNotNull(sessionUser)||this._USERID != sessionUser){
                    this._Component.getRouter().navTo("login");
                }
                this._setData(oEvent.getParameter("arguments").id);
            }, this);
        },

        _setData: function (dsId) {
            this._dsId = dsId;
            if (dsId == 0) {
                utils.isNewDashboard = true;
                this.clearForm();
                return;
            }
            else {
                utils.isNewDashboard = false;
                var disPlayData = this.getDashboardConfigData(dsId);
                var pageData = {};
                pageData.data = disPlayData;
                utils.oDisplayModel.setData(pageData);
                this.getView().setModel(utils.oDisplayModel);
            }

        },

        getDashboardConfigData: function (dsId) {
            var url = this._URL + dsId;
            var result = utils.getServiceResponseInJson(url, 'GET');
            return result;
        },

        onExit: function () {
            utils.oDisplayModel.destroy();
        },

        handlePressUser: function (oEvent) {
            this._Component.handlePressUser(oEvent, false);
        },

        clearForm: function () {
            utils.oDisplayModel.getData().data = {
                name: "",
                schedulerTime: "0700",
                id: 0,
                op: 1,
                modules: [],
                jenkinsServer: {
                    url: "",
                    userName:"",
                    password: "",
                    isPipelineJob: false,
                    op: 1,
                    serverId: 0
                },
                jenkinsServerRef: {
                    url: "",
                    userName:"",
                    password: "",
                    isPipelineJob: false,
                    op: 1,
                    serverId: 0
                },
                sonarServer: {
                    url: "",
                    userName:"",
                    password: "",
                    op: utils._NEW,
                    serverId: 0
                },
                bcpServer: {
                    url: "https://support.wdf.sap.corp",
                    userName:"",
                    password: "",
                    op: utils._NEW,
                    serverId: 0,
                    component:"",
                    projectMembers:[]
                }
            };
            utils.oDisplayModel.refresh(true);
            this.getView().setModel(utils.oDisplayModel);
        },

        onNavBack: function (oEvent) {
            var that = this;
            that.getView().byId("iconTabBar").setSelectedKey("tabBasic");
            if (!utils.isObjectNotNull(that._USERID)) {
                that._Component.getRouter().navTo("login");
            }
            that._Component.getRouter().navTo("dashboardManager", {
                userId: that._USERID
            }, true);

        },

        handleIconTabBarSelect: function (oEvent) {
            var that = this,
                oIconBar = that.getView().byId("iconTabBar"),
                sKey = oEvent.getParameter("key"),
                data = utils.oDisplayModel.getData(),
                dashboardData = utils.oDisplayModel.getData().data,
                isValidInputs = true;
            if (!utils.isObjectNotNull(dashboardData.name)) {
                utils.showWarningMessageBox(utils.oBundle.getText("m_noDashboardName"));
                oIconBar.setSelectedKey("tabBasic");
                return;
            }
            var project = dashboardData.modules[0];

            //Validate the inputs of project name
            if (project == undefined || !utils.isObjectNotNull(project.name)) {
                isValidInputs = false;
            }
            //Validate the inputs of module name
            else if (project.modules.length > 0) {
                project.modules.forEach(function (module) {
                    if (!utils.isObjectNotNull(module.name))
                        isValidInputs = false;
                });
            }
            if (!isValidInputs) {
                utils.showWarningMessageBox(utils.oBundle.getText("m_invalidInputs"));
                oIconBar.setSelectedKey("tabBasic");
                return;
            }

            //Set the module/project selection tree
            if (sKey === "tabJenkins" || sKey === "tabSonar" || sKey === "tabJenkinsRef") {
                var list = [];
                if (project != undefined && utils.isObjectNotNull(project.name)) {
                    list.push(project);
                    if (project.modules != undefined)
                        project.modules.forEach(function (obj) {
                            if (utils.isObjectNotNull(obj.name))
                                list.push(obj)
                        })
                }
                if (list.length == 0) {
                    utils.showWarningMessageBox(utils.oBundle.getText("m_noProject"));
                    oIconBar.setSelectedKey("tabBasic");
                    return;
                }
                data.moduleList = list;

            }
            that.getView().getModel().refresh(true);

            if (sKey === "tabJenkins") {
                if (data.data.jenkinsServer == undefined) {
                    data.data.jenkinsServer = {url: "", userName: "", password: ""};
                }
            }

            if (sKey === "tabJenkinsRef") {
                if (data.data.jenkinsServerRef == undefined) {
                    data.data.jenkinsServerRef = {url: "", userName: "", password: ""};
                }
            }
            if (sKey === "tabSonar") {
                if (data.data.sonarServer == undefined) {
                    data.data.sonarServer = {url: "", userName: "", password: ""};
                }
            }
            //Validate the user
            if (sKey !== "tabBcp") {
                if (dashboardData.bcpServer == undefined) {
                    dashboardData.bcpServer = {url: "", userName: "", password: "", projectMembers: []};
                }
                var userList = dashboardData.bcpServer.projectMembers;
                if (userList != undefined) {
                    userList.forEach(function (name) {
                        if (!utils.isObjectNotNull(name)) {
                            utils.showWarningMessageBox(utils.oBundle.getText("m_invalidUser"));
                            oIconBar.setSelectedKey("tabBcp");
                            return;
                        }
                    })
                }
            }

        },

        handleSaveDashboard: function (oEvent) {
            var that = this;
            var parameters = utils.oDisplayModel.getData().data;
            //Get the new data object to save
            if (!this.validationOnSave(parameters)) {
                return;
            }
            if(utils.oDeletedParams.length > 0) {
                //Delete first
                var result = utils.getServiceResponseInJson(that._URL, "DELETE", utils.oDeletedParams);
            }
            //Save to db
            var url = that._URL + parameters.id;
            result = utils.getServiceResponseInJson(url, "POST", parameters);
            if (result.status == "Success") {
                that._IS_SAVED = true;
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
                            that.onNavBack();
                        }
                    })
                });
                dialog.open();
            }
        },

        changeToViewMode: function () {
            this._IS_SAVED = true;
        },

        changeToEditMode: function () {
            this._IS_SAVED = false;
        },

        onPressClock: function (oEvent) {
            var oModel = this.getView().getModel();
            var oData = oModel.getData();
            if (oData.data.schedulerTime == undefined) {
                oData.data.schedulerTime = "07:00";
            }
            oData.data.op = utils._CHANGE;
            var oldValue = oData.data.schedulerTime;
            var popover = new sap.m.Popover({
                showHeader: false,
                placement: sap.m.PlacementType.Bottom,
                content: [
                    new sap.m.TimePicker({
                        value: "{/data/schedulerTime}",
                        valueFormat: "HHmm",
                        displayFormat: "HH:mm"
                    })]
            }).addStyleClass("sapMOTAPopover sapTntToolHeaderPopover");
            popover.setModel(oModel);
            popover.openBy(oEvent.getSource());

        },

        validationOnSave: function (params) {
            var that = this;
            var project = params.modules[0];
            if (project == undefined) {
                utils.showErrorMessageBox(utils.oBundle.getText("m_noProject"));
                return false;
            }
            else {
                var modules = project.modules;
            }

            // Check ds Name not null
            if (!utils.isObjectNotNull(params.name)) {
                utils.showErrorMessageBox(utils.oBundle.getText("m_noDashboardName"));
                return false;
            }
            // Check project member user id be valid
            if(params.bcpServer.projectMembers!=undefined) {
                for (var i = 0; i < params.bcpServer.projectMembers.length; i++) {
                    if (!utils.isValidUserId(params.bcpServer.projectMembers[i])) {
                        utils.showErrorMessageBox(utils.oBundle.getText("m_invalidUser"));
                        return false;
                    }
                }
            }

            if (!utils.isNewDashboard) {
                var saveData = that.getDashboardConfigData(that._dsId);
                if (params.name != saveData.name) {
                    params.op = utils._CHANGE;
                }


                if (params.sonarServer.url != saveData.sonarServer.url || params.sonarServer.userName != saveData.sonarServer.userName || params.sonarServer.password != saveData.sonarServer.userName) {
                    params.sonarServer.op = utils._CHANGE;
                }

                //Level 2: Check bcp server
                params.bcpServer.op = utils._CHANGE;


                //Level 2: Check jenkinsServer
                if (params.jenkinsServer.url != saveData.jenkinsServer.url || params.jenkinsServer.userName != saveData.jenkinsServer.userName || params.jenkinsServer.password != saveData.jenkinsServer.userName) {
                    params.jenkinsServer.op = utils._CHANGE;
                }

                //Level 2: Check jenkinsServerRef
            }
            utils.oDisplayModel.refresh(true);

            return true
        },


        handlePressCancel: function (oEvent) {
            var that = this;

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
    });
    return ConfigController;
});
