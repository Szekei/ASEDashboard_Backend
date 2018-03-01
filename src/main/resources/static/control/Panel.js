sap.ui.define([
    "sap/ui/core/Control"

], function (Control) {
    "use strict";
    return Control.extend("ase.wt.control.Panel", {
        metadata: {
            properties: {
                width: {
                    type: "sap.ui.core.CSSSize",
                    defaultValue: "100%"
                },
                title: {
                    type: "string",
                    defaultValue: ""
                },
                link: {
                    type: "string",
                    defaultValue: ""
                },
                subTitle: {
                    type: "string",
                    defaultValue: ""
                },
                iconVisible: {
                    type: "string",
                    default: "false"
                },
                iconColor: {
                    type: "string",
                    default: ""
                },
                message:{
                    type: "string",
                    default:""
                }

            },
            aggregations: {
                _panel: {
                    type: "sap.m.Panel",
                    multiple: false,
                    visibility: "hidden"
                },

                panelTable: {
                    type: "sap.m.Table",
                    multiple: false
                }
            }

        },
        init: function () {
            var oTable = new sap.m.Table({});
            var oToolbar = new sap.m.Toolbar({
                content: [
                    new sap.m.VBox({
                        items: [new sap.m.Link({
                            target: "_blank",
                            enabled: false
                        }),
                            new sap.m.Title({
                                titleStyle: "H6"
                            })]
                    }),
                    new sap.ui.core.Icon({
                        src: "sap-icon://alert",
                        visible: false
                    })

                ]
            });
            var oPanel = new sap.m.Panel({
                headerToolbar: oToolbar,
                expandable: true
            });
            this.setAggregation("_panel", oPanel);

        },

        setTitle: function (iTitle) {
            var oLink = this.getAggregation("_panel").getHeaderToolbar().getContent()[0].getItems()[0];
            oLink.setText(iTitle);
        },
        setLink: function (iLink) {
            var oLink = this.getAggregation("_panel").getHeaderToolbar().getContent()[0].getItems()[0];
            oLink.setEnabled(true);
            oLink.setHref(iLink);
        },

        setSubTitle: function (iSubTitle) {
            var oSub = this.getAggregation("_panel").getHeaderToolbar().getContent()[0].getItems()[1];
            oSub.setText(iSubTitle);
        },

        setIconVisible: function (iconVisible) {
            if (iconVisible == "true")
                this.getAggregation("_panel").getHeaderToolbar().getContent()[1].setVisible(true);
            else if (iconVisible == "false")
                this.getAggregation("_panel").getHeaderToolbar().getContent()[1].setVisible(false);
        },

        setIconColor: function (iColor) {
            this.getAggregation("_panel").getHeaderToolbar().getContent()[1].setColor(iColor);
        },

        setPanelTable: function(iTable){
            this.getAggregation("_panel").addContent(iTable);
        },

        setMessage: function(message){
            this.getAggregation("_panel").getHeaderToolbar().getContent()[1].setTooltip(message);
        },

        renderer: function (oRm, oControl) {
            oRm.write("<div");

            oRm.addClass("infoPanel");
            oRm.writeClasses(oControl);
            oRm.write(" style=\"width:"+oControl.getWidth()+"\"");
            oRm.writeControlData(oControl);
            oRm.write(">");

            oRm.renderControl(oControl.getAggregation("_panel"));
            oRm.write("</div>");
        }
    });
});