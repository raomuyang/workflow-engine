package org.radrso.workflow.wfservice.controller;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.DateTools;
import org.radrso.workflow.entity.model.JarFile;
import org.radrso.workflow.entity.schema.WorkflowSchema;
import org.radrso.workflow.entity.model.WorkflowRuntimeState;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.radrso.workflow.wfservice.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by raomengnan on 17-1-20.
 */
@RestController
@RequestMapping(value = "/workflow")
@Log4j
public class WorkflowConfroller {

    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private WorkflowExecuteStatusService statusService;

    @RequestMapping("/")
    public List<String> getAllWFId(){
        List<WorkflowSchema> wfs = workflowService.getAll();
        List<String> wfIds = new ArrayList<>();
        if(wfs != null)
            wfs.forEach(wf->{
                wfIds.add(wf.getId());
            });
        return wfIds;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/")
    public ResponseEntity<ModelMap> create(@RequestBody WorkflowSchema workflow){
        boolean res = workflowService.save(workflow);
        ModelMap map = new ModelMap();
        map.put("status", res);
        if(!res) {
            map.put("msg", "请检查后重试");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }else
            workflowService.updateServiceStatus(workflow);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping("/pno/{pno}/psize/{psize}/")
    public Page<WorkflowSchema> getAllInfos(@PathVariable("pno") int pno, @PathVariable("psize")int psize){
        return workflowService.getAll(pno, psize);
    }

    @RequestMapping("/{workflowId}")
    public WorkflowSchema getWorkflowById(@PathVariable("workflowId") String id){
        return workflowService.getByWorkflowId(id);
    }

    @RequestMapping(value = "/{workflowId}", method = RequestMethod.DELETE)
    public ResponseEntity<ModelMap> delete(@PathVariable("workflowId") String workflowId){
        boolean res = workflowService.delete(workflowId);
        ModelMap map = new ModelMap();
        map.put("status", res);
        if(!res){
            map.put("msg", String.format("Delete [%s] error", workflowId));
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/{application}/jars/", method = RequestMethod.POST)
    public ResponseEntity<ModelMap> uploadJar(@PathVariable("application") String application, MultipartFile file){
        log.info(String.format("Upload jar file [%s] for [%s]", file.getOriginalFilename(), application));
        boolean res = workflowService.transferJarFile(application, file);

        ModelMap map = new ModelMap();
        map.put("status", res);

        if(!res){
            map.put("msg", "Upload error, pleas apply the applicationId");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/{application}/jars/", method = RequestMethod.GET)
    public List<JarFile> listApplicationJar(@PathVariable("application") String application){
        log.info(String.format("List jar files of [%s]", application));
        return workflowService.listApplicationJarFiles(application);
    }

    @RequestMapping("/{workflowId}/status")
    public WorkflowRuntimeState getWorkflowStatus(@PathVariable("workflowId") String workflowId){
        workflowService.updateServiceStatus(workflowService.getByWorkflowId(workflowId));
        return statusService.get(workflowId);
    }

    /**
     * 幂等操作
     */
    @RequestMapping(value = "/{workflowId}/restart/stop-in/{deadline}", method = RequestMethod.PUT)
    public ResponseEntity<ModelMap> restartWorkflow(@PathVariable("workflowId") String workflowId, @PathVariable("deadline") String deadline){
        Date date = DateTools.string2Date(deadline);
        boolean res = workflowService.restartWorkflow(workflowId, date);
        ModelMap modelMap = new ModelMap();
        modelMap.put("status", res);
        String msg = String.format("Restart workflow %s successful, deadline is %s", workflowId, deadline);
        HttpStatus statusCode = HttpStatus.OK;
        if (!res) {
            msg = String.format("Restart workflow %s successful, deadline is %s, please apply it", workflowId, deadline);
            statusCode = HttpStatus.BAD_REQUEST;
        }
        modelMap.put("msg", msg);
        return new ResponseEntity<>(modelMap, statusCode);
    }

    /**
     * 幂等操作
     */
    @RequestMapping(value = "/{workflowId}/stop", method = RequestMethod.PUT)
    public ResponseEntity<ModelMap> stopWorkflow(@PathVariable("workflowId") String workflowId){
        workflowService.stopWorkflow(workflowId);
        ModelMap modelMap = new ModelMap();
        modelMap.put("status", true);
        String msg = String.format("Stop workflow %s successful", workflowId);
        HttpStatus statusCode = HttpStatus.OK;
        modelMap.put("msg", msg);
        return new ResponseEntity<>(modelMap, statusCode);
    }

    @RequestMapping("/status/pno/{pno}/psize/{psize}/")
    public Page<WorkflowRuntimeState> getAllStatus(@PathVariable("pno") int pno, @PathVariable("psize")int psize){
        return statusService.getAll(pno, psize);
    }

    /**
     * 更新工作流配置，幂等操作
     * @param workflow
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/update")
    public ResponseEntity<ModelMap> update(@RequestBody WorkflowSchema workflow){

        boolean res = false;
        if(workflow.getId() != null
                && workflowService.getByWorkflowId(workflow.getId()) != null)
            res = workflowService.save(workflow);

        ModelMap map = new ModelMap();
        map.put("status", res);
        if(!res) {
            map.put("msg", "更新失败");
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }else
            workflowService.updateServiceStatus(workflow);

        return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
    }

    @RequestMapping("/app/{application}/")
    public List<WorkflowSchema> getWorkflowByApplication(@PathVariable("application") String application){
        return workflowService.getByApplication(application);
    }

    @RequestMapping(value = "/app/{application}/", method = RequestMethod.DELETE)
    public ResponseEntity<ModelMap> deleteByApplication(@PathVariable("application") String application){
        boolean res = workflowService.deleteByApplication(application);
        ModelMap map = new ModelMap();
        map.put("status", res);
        if(!res){
            map.put("msg", String.format("Delete by [%s] error", application));
            return new ResponseEntity<ModelMap>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
    }
}
