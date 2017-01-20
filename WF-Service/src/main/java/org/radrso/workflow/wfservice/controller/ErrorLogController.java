package org.radrso.workflow.wfservice.controller;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.wfservice.service.WorkflowLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * Created by raomengnan on 17-1-20.
 */
@RestController
@Log4j
@RequestMapping("/logs")
public class ErrorLogController {
    @Autowired
    private WorkflowLogService workflowLogService;

    @RequestMapping("/workflow/{workflowId}")
    public List<WorkflowErrorLog> getByWorkflowId(@PathParam("workflowId") String workflowId){
        return workflowLogService.getByWorkflowId(workflowId);
    }

    @RequestMapping("/instance/{instanceId}")
    public List<WorkflowErrorLog> getByInstanceId(@PathParam("instanceId") String instanceId){
        return workflowLogService.getByInstanceId(instanceId);
    }

    @RequestMapping(value = "/delete/workflow/{workflowId}", method = RequestMethod.DELETE)
    public ResponseEntity<ModelMap> deleteByWorkflowId(@PathParam("workflowId") String workflowId){
        boolean res = workflowLogService.deleteByWorkflowId(workflowId);
        ModelMap map = new ModelMap();
        map.put("status", res);
        if(!res) {
            map.put("msg", String.format("Can not delete by workflowId[%s]", workflowId));
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/instance/{instanceId}", method = RequestMethod.DELETE)
    public ResponseEntity<ModelMap> deleteByInstanceId(@PathParam("instanceId") String instanceId){
        boolean res = workflowLogService.deleteByInstanceId(instanceId);
        ModelMap map = new ModelMap();
        map.put("status", res);
        if(!res) {
            map.put("msg", String.format("Can not delete by instanceId[%s]", instanceId));
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
    }
}
