
import groovyx.net.http.RESTClient
import net.sf.json.JSONArray
import net.sf.json.JSONObject

@Grab(group = 'org.json', module = 'json', version = '20140107')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7')

static JSONArray assignNewUniqueName(JSONArray inputArray,Map uniqueNamesMap) {
    JSONArray returnArray = new JSONArray()
    String oldUniqueName, newUniqueName
    int idx = 0
    for (def eachEntity : inputArray) {
        oldUniqueName = eachEntity.uniquename
        newUniqueName = generateUniqueName()
        eachEntity.uniquename = newUniqueName
        returnArray.add(idx, eachEntity)
        uniqueNamesMap.put(oldUniqueName, newUniqueName)
        idx += 1
    }
    return returnArray
}

static JSONObject triggerAddFeature(String destinationurl, String username, String password, String organism, String sequenceName, JSONArray featuresArray) {
    URL url = new URL(destinationurl)
    String fullPath = "${url.path}/annotationEditor/addFeature"
    fullPath = fullPath.replaceAll("//","/")
    def addFeatureClient = new RESTClient(url)
    def addFeatureResponse = addFeatureClient.post(
            contentType: 'text/javascript',
            path: fullPath,
            body: [  'username' : username, 'password' : password, 'track' : sequenceName, 'organism' : organism, 'features' : featuresArray ]
    )

    assert addFeatureResponse.status == 200
    if (addFeatureResponse.getData().size() == 0) {
        println "Error: Server did not respond properly while trying to call /addFeature"
        return
    }
    else {
        return addFeatureResponse.getData()

    }
}

static JSONObject triggerAddTranscript(String destinationurl, String username, String password, String organism, String sequenceName, JSONArray featuresArray) {
    URL url = new URL(destinationurl)
    String fullPath = "${url.path}/annotationEditor/addTranscript"
    fullPath = fullPath.replaceAll("//","/")
    def addTranscriptClient = new RESTClient(url)
    def addTranscriptResponse = addTranscriptClient.post(
            contentType: 'text/javascript',
            path: fullPath,
            body: [  'username' : username, 'password' : password, 'track' : sequenceName, 'organism' : organism, 'features' : featuresArray ]
    )

    assert addTranscriptResponse.status == 200
    if (addTranscriptResponse.getData().size() == 0) {
        println "Error: Server did not respond properly while trying to call /addTranscript"
        return
    }
    else {
        return addTranscriptResponse.getData()
    }
}

static String generateUniqueName() {
    return UUID.randomUUID().toString()

}