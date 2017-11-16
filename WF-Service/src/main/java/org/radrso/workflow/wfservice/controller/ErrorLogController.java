package org.radrso.workflow.wfservice.controller;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.entity.model.WorkflowErrorLog;
import org.radrso.workflow.wfservice.service.WorkflowLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/workflow/{workflowId}/")
    public List<WorkflowErrorLog> getByWorkflowId(@PathVariable("workflowId") String workflowId){
        return workflowLogService.getByWorkflowId(workflowId);
    }

    @RequestMapping("/workflow/{workflowId}/pno/{pno}/psize/{psize}/")
    public Page<WorkflowErrorLog> getByWorkflowId(@PathVariable("workflowId") String workflowId,
                                                  @PathVariable("pno") int pno,
                                                  @PathVariable("psize")int psize){
        return workflowLogService.getByWorkflowId(workflowId, pno, psize);
    }

    @RequestMapping("/workflow/{workflowId}/count")
    public int count(@PathVariable("workflowId")  String workflowId){
        return workflowLogService.count(workflowId);
    }

    @RequestMapping("/instance/{instanceId}/")
    public List<WorkflowErrorLog> getByInstanceId(@PathVariable("instanceId") String instanceId){
        return workflowLogService.getByInstanceId(instanceId);
    }

    @RequestMapping("/instance/{instanceId}/pno/{pno}/psize/{psize}/")
    public Page<WorkflowErrorLog> getByInstanceId(@PathVariable("instanceId") String instanceId,
                                                  @PathVariable("pno") int pno,
                                                  @PathVariable("psize")int psize){
        return workflowLogService.getByInstanceId(instanceId, pno, psize);
    }

    @RequestMapping(value = "/workflow/{workflowId}/", method = RequestMethod.DELETE)
    public ResponseEntity<ModelMap> deleteByWorkflowId(@PathVariable("workflowId") String workflowId){
        boolean res = workflowLogService.deleteByWorkflowId(workflowId);
        ModelMap map = new ModelMap();
        map.put("status", res);
        if(!res) {
            map.put("msg", String.format("Can not delete by workflowId[%s]", workflowId));
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/instance/{instanceId}/", method = RequestMethod.DELETE)
    public ResponseEntity<ModelMap> deleteByInstanceId(@PathVariable("instanceId") String instanceId){
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
