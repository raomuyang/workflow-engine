package org.radrso.workflow.wfservice.controller;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * Created by raomengnan on 17-1-20.
 */
@RestController
@RequestMapping("/instance")
@Log4j
public class InstanceController {

    @Autowired
    private WorkflowInstanceService workflowInstanceService;

    @RequestMapping(value = "/new/workflow/{workflowid}", method = RequestMethod.PUT)
    public ResponseEntity<ModelMap> create(@PathVariable("workflowid")String workflowid){
        ModelMap map = new ModelMap();

        WorkflowInstance instance = workflowInstanceService.newInstance(workflowid);
        if(instance != null){
            map.put("instance", instance);
            map.put("status", true);
            return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
        }
        else {
            map.put("status", false);
            map.put("msg", "No such workflow:" + workflowid);
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/{instanceId}")
    public WorkflowInstance getByInstanceId(@PathVariable("instanceId") String instanceId){
        return workflowInstanceService.getByInstanceId(instanceId);
    }

    @RequestMapping("/workflow/{workflowId}")
    public List<WorkflowInstance> getByWorkflowId(@PathVariable("workflowId") String workflowId){
        return workflowInstanceService.getByWorkflowId(workflowId);
    }
}
