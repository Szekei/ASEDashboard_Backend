sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ase/wt/js/utils"
], function (Controller, utils) {
    "use strict";
    return Controller.extend("ase.wt.controller.Board", {

        onInit: function () {
            this._Component = this.getOwnerComponent();
            this._dsId = "";
            this._USERID="";
            this._TYPE = "Project";
            this._DS_MODEL = new sap.ui.model.json.JSONModel();
            this.getView().setModel(this._DS_MODEL);

            var oRouter = this._Component.getRouter();
            oRouter.getRoute("dashboard").attachMatched(function (oEvent) {
                this._dsId = oEvent.getParameter("arguments").id;
                this._USERID = oEvent.getParameter("arguments").userId;
                var sessionUser = utils.getSessionUser();
                if(!utils.isObjectNotNull(sessionUser)||this._USERID != sessionUser){
                    this._Component.getRouter().navTo("login");
                }
                this.checkPermission(this._USERID, this._dsId);
                this.loadDashboard();
            }, this);

        },

        handlePressHelp: function (oEvent) {
            alert("Coming soon...");
        },
        handlePressUser: function (oEvent) {
            this._Component.handlePressUser(oEvent, true);
        },

        handleSyncData: function(oEvent){
            var url = "../api/job/" + this._dsId;
            var result = utils.getServiceResponseInJson(url, "GET");
            if(result.status == "Success"){
                utils.showSuccessMessageBox(result.message);
            }
            else{
                utils.showErrorMessageBox(result.message);
            }
        },

        loadDashboard: function(){
            var data = this.getBoardData();

            this._DS_MODEL.setData(data);
            this._DS_MODEL.refresh(true);
        },




        goToDsListView: function(userId){
            this._Component.getRouter().navTo("dashboardManager", {
                userId: userId
            });
        },

        checkPermission: function(userId, dsId){
            var url = "../api/access/" + dsId+"/"+userId;
            var result = utils.getServiceResponseInJson(url,"GET");
            if(result.status == "Success"){
                return true;
            }
            else if(result.status== "Fail"){
                this._Component.getRouter().navTo("noAccess", {} );
            }

        },
        getBoardData: function(){
            var oData = {
                dsName:"",
                jenkinsStatus:{},
                internalTicket:{},
                customerTicket:{},
                accTicket:{},
                frontendUT:{},
                backendUT:{},
                integrationTest:{},
                api:{},
                techIssues:{},
                codeDebt:{},
                security:{},
                performance:{}
            };
            //Dashboard Name
            oData.dsName = utils.getServiceResponseInJson("../api/dashboard/"+this._dsId).name;
            //Jenkins Status
            oData.jenkinsStatus = this.getDashboardDataService("../api/jenkinsJobStatus/", false);
            if(oData.jenkinsStatus.length == 0){
                oData.jenkinsStatus.push( {color:"grey", url:"https://xmake-dev.wdf.sap.corp:8443/", moduleName:"N/A"});
            }


            //Fetch data for BCP
            oData.internalTicket = utils.getServiceResponseInJson("../api/ticket/"+this._dsId+"/internal");
            oData.customerTicket = utils.getServiceResponseInJson("../api/ticket/"+this._dsId+"/customer");
            oData.accTicket = utils.getServiceResponseInJson("../api/ticket/"+this._dsId+"/internal-acc");



            //Functional quality
            oData.frontendUT = this.getDashboardDataService("functionalQuality/frontendUT/", true, true);
            oData.backendUT = this.getDashboardDataService("functionalQuality/backendUT/", true, true);
            oData.integrationTest = {};
            oData.api = this.getDashboardDataService("functionalQuality/apiTest/", true, true);

            //Code quality
            oData.techIssues = this.getDashboardDataService("codeQuality/techIssue/", true);
            oData.codeDebt = this.getDashboardDataService("codeQuality/codeDebt/",true);

            //Production quality
            oData.security = {};
            oData.performance = {};

            //Refresh data
            return oData;
        },

        setDataForJenkins: function(obj){
            var result = {};
            result.count=obj.count;
            result.data = obj.mainData.data;
            result.url = obj.mainData.url;
            if(obj.mainData.data.length == 0 && obj.refData.data.length!=0){
                result.data = obj.refData.data;
                result.url = obj.refData.url;
            }
            return result;
        },

        getDashboardDataService: function(serviceName, hasIcon, isJenkins){
            if(this._dsId!==0 && this._dsId!=undefined &&utils.isObjectNotNull(this._dsId)) {
                var url = "../api/" + serviceName + this._dsId + "/" + this._TYPE;
                var result = utils.getServiceResponseInJson(url, "GET");
                if(isJenkins){
                    result = this.setDataForJenkins(result);
                }
                if(hasIcon && result.data != undefined) {
                    result.message = "";
                    result.iconVisible = "false";
                    var index = 0;
                    var isLatest = true;
                    var isGoodCoverage = true;
                    for (var i = 0; i < result.data.length; i++) {
                        if (isLatest && result.data[i].isLatest == false) {
                            result.iconColor = "Orange";
                            result.message += (++index)+"."+utils.oBundle.getText("tip_notLatestData")+"\n";
                            isLatest = false;
                        }
                        if (isGoodCoverage&& result.data[i].coverage!=undefined&& result.data[i].coverage < "80%") {
                            result.iconColor = "Red";
                            result.message += (++index)+"."+utils.oBundle.getText("tip_badCoverage")+"\n";
                            isGoodCoverage = false;
                        }
                    }
                    if (result.count!=undefined && result.count > 0) {
                        result.iconColor = "Red";
                        result.message += (++index)+"."+utils.oBundle.getText("tip_hasIssues")+"\n";
                    }
                    if(utils.isObjectNotNull(result.iconColor))
                        result.iconVisible = "true";
                }
                if(serviceName == "functionalQuality/backendUT/"){
                    var totalLine = 0;
                    var coverLine = 0;
                    for(var i=0; i<result.data.length; i++){
                        coverLine += (result.data[i].codeLine*parseFloat(result.data[i].coverage)/100);
                        totalLine +=result.data[i].codeLine;
                    }
                    if(totalLine!=0) {
                        result.average = ((coverLine / totalLine)*100).toFixed(2)+"%";
                    }
                }

                return result;
            }
            else
                this.goToDsListView(this._USERID);
        },

        onChangeDashboardType: function(oEvent){
            var that = this;
            var selIndex = oEvent.getParameters().selectedIndex;
            if(selIndex == 1){
                that._TYPE = "Module";
                sap.m.MessageToast.show(utils.oBundle.getText("m_moduleSelected"));
                // that._Component.getRouter().navTo("dashboard",{
                //     id: that._dsId,
                //     userId: utils.currentUser,
                //     type: "Module"
                // });
            }
            else if(selIndex == 0){
                that._TYPE = "Project";
                sap.m.MessageToast.show(utils.oBundle.getText("m_projectSelected"));
            }
            that.loadDashboard()
        }


    });
});