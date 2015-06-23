var hpciStatus = "";
var hpciNoConflict = "";
var successCallbackMDLIVE = null;
var failureCallbackMDLIVE = null;

var captchaIdValue = "1483778";
var captchaRespValue = "649237";

var hpciUrlParam = function(name, queryStr){
	var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(queryStr);
	if (!results) { return 0; }
	return results[1] || 0;
};

var processCCTokenHPCIMsg = function() { };
processCCTokenHPCIMsg = function(e) {

    hpciStatus = hpciUrlParam('hpciStatus', "?" + e.data);

    if (hpciStatus == "success") {

        hpciMappedCCValue = hpciUrlParam('hpciCC', "?" + e.data);
        hpciMappedCVVValue = hpciUrlParam('hpciCVV', "?" + e.data);
        hpciCCBINValue = hpciUrlParam('hpciCCBIN', "?" + e.data);
        hpci3DSecValue = hpciUrlParam('hpci3DSec', "?" + e.data);
        if (hpci3DSecValue == "verify3dsec") {
	         if (typeof hpciSiteShow3DSecHandler!="undefined") {
	        	 hpciSiteShow3DSecHandler();
	         }
	         else {
	        	 hpciDefaultSiteShow3DSecHandler();
	         }
        }
        else if (hpci3DSecValue == "report3dsec") {
       	 hpci3DSecAuthStatus = hpciUrlParam('hpci3DSecAuthStatus', "?" + e.data);
       	 hpci3DSecAuthCAVV = hpciUrlParam('hpci3DSecAuthCAVV', "?" + e.data);
       	 hpci3DSecAuthECI = hpciUrlParam('hpci3DSecAuthECI', "?" + e.data);
       	 hpci3DSecTxnId = hpciUrlParam('hpci3DSecTxnId', "?" + e.data);
	         if (typeof hpci3DSiteSuccessHandlerV2!="undefined") {
	        	 hpci3DSiteSuccessHandlerV2(hpciMappedCCValue, hpciMappedCVVValue, hpciCCBINValue, hpci3DSecAuthStatus, hpci3DSecAuthCAVV, hpci3DSecAuthECI, hpci3DSecTxnId);
	         }
	         else if (typeof hpci3DSiteSuccessHandler!="undefined") {
	        	 hpci3DSiteSuccessHandler(hpciMappedCCValue, hpciMappedCVVValue, hpci3DSecAuthStatus, hpci3DSecAuthCAVV, hpci3DSecAuthECI, hpci3DSecTxnId);
	         }
	         else {
	        	 hpci3DDefaultSiteSuccessHandler(hpciMappedCCValue, hpciMappedCVVValue, hpci3DSecAuthStatus, hpci3DSecAuthCAVV, hpci3DSecAuthECI, hpci3DSecTxnId);
	         }
        }
        else if (hpci3DSecValue == "reportpinverfy") {

        }
        else {
            console.log("hpciSite");
            if (typeof hpciSiteSuccessHandlerV2 != "undefined") {
                console.log("hpciSite - success");
       		 hpciSiteSuccessHandlerV2(hpciMappedCCValue, hpciMappedCVVValue, hpciCCBINValue);
	         }
            else if (typeof hpciSiteSuccessHandler != "undefined") {
                console.log("hpciSite - handler");
		         hpciSiteSuccessHandler(hpciMappedCCValue, hpciMappedCVVValue);
	         }
	         else {
		         hpciDefaultSiteSuccessHandler(hpciMappedCCValue, hpciMappedCVVValue);
	         }
        }
    }
    else {

        hpciErrCode = hpciUrlParam('hpciErrCode', "?" + e.data);
        hpciErrMsgEncoded = hpciUrlParam('hpciErrMsg', "?" + e.data);
        hpciErrMsg = unescape(hpciErrMsgEncoded);
        console.log("test-hpci");
        if (typeof hpciSiteErrorHandler != "undefined") {
            console.log("test-hpci errorhandler");
	    	 hpciSiteErrorHandler(hpciErrCode, hpciErrMsg);
        }
        else {
            console.log("test-hpci defaultSite");
	    	 hpciDefaultSiteErrorHandler(hpciErrCode, hpciErrMsg);
        }
    }

}

var processNonTokenHPCIMsg = function() { };
processNonTokenHPCIMsg = function(e) {

	 hpciRespMode = hpciUrlParam('hpciRespMode', "?" + e.data);
     hpciMsgStatus = hpciUrlParam('hpciStatus', "?" + e.data);

     // handle preliminary messages
     if (hpciRespMode == "ccprelim") {
    	 if (hpciMsgStatus == "success") {
    		 hpciCCTypeValue = hpciUrlParam('hpciCCType', "?" + e.data);
    		 hpciCCBINValue = hpciUrlParam('hpciCCBIN', "?" + e.data);
    		 hpciCCValidValue = hpciUrlParam('hpciCCValid', "?" + e.data);
    		 hpciCCLengthValue = hpciUrlParam('hpciCCLength', "?" + e.data);
    		 alert("hpciCCTypeValue:" + hpciCCTypeValue + ";" + "hpciCCBINValue:" + hpciCCBINValue + ";" + "hpciCCValidValue:" + hpciCCValidValue + ";" + "hpciCCLengthValue:" + hpciCCLengthValue + ";");
	         if (typeof hpciCCPreliminarySuccessHandler!="undefined") {
	        	 hpciCCPreliminarySuccessHandler(hpciCCTypeValue, hpciCCBINValue, hpciCCValidValue, hpciCCLengthValue);
	         }
    	 }
     }
     else if (hpciRespMode == "cvvprelim") {
    	 if (hpciMsgStatus == "success") {
    		 hpciCVVLengthValue = hpciUrlParam('hpciCVVLength', "?" + e.data);
    		 hpciCVVValidValue = hpciUrlParam('hpciCVVValid', "?" + e.data);
    		 alert("hpciCVVLengthValue:" + hpciCVVLengthValue + ";");
	         if (typeof hpciCVVPreliminarySuccessHandlerV2!="undefined") {
	        	 hpciCVVPreliminarySuccessHandlerV2(hpciCVVLengthValue, hpciCVVValidValue);
	         }
	         else if (typeof hpciCVVPreliminarySuccessHandler!="undefined") {
	        	 hpciCVVPreliminarySuccessHandler(hpciCVVLengthValue);
	         }
    	 }
     }
     else {
	     if (hpciMsgStatus == "success") {
	         hpci3DSecValue = hpciUrlParam('hpci3DSec', "?" + e.data);
	         if (hpci3DSecValue == "reportpinverify") {
	        	  alert("Got hpci3DSecValue:reportpinverify");
		         if (typeof hpci3DSitePINSuccessHandler!="undefined") {
		        	 hpci3DSitePINSuccessHandler();
		         }
		         else {
		        	 hpci3DDefaultSitePINSuccessHandler();
		         }
	         }
	         else {
		         if (typeof hpci3DSitePINErrorHandler!="undefined") {
		        	 hpci3DSitePINErrorHandler();
		         }
		         else {
		        	 hpci3DDefaultSitePINErrorHandler();
		         }
	         }
	     }
	     else {
	         if (typeof hpci3DSitePINErrorHandler!="undefined") {
	        	 hpci3DSitePINErrorHandler();
	         }
	         else {
	        	 hpci3DDefaultSitePINErrorHandler();
	         }
	     }
     }
}

var sendHPCIMsg = function(successCallback, failureCallback) { };
sendHPCIMsg = function (successCallback, failureCallback) {
    try{
        // setup non Conflicting handler
        if (hpciNoConflict != "N") {
            jQuery.noConflict();
        }
        this.successCallbackMDLIVE = successCallback;
        this.failureCallbackMDLIVE = failureCallback;
        // setup receive message handler


        // find the uri
        var url = "" + window.location;
        alert("sendHPCIMsg >>> url : " + url);
        // prepare full message to send/post
        var fullMsg = "";
        var msgConcat = "";

        // define the parameters for 3D Sec
        var defThreeDSecEnabled = false;
        if (typeof hpciThreeDSecEnabled!="undefined" && hpciThreeDSecEnabled) {
            defThreeDSecEnabled = true;
        }

        if (defThreeDSecEnabled) {
            // lookup the parameter names for 3D Sec
            var defExpMonthName = "expMonth";
            var defExpMonthValue = "";
            if (typeof hpciExpMonthName!="undefined" && hpciExpMonthName!="") {
                defExpMonthName = hpciExpMonthName;
            }
            // find the parameter value
            var expMonthInput = document.getElementById(defExpMonthName);
            if (typeof expMonthInput!="undefined") {
                defExpMonthValue = expMonthInput.value;
            }
            if (defExpMonthValue!="") {
                fullMsg = fullMsg + msgConcat + "expMonth=" + defExpMonthValue;
                msgConcat = "&";
            }

            // lookup year
            var defExpYearName = "expYear";
            var defExpYearValue = "";
            if (typeof hpciExpYearName!="undefined" && hpciExpYearName!="") {
                defExpYearName = hpciExpYearName;
            }
            // find the parameter value
            var expYearInput = document.getElementById(defExpYearName);
            if (typeof expYearInput!="undefined") {
                defExpYearValue = expYearInput.value;
            }
            if (defExpYearValue!="") {
                fullMsg = fullMsg + msgConcat + "expYear=" + defExpYearValue;
                msgConcat = "&";
            }

            // lookup message id
            var defMessageIdName = "messageId";
            var defMessageIdValue = "";
            if (typeof hpciMessageIdName!="undefined" && hpciMessageIdName!="") {
                defMessageIdName = hpciMessageIdName;
            }
            // find the parameter value
            var messageIdInput = document.getElementById(defMessageIdName);
            if (typeof messageIdInput!="undefined") {
                defMessageIdValue = messageIdInput.value;
            }
            if (defMessageIdValue!="") {
                fullMsg = fullMsg + msgConcat + "messageId=" + defMessageIdValue;
                msgConcat = "&";
            }

            // lookup transaction id
            var defTransactionIdName = "transactionId";
            var defTransactionIdValue = "";
            if (typeof hpciTransactionIdName!="undefined" && hpciTransactionIdName!="") {
                defTransactionIdName = hpciTransactionIdName;
            }
            // find the parameter value
            var transactionIdInput = document.getElementById(defTransactionIdName);
            if (typeof transactionIdInput!="undefined") {
                defTransactionIdValue = transactionIdInput.value;
            }
            if (defTransactionIdValue!="") {
                fullMsg = fullMsg + msgConcat + "transactionId=" + defTransactionIdValue;
                msgConcat = "&";
            }

            // lookup display ready transaction amount
            var defTranDispAmountName = "tranDispAmount";
            var defTranDispAmountValue = "";
            if (typeof hpciTranDispAmountName!="undefined" && hpciTranDispAmountName!="") {
                defTranDispAmountName = hpciTranDispAmountName;
            }
            // find the parameter value
            var tranDispAmountInput = document.getElementById(defTranDispAmountName);
            if (typeof transactionIdInput!="undefined") {
                defTranDispAmountValue = tranDispAmountInput.value;
            }
            if (defTranDispAmountValue!="") {
                fullMsg = fullMsg + msgConcat + "tranDispAmount=" + defTranDispAmountValue;
                msgConcat = "&";
            }

        }


        //////////////////////////////////////////////////////////////////////

        var ccNumInput = document.getElementById("ccNum");
        var ccCVVInput = document.getElementById("ccCVV");

        alert("ccNumInput : " + ccNumInput.value + " :: ccCVVInput : " + ccCVVInput.value);
        jQuery.post("https://cc.hostedpci.com/iSynSApp/appUserMapCC!createMapedCC.action", { captchaId: captchaIdValue, captchaResp: captchaRespValue, ccNum: ccNumInput.value, ccCVV: ccCVVInput.value, sid: "527122", cvvValidate: "", enableTokenDisplay: "", ccNumTokenIdx: "1", ccNumToken: "", ccCVVToken: "" },
          function (data) {
              alert("Sucess hpciStatus  >>> "+JSON.stringify(data));
              hpciStatus = hpciUrlParam('hpciStatus', "?" + data);
              captchaIdValue = hpciUrlParam('nextCaptchaId', "?" + data);
              captchaRespValue = hpciUrlParam('nextCaptchaResp', "?" + data);

              var hpciCC = hpciUrlParam('hpciCC', "?" + data);
              var hpciCVV = hpciUrlParam('hpciCVV', "?" + data);
              var hpciCCBIN = hpciUrlParam('hpciCCBIN', "?" + data);

              if (hpciStatus == "success")
              {
                  alert("Sucess  >>> "+hpciStatus + " : " + hpciCC + " : " + hpciCVV + " : " + hpciCCBIN);
                  mergeDataBillingWithToken(hpciCC,hpciCVV,hpciCCBIN);

                  //Cordova Call
              }
              else
              {
					alert(hpciStatus);
              }
          }
        );

        return true;
        ///////////////////////////////////////////////////////////////////////

        /*****
        // prepare full message to send/post
        fullMsg = fullMsg + msgConcat + "mapcc-url=" + url;

        //hpciCCFrameFullUrl = "https://cc.hostedpci.com/iSynSApp/showPxyPage!ccFrame.action?pgmode1=stag&locationName=mobile&sid=527122&fullParentHost=x-wmapp0%3A%2Fwww&fullParentQStr=%2FHostedPCI%2Fwww%2Findex.html";
        //hpciCCFrameFullUrl += "?pgmode1=stag&locationName=mobile&sid=527122&fullParentHost=x-wmapp0%3A%2Fwww&fullParentQStr=%2FHostedPCI%2Fwww%2Findex.html"; //"x-wmapp0:www/HostedPCI/www/cciframe.html";
        //hpciCCFrameFullUrl = "file:///C:/Users/conf_p/Desktop/MDLIVE/GIT/WP8/MDLiveMobileApp/MDLiveMobileApp/www/HostedPCI/www/cciframe.html";

        if (hpciStatus != "success") {


            alert(fullMsg + " : " + hpciCCFrameFullUrl + " : " + hpciCCFrameName);

            jQuery.postMessage(
              fullMsg,
              hpciCCFrameFullUrl,
              frames[hpciCCFrameName]
            );


            jQuery.receiveMessage(
          function (e) {
              alert("hpciResp argument :" + e);
              hpciRespMode = hpciUrlParam('hpciRespMode', "?" + e.data);
              alert("hpciRespMode " + hpciRespMode);
              if (hpciRespMode == "") {
                  alert("processCCTokenHPCIMsg");
                  processCCTokenHPCIMsg(e);
              }
              else {
                  processNonTokenHPCIMsg(e);
                  alert("processNonTokenHPCIMsg");
              }
          },
          hpciCCFrameHost
        );

            return false;
        }
        else {
            return true;
        } ****/
    }
    catch (e) {
        console.log("catch :" + e.message);
        alert("catch :"+e.message);
    }

};

var hpci3DDefaultSitePINSuccessHandler = function (){

}

var hpci3DDefaultSitePINErrorHandler = function (){

}

var receivePINEnabled = "";
var receivePINMsg = function() { };
receivePINMsg = function() {
	receiveHPCIMsg();
};

var receiveHPCIMsg = function() { };
receiveHPCIMsg = function() {

	if (receivePINEnabled == "Y")
		return;
	// make sure another listner is not enabled
	receivePINEnabled = "Y";

	// setup receive message handler
	jQuery.receiveMessage(
		  function (e) {

			  processNonTokenHPCIMsg(e);
		  },
		  hpciCCFrameHost
		);
	
};

var sendHPCIChangeStyleMsg = function() { };
sendHPCIChangeStyleMsg = function(elementId, propName, propValue) {

	// setup non Conflicting handler
	if (hpciNoConflict != "N") {
		jQuery.noConflict();
	}
	
	// prepare full message to send/post
	var fullMsg = "msgCmd=changestyle&elementId=" + elementId + "&propName=" + encodeURIComponent(propName) + "&propValue=" + encodeURIComponent(propValue);
	alert("fullMsg     "+fullMsg);
	jQuery.postMessage(
	  fullMsg,
	  hpciCCFrameFullUrl,
	  frames[hpciCCFrameName]
	);
    return true;
	
};
