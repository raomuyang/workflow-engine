package org.radrso.workflow.wfservice.service.impl;

import org.bson.types.ObjectId;
import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.radrso.workflow.entities.model.WorkflowInstance;
import org.radrso.workflow.wfservice.repositories.WorkflowInstanceRepository;
import org.radrso.workflow.wfservice.repositories.WorkflowRepository;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.radrso.workflow.wfservice.utils.MongoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-17.
 */
@Service
public class WorkflowInstanceServiceImpl implements WorkflowInstanceService{
    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public WorkflowInstance newInstance(String workflowId) {
        WorkflowSchema workflowConfig = workflowRepository.findOne(workflowId);
        if(workflowConfig == null)
            return null;

        ObjectId id = new ObjectId();
        WorkflowInstance workflowInstance = new WorkflowInstance(workflowId, id.toHexString());
        save(workflowInstance);
        return workflowInstance;
    }

    @Override
    public boolean save(WorkflowInstance workflowInstance) {
        if(workflowInstance == null || workflowInstance.getInstanceId() == null)
            return false;
        workflowInstanceRepository.save(workflowInstance);
        return true;
    }

    @Override
    public WorkflowInstance getByInstanceId(String instanceId) {
        if(instanceId == null)
            return null;
        return workflowInstanceRepository.findOne(instanceId);
    }

    @Override
    public List<WorkflowInstance> getInstanceAllBranchesDetail(String instanceId){
        WorkflowInstance instance = getByInstanceId(instanceId);
        if(instance == null)
            return new ArrayList<>();

        Query query = new Query(MongoUtil.fuzzyCriteria("_id",instanceId));
        List<WorkflowInstance> instances = mongoTemplate.find(query ,WorkflowInstance.class);
        instances.add(0, instance);
        return instances;
    }

    @Override
    public List<WorkflowInstance> getByWorkflowId(String workflowId) {
        if(workflowId == null)
            return new ArrayList<>();
        List<WorkflowInstance> instances = workflowInstanceRepository.findByWorkflowId(workflowId);
        List<WorkflowInstance> pureInstances = new ArrayList<>();
        if(instances != null)
            for (WorkflowInstance i: instances) {
                if(!i.getInstanceId().contains("-"))
                    pureInstances.add(i);
            }
        return pureInstances;
    }

    @Override
    public int count(String workflowId){
        List<WorkflowInstance> instances = getByWorkflowId(workflowId);

        int len = 0;
        if(instances == null)
            return 0;
        else{

            for (WorkflowInstance i:
                 instances) {
                if(i.getInstanceId().contains("-"))
                    len++;
            }
        }
        return instances.size() - len;

    }

    @Override
    public int countFinished(String workflowId){
        List<WorkflowInstance> instances = getByWorkflowId(workflowId);
        if(instances == null)
            return 0;
        int i = 0;
        for(int j = 0; j < instances.size(); ++j){
            WorkflowInstance instance = instances.get(j);
            if(instance.getStatus().equals(WorkflowInstance.COMPLETED) && !instance.getInstanceId().contains("-"))
                ++i;
        }
        return i;
    }


    /**
     * 返回instance中各个分所有步骤的执行状态
     * @param instanceId
     * @return
     */
    @Override
    public Map<String, Map> currentProcess(String instanceId) {
        List<WorkflowInstance> instanceDetails = getInstanceAllBranchesDetail(instanceId);
        Map<String, Map> map = new HashMap<>();
        if (instanceDetails != null){
            for (WorkflowInstance instance: instanceDetails) {
                map.put(instance.getInstanceId(), instance.getStepProcess());
            }
        }
        return map;
    }

    /**
     * 返回instance各个分支已完成的step列表
     * @param instanceId
     * @return
     */
    @Override
    public Map<String, List<String>> finishedStep(String instanceId) {
        Map<String, List<String>> map = new HashMap<>();

        List<WorkflowInstance> instanceDetails = getInstanceAllBranchesDetail(instanceId);
        if (instanceDetails != null){
            for (WorkflowInstance instance: instanceDetails) {
                map.put(instance.getInstanceId(), instance.getFinishedSequence());
            }
        }
        return map;
    }

    @Override
    public boolean delete(String instanceId) {
        if(instanceId == null)
            return false;
        workflowInstanceRepository.delete(instanceId);
        return true;
    }

    @Override
    public boolean deleteByWorkflowId(String workflowId) {
        if(workflowId == null)
            return false;
        workflowInstanceRepository.deleteByWorkflowId(workflowId);
        return true;
    }
}
