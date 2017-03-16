package org.radrso.workflow.wfservice.controller;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;
import org.radrso.workflow.resolvers.ResolverChain;
import org.radrso.workflow.wfservice.executor.InstanceJobRunner;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.radrso.workflow.wfservice.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/instance")
@Log4j
public class InstanceController {

    @Autowired
    private WorkflowInstanceService workflowInstanceService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private InstanceJobRunner instanceJobRunner;

    @RequestMapping(value = "/new/workflow/{workflowid}", method = RequestMethod.PUT)
    public ResponseEntity<ModelMap> create(@PathVariable("workflowid") String workflowid) {
        ModelMap map = new ModelMap();

        WorkflowInstance instance = workflowInstanceService.newInstance(workflowid);
        if (instance != null) {
            map.put("instance", instance);
            map.put("status", true);
            return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
        } else {
            map.put("status", false);
            map.put("msg", "No such workflow:" + workflowid);
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/{instanceId}")
    public WorkflowInstance getByInstanceId(@PathVariable("instanceId") String instanceId) {
        return workflowInstanceService.getByInstanceId(instanceId);
    }

    @RequestMapping("/count/workflow/{workflowid}")
    public int count(@PathVariable("workflowid") String workflowid) {
        return workflowInstanceService.count(workflowid);
    }

    @RequestMapping("/count/workflow/{workflowid}/finished")
    public int countFinished(@PathVariable("workflowid") String workflowid) {
        return workflowInstanceService.countFinished(workflowid);
    }

    /**
     * 与直接以instanceId查询不同的是，这个接口会返回这个工作流实例所有的分支执行信息
     *
     * @param instanceId
     * @return
     */
    @RequestMapping("/all-details/{instanceId}")
    public List<WorkflowInstance> getAllInstanceDetails(@PathVariable("instanceId") String instanceId) {
        return workflowInstanceService.getInstanceDetails(instanceId);
    }

    @RequestMapping("/workflow/{workflowId}")
    public List<WorkflowInstance> getByWorkflowId(@PathVariable("workflowId") String workflowId) {
        return workflowInstanceService.getByWorkflowId(workflowId);
    }

    @RequestMapping(value = "/delete/{instanceId}", method = RequestMethod.DELETE)
    public ResponseEntity<ModelMap> deleteByInstanceId(@PathVariable("instanceId") String instanceId) {
        boolean res = workflowInstanceService.delete(instanceId);
        ModelMap map = new ModelMap();
        map.put("status", res);
        if (!res) {
            map.put("msg", String.format("Delete instance[%s] failure", instanceId));
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/workflow/{workflowId}", method = RequestMethod.DELETE)
    public ResponseEntity<ModelMap> deleteByWorkflowId(@PathVariable("workflowId") String workflowId) {
        boolean res = workflowInstanceService.deleteByWorkflowId(workflowId);
        ModelMap map = new ModelMap();
        map.put("status", res);
        if (!res) {
            map.put("msg", String.format("Delete instances of [%s] failure", workflowId));
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/start/{instanceId}", method = RequestMethod.POST)
    public ResponseEntity<ModelMap> startInstance(@PathVariable("instanceId") String instanceId) {
        WorkflowInstance instance = workflowInstanceService.getByInstanceId(instanceId);
        ModelMap map = new ModelMap();
        if (instance == null) {
            map.put("status", false);
            map.put("msg", String.format("No such instance[%s]", instanceId));
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }
        WorkflowConfig workflowConfig = workflowService.getByWorkflowId(instance.getWorkflowId());

        BaseWorkflowConfigResolver workflowResolver = ResolverChain.getWorkflowConfigResolver(workflowConfig, instance);
        WFResponse response = instanceJobRunner.startExecute(workflowResolver);
        boolean res = response.getCode() / 100 < 3 ? true : false;
        map.put("status", res);
        map.put("msg", response.getMsg());

        if (res)
            return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
        else
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
    }

}
