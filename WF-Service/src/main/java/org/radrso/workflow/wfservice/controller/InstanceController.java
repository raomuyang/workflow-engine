package org.radrso.workflow.wfservice.controller;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.radrso.workflow.entities.info.WorkflowResult;
import org.radrso.workflow.entities.info.WorkflowInstance;
import org.radrso.workflow.resolvers.FlowResolver;
import org.radrso.workflow.resolvers.Resolvers;
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
import java.util.Map;

/**
 * Created by raomengnan on 17-1-20.
 */
@RestController
@RequestMapping("/instances")
@Log4j
public class InstanceController {

    @Autowired
    private WorkflowInstanceService workflowInstanceService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private InstanceJobRunner instanceJobRunner;

    /**
     * 通过WorkflowId创建一个workflow instance，是非幂等操作
     * @param workflowId
     * @return
     */
    @RequestMapping(value = "/workflow/{workflowId}/", method = RequestMethod.POST)
    public ResponseEntity<ModelMap> create(@PathVariable("workflowId") String workflowId) {
        ModelMap map = new ModelMap();

        WorkflowInstance instance = workflowInstanceService.newInstance(workflowId);
        if (instance != null) {
            map.put("instance", instance);
            map.put("status", true);
            return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
        } else {
            map.put("status", false);
            map.put("msg", "No such workflow:" + workflowId);
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取workflow下所有的instance
     * @param workflowId
     * @return
     */
    @RequestMapping("/workflow/{workflowId}/")
    public List<WorkflowInstance> getByWorkflowId(@PathVariable("workflowId") String workflowId) {
        return workflowInstanceService.getByWorkflowId(workflowId);
    }

    @RequestMapping("/workflow/{workflowId}/count")
    public int count(@PathVariable("workflowId") String workflowId) {
        return workflowInstanceService.count(workflowId);
    }

    @RequestMapping("/workflow/{workflowId}/finished/count")
    public int countFinished(@PathVariable("workflowId") String workflowId) {
        return workflowInstanceService.countFinished(workflowId);
    }

    @RequestMapping(value = "/workflow/{workflowId}/", method = RequestMethod.DELETE)
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

    @RequestMapping("/{instanceId}")
    public WorkflowInstance getByInstanceId(@PathVariable("instanceId") String instanceId) {
        return workflowInstanceService.getByInstanceId(instanceId);
    }

    /**
     * 与直接以instanceId查询不同的是，这个接口会返回这个工作流实例所有的分支执行信息
     *
     * @param instanceId
     * @return
     */
    @RequestMapping("/{instanceId}/all-details/")
    public List<WorkflowInstance> getAllInstanceDetails(@PathVariable("instanceId") String instanceId) {
        return workflowInstanceService.getInstanceAllBranchesDetail(instanceId);
    }

    @RequestMapping(value = "/{instanceId}", method = RequestMethod.DELETE)
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

    /**
     * 执行工作流实例是幂等操作，
     * 使用PUT方法
     * @param instanceId
     * @return
     */
    @RequestMapping(value = "/{instanceId}/run", method = RequestMethod.PUT)
    public ResponseEntity<ModelMap> runInstance(@PathVariable("instanceId") String instanceId) {
        return runInstance(instanceId, false);
    }

    @RequestMapping(value = "/{instanceId}/interrupt", method = RequestMethod.PUT)
    public ResponseEntity<ModelMap> interrupt(@PathVariable("instanceId") String instanceId){
        boolean res = instanceJobRunner.interrupt(instanceId);
        ModelMap map = new ModelMap();
        map.put("msg", "Already interrupted");
        HttpStatus statusCode = HttpStatus.OK;
        if (!res) {
            String msg = String.format("Interrupt exception, instance %s not found", instanceId);
            map.put("msg", msg);
            statusCode = HttpStatus.NOT_FOUND;
        }
        return  new ResponseEntity<>(map, statusCode);
    }

    @RequestMapping(value = "/{instanceId}/restart", method = RequestMethod.PUT)
    public ResponseEntity<ModelMap> rerunInstance(@PathVariable("instanceId") String instanceId) {
        return runInstance(instanceId, true);
    }

    @RequestMapping("/{instanceId}/finished-steps/")
    public Map finishedSteps(@PathVariable("instanceId") String instanceId) {
        return workflowInstanceService.finishedStep(instanceId);
    }

    @RequestMapping("/{instanceId}/current-process/")
    public Map currentProcess(@PathVariable("instanceId") String instanceId){
        return workflowInstanceService.currentProcess(instanceId);
    }

    private ResponseEntity<ModelMap> runInstance(String instanceId, boolean rerun){
        WorkflowInstance instance = workflowInstanceService.getByInstanceId(instanceId);
        ModelMap map = new ModelMap();
        if (instance == null) {
            map.put("status", false);
            map.put("msg", String.format("No such instance[%s]", instanceId));
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        WorkflowSchema workflowConfig = workflowService.getByWorkflowId(instance.getWorkflowId());

        FlowResolver workflowResolver = Resolvers.getFlowResolver(workflowConfig, instance);
        WorkflowResult response = instanceJobRunner.startExecute(workflowResolver, rerun);
        boolean res = response.getCode() / 100 < 3 ? true : false;
        map.put("status", res);
        map.put("msg", response.getMsg());
        if (res)
            return new ResponseEntity<>(map, HttpStatus.OK);
        else
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
