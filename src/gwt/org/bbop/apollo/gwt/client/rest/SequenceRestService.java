package org.bbop.apollo.gwt.client.rest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import org.bbop.apollo.gwt.client.Annotator;
import org.bbop.apollo.gwt.client.ExportPanel;
import org.bbop.apollo.gwt.client.dto.SequenceInfo;

/**
 * Created by ndunn on 1/14/15.
 */
public class SequenceRestService {

    public static void setCurrentSequence(RequestCallback requestCallback, SequenceInfo sequenceInfo) {
        RestService.sendRequest(requestCallback, "sequence/setCurrentSequence/" + sequenceInfo.getId());
    }


    public static void generateLink(final ExportPanel exportPanel) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", new JSONString(exportPanel.getType()));
        jsonObject.put("seqType", new JSONString(exportPanel.getSequenceType()));
        jsonObject.put("exportAllSequences", new JSONString(exportPanel.getExportAll().toString()));
        jsonObject.put("exportGff3Fasta", new JSONString(exportPanel.getExportGff3Fasta().toString()));
        jsonObject.put("output", new JSONString("file"));
        JSONArray jsonArray = new JSONArray();
        int i=0;
        for (SequenceInfo sequenceInfo : exportPanel.getSequenceList()) {
            jsonArray.set(i++, new JSONString(sequenceInfo.getName()));
        }
        jsonObject.put("sequences",jsonArray);
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                JSONObject responseObject = JSONParser.parseStrict(response.getText()).isObject();
                GWT.log("Received response: "+responseObject.toString());
                String uuid = responseObject.get("uuid").isString().stringValue();
                String exportType = responseObject.get("exportType").isString().stringValue();
                String sequenceType = responseObject.get("seqType").isString().stringValue();
                String exportUrl = Annotator.getRootUrl() + "IOService/download?uuid=" + uuid + "&exportType=" + exportType + "&seqType=" + sequenceType;
                exportPanel.setExportUrl(exportUrl);
            }

            @Override
            public void onError(Request request, Throwable exception) {
                Window.alert("Error: " + exception);
            }
        };

        RestService.sendRequest(requestCallback, "IOService/write", "data=" + jsonObject.toString());
    }

    public static void setCurrentSequenceAndLocation(RequestCallback requestCallback, String sequenceNameString, Integer start, Integer end) {
        setCurrentSequenceAndLocation(requestCallback,sequenceNameString,start,end,false) ;
    }

    public static void setCurrentSequenceAndLocation(RequestCallback requestCallback, String sequenceNameString, Integer start, Integer end,boolean suppressOutput) {
        String url = "sequence/setCurrentSequenceLocation/?name=" + sequenceNameString + "&startbp=" + start + "&endbp=" + end;
        if(suppressOutput){
            url += "&suppressOutput=true";
        }

        RestService.sendRequest(requestCallback, url);
    }

    public static void getSequenceForOffsetAndMax(RequestCallback requestCallback, String text, int start, int length, String sortBy,Boolean sortNameAscending, String minFeatureLengthText, String maxFeatureLengthText) {
        String searchString = "sequence/getSequences/?name=" + text + "&start=" + start + "&length=" + length ;
        if(sortBy!=null && sortBy.length()>1){
            searchString += "&sort="+sortBy+"&asc=" + sortNameAscending;
        }
        try {
            searchString += "&minFeatureLength=" + Integer.parseInt(minFeatureLengthText);
        } catch (NumberFormatException nfe) {
            //
        }
        try {
            searchString += "&maxFeatureLength=" + Integer.parseInt(maxFeatureLengthText);
        } catch (NumberFormatException nfe) {
            //
        }
        RestService.sendRequest(requestCallback, searchString);
    }


}
