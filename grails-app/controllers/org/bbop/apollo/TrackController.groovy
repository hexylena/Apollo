package org.bbop.apollo

import grails.converters.JSON
import org.apache.shiro.SecurityUtils
import org.bbop.apollo.gwt.shared.FeatureStringEnum
import org.bbop.apollo.gwt.shared.PermissionEnum
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

class TrackController {

    def permissionService
    def trackService

    def findByName(String name){

    }

    /**
     *
     * Input is track key and projected input.
     * Output is a lookup of name, sequence, etc. to retrieve the proper track data
     *
     *
     * Example input / output
     * [0,291459,294130,1,"amel_OGSv3.2","Group1.10","GB40866-RA",0.763096,"GB40866-RA","mRNA",[[1,291706,291911,1,"amel_OGSv3.2","Group1.10",0,"CDS"],[1,292012,293784,1,"amel_OGSv3.2","Group1.10",2,"CDS"],[2,291459,291595,1,"amel_OGSv3.2","Group1.10",0.763096,"five_prime_UTR"],[2,291696,291706,1,"amel_OGSv3.2","Group1.10",0.763096,"five_prime_UTR"],[2,293784,294130,1,"amel_OGSv3.2","Group1.10",0.763096,"three_prime_UTR"],[2,291459,291595,1,"amel_OGSv3.2","Group1.10",0.763096,"exon"],[2,291696,291911,1,"amel_OGSv3.2","Group1.10",0.763096,"exon"],[2,292012,294130,1,"amel_OGSv3.2","Group1.10",0.763096,"exon"]]]
     */
    def retrieve() {
//        println "request JSON ${request.JSON}"
//        println "params data ${params.data}"
        println "params ${params}"
//        JSONObject requestJson = request.JSON?:JSON.parse(params.data) as JSONObject
        String trackName = params.track
        String organismString = params.organism
        println "organism ${organismString}"
        println "trackName ${trackName}"
        JSONArray inputArray= JSON.parse(params.input) as JSONArray
        println "inputJson ${inputArray as JSON}"


        try {
            String sequenceName = inputArray.getString(5)
            String nameLookup = inputArray.getString(6)

            JSONObject rootElement = new JSONObject()
            rootElement.put(FeatureStringEnum.USERNAME.value, SecurityUtils.subject.principal)
            rootElement.put(FeatureStringEnum.SEQUENCE.value, sequenceName)

            Sequence sequence = permissionService.checkPermissions(rootElement, PermissionEnum.READ)

            println "sequence ${sequence}"
            assert sequence!=null
            assert sequence.name==sequenceName


            JSONArray returnData = trackService.getTrackData(sequence,trackName,nameLookup)
            println "returnData ${returnData as JSON}"

//            render inputArray as JSONArray
            def responseObject = new JSONObject()
            responseObject.organismId = sequence.organismId
            responseObject.trackDetails = returnData

            if(returnData.getInt(0)==0){
                responseObject.start = returnData.getInt(1)
                responseObject.end = returnData.getInt(2)
                responseObject.strand = returnData.getInt(3)
                responseObject.note = returnData.getString(4) // not sure if this is correct or not . . .
                responseObject.seq = returnData.getString(5) // not sure if this is correct or not . . .
                responseObject.name = returnData.getString(6)
                responseObject.score = returnData.getDouble(7)
                responseObject.type = returnData.getString(9)
            }


            render responseObject as JSON

//            println "request JSON ${requestJson as JSON}"


//            else {
//                def error= [error: 'not authorized to add organism']
//                render error as JSON
//                log.error(error.error)
//            }
        } catch (e) {
            def error= [error: 'problem retrieving track: '+e]
            render error as JSON
            e.printStackTrace()
            log.error(error.error)
        }

    }
}