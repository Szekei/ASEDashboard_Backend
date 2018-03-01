sap.ui.define([
    "sap/ui/core/Control"

], function (Control) {
    "use strict";
    jQuery.sap.declare("ase.wt.control.TrafficLightIndicator");
    return Control.extend("ase.wt.control.TrafficLightIndicator", {

        metadata: {
            properties: {
                color: {type: "string", defaultValue: "red"},
                text: {type: "string", defaultValue: ""},
                url: {type: "string", defaultValue: ""}
            }

        },

        init: function () {

        },

        renderer: function (oRm, oControl) {
            var oColor = oControl.getColor();
            var oText = oControl.getText();
            var oLink = oControl.getUrl();

            oRm.write("<div class='traffic-light-container'");
            oRm.writeControlData(oControl);
            oRm.write(">");
            oRm.write("<div class='traffic-light-indicator'");
            if (oColor == "blue") {
                oRm.addStyle("background-color", "Green");
                oRm.writeStyles();

            }
            else if (oColor === "red" || oColor === "aborted") {
                oRm.addStyle("background-color", "Red");
                oRm.writeStyles();

            }
            else if (oColor === "grey") {
                oRm.addStyle("background-color", "Darkgrey");
                oRm.writeStyles();
            }
            oRm.write(">");
            oRm.write("<h3>");
            oRm.write(oText);
            oRm.write("</h3>");
            oRm.write("<a target='_blank' href='");
            oRm.write(oLink);
            oRm.write("'>");
            oRm.write("<span class='hyperspan' />");

            oRm.write("</a>");

            oRm.write("</div>");
            oRm.write("</div>");
        }
    });
});