/**
 * Created by raomengnan on 17-1-24.
 */
var domain = "http://127.0.0.1:8082"

var ERRORS = "/logs/workflow/{workflowid}"
var WORKFLOWS = "/workflow/"

$(document).ready( function(){
    initLogs()
    initWorkflow()
})

function initWorkflow() {
    var $tmp = '<li><a href="logs.html?workflow={id}">{id}</a></li>'

    var workflow = $.getUrlParam("workflow")
    $("#wf-console").attr("href","console.html?workflow="+workflow)
    $("#wf-instances").attr("href","instances.html?workflow="+workflow)

    $.ajax({
        url:domain + WORKFLOWS.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {
            response.forEach(function (e) {
                var $t = $tmp.replace(/{id}/g,e)
                $('#wf-list').append($t)
            })
        },
        error:function (response) {
            console.log(response)
        }
    })
}

function initLogs() {
    var workflow = $.getUrlParam("workflow")

    $.ajax({
        url: domain + ERRORS.replace("{workflowid}", workflow),
        type: "GET",
        success: function (response) {
            var $tmp = '<li class="list-group-item">'+
                '<span class="pull-right" >'+
                '<a href="#">'+
                '<i class="fa fa-times icon-muted fa-fw"></i>'+
                '</a>'+
                '</span>'+

                '<span class="pull-left media-xs">'+
                '<i class="fa fa-paperclip text-muted fa m-r-sm"></i>'+
                '{date}'+
                '</span>'+

                '<div class="clear">&nbsp;&nbsp;&nbsp;&nbsp;' +
                '<a href="instance-info?instance={instance}">{instance}</a>' +
                '&nbsp;&nbsp;&nbsp;&nbsp;' +
                '<font color="#6495ed">{step}</font>' +
                '&nbsp;&nbsp;&nbsp;&nbsp;' +
                '<font color="#a52a2a">{msg}</font>' +
                '</div>'+
                '</li>'
            response.forEach(function (e) {
                var $t = $tmp.replace(/{instance}/g, e["instanceId"])
                    .replace('{step}',e['stepSign'])
                    .replace('{date}',timeStamp2DataStr(e['date']))
                    .replace('{msg}',e['msg'])
                $("#log-list").append($t)

            })
        },
        error: function () {

        }
    })
}
