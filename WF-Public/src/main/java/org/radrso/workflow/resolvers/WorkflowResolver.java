package org.radrso.workflow.resolvers;

import com.google.gson.JsonObject;
import lombok.Data;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.InputItem;
import org.radrso.workflow.entities.config.items.Judge;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.provider.CustomClassLoader;
import org.radrso.workflow.provider.JsonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by raomengnan on 17-1-14.
 */
@Data
public class WorkflowResolver implements Serializable{
    public static final String START = "START";
    public static final String FINISH = "FINISH";
    public static final String OUTPUT = "{output}";
    public static final String INSTANCE_ID = "{instanceid}";

    private String header;
    private WorkflowInstance workflowInstance;

    private Step currentStep;
    private ConcurrentHashMap<String, Step> stepMap;
    private ConcurrentHashMap<String, WFResponse> responseMap;

    private List<Step> scatterSteps;

    public WorkflowResolver(){
        this.stepMap = new ConcurrentHashMap<>();
    }

    public WorkflowResolver(WorkflowConfig workflowConfig, WorkflowInstance workflowInstance){
        this();
        this.header = workflowConfig.getHeader();
        this.workflowInstance = workflowInstance;

        this.workflowInstance.setApplicationId(workflowConfig.getId());
        this.responseMap = workflowInstance.getStepResponses();

        workflowConfig.getSteps().forEach(step -> {
            stepMap.put(step.getSign(), step);
            workflowInstance.getStepProcess().put(step.getSign(), Step.WAIT);
        });
    }


    public Step getCurrentStep(){
        if(currentStep == null && stepMap.size() > 0) {
            Step s = this.stepMap.get(START);
            this.currentStep = s;
        }
        return currentStep;
    }

    public Transfer getCurrentTransfer(){
        if(getCurrentStep() == null)
            return null;
        else {
            return currentStep.getTransfer();
        }
    }

    /**
     * 通过sign-params 的映射返回当前状态的入参
     * @return
     */
    public Object[] getCurrentStepParams(){
        return workflowInstance.getStepParams().get(getCurrentStep().getSign());
    }

    public String[] getCurrentStepParamNames(){
        return workflowInstance.getStepParamNames().get(getCurrentStep().getSign());
    }

    /**
     * 工作流程向下个状态转移一次:
     *      当前状态 -- > 当前转移函数 -- > 下一个转移状态 + 分支状态
     * @return
     * @throws ConfigReadException
     */
    public WorkflowResolver next() throws ConfigReadException {
        Transfer currentTransfer = getCurrentTransfer();
        if(currentTransfer == null)
            return null;

        Step nextStep = transferToNextStep(currentTransfer);

        workflowInstance.getStepProcess().put(currentStep.getSign(), Step.RUNNING);
        currentStep = nextStep;
        workflowInstance.getStepProcess().put(currentStep.getSign(), Step.RUNNING);
        return this;
    }

    /**
     *
     * @return 当前是否为最后一步
     */
    public boolean eof(){
        Step step = getCurrentStep();
        if(step == null)
            return true;
        if(getCurrentStep().getSign().equals(FINISH))
            return true;
        return false;
    }


    /**
     * 状态转移函数到下一个状态的转换
     * 若没有判断函数，则直接转移到状态转移函数定义的下一个状态以及分支状态
     * 若有判断函数，判断函数 --> 状态转移函数 --> 下一状态
     * @param transfer
     * @return
     * @throws ConfigReadException
     */
    public Step transferToNextStep(Transfer transfer) throws ConfigReadException {
        if(transfer.getJudge() == null) {
            scatterSteps = scatterTo(transfer);
            return stepMap.get(transfer.getTo());
        }

        Transfer nextTransfer = judgeNextTransfer(transfer.getJudge());
        return transferToNextStep(nextTransfer);
    }

    /**
     * @param transfer
     * @return 非空的一个List
     */
    public List<Step> scatterTo(Transfer transfer){
        List<String> stepNames = transfer.getScatters();
        List<Step> scatterSteps = new ArrayList<>();

        if(scatterSteps != null && scatterSteps.size() > 0)
            for(int i = 0; i < scatterSteps.size(); i++ )
                scatterSteps.add(stepMap.get(stepNames.get(0)));

        return scatterSteps;
    }
    /**
     * 通过判断函数获取下一个要转移的的状态
     * @param judge
     * @return
     * @throws ConfigReadException
     */
    public Transfer judgeNextTransfer(Judge judge) throws ConfigReadException {
        Object computer = judge.getCompute();
        Object computerWith = judge.getComputeWith();
        String type = judge.getType();
        computer = parseValue(type, resolverParamString(computer.toString()));
        computerWith = parseValue(type, resolverParamString(computerWith.toString()));

        Comparable a = (Comparable) computer;
        Comparable b = (Comparable) computerWith;

        String condition = judge.getExpression();
        switch (condition){
            case ">":
                return (a.compareTo(b) > 0)?judge.getPassTransfer():judge.getNopassTransfer();
            case "=":
            case "==":
                return (a.compareTo(b) == 0)?judge.getPassTransfer():judge.getNopassTransfer();
            case "<":
                return (a.compareTo(b) < 0)?judge.getPassTransfer():judge.getNopassTransfer();
            case  ">=":
                return (a.compareTo(b) >= 0)?judge.getPassTransfer():judge.getNopassTransfer();
            case "<=":
                return (a.compareTo(b) <= 0)?judge.getPassTransfer():judge.getNopassTransfer();
            case "&&":
                return ((Boolean)computer && (Boolean)computerWith)?judge.getPassTransfer():judge.getNopassTransfer();
            case "||":
                return ((Boolean)computer || (Boolean)computerWith)?judge.getPassTransfer():judge.getNopassTransfer();
            default:
                throw new ConfigReadException("Unknow expression: " + condition);
        }
    }

    /**
     * 从状态转移函数中解析参数
     * @param transfer 状态转移函数
     * @return 返回的是参数集合
     * @throws ConfigReadException
     */
    public Object[] getParams(Transfer transfer) throws ConfigReadException {
        List<InputItem> inputs = transfer.getInput();
        if(inputs == null || inputs.size() ==0)
            return new Object[]{};

        Object[] params = new Object[inputs.size()];
        String[] paramsNames = new String[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            InputItem inputItem = inputs.get(i);
            Object value = resolverParamString(inputItem.getValue());
            String type = inputItem.getType();
            value = parseValue(type, value);
            params[i] = value;
            paramsNames[i] = inputItem.getName();
        }

        workflowInstance.getStepParams().put(transfer.getTo(), params);
        workflowInstance.getStepParamNames().put(transfer.getTo(), paramsNames);
        return params;
    }

    /**
     * 配置文件中的表达式语句  {output}[sign-xxx][xxx][xxx]
     * @param paramStr
     * @return
     */
    public Object resolverParamString(String paramStr) throws ConfigReadException {

        String errorMsg = null;

        if(paramStr.toLowerCase().equals(INSTANCE_ID))
            return workflowInstance.getInstanceId();

        if(paramStr.indexOf(OUTPUT) >= 0){

            int i = 2;
            String[] sp = null;
            Object ret = null;
            try {
                paramStr = paramStr.replaceAll("\\[", ".").replaceAll("]", "");
                sp = paramStr.split("\\.");
                Object output = responseMap.get(sp[1]).getResponse();
                ret = output;
                for (; i < sp.length; ++i)
                    ret = ((JsonObject) ret).get(sp[i]);

            }catch (IndexOutOfBoundsException e1){
                errorMsg = "Read Config Error:" + e1.getMessage();
            } catch (NullPointerException e2){
                errorMsg = "No such value: " + paramStr + "/" + sp[i];
            } catch (Throwable e3){
                errorMsg = e3.getMessage();
            }

            if(errorMsg != null)
                throw new ConfigReadException(errorMsg);

            try {
                if( ( (JsonObject)ret ).isJsonArray())
                    return ( (JsonObject)ret ).getAsJsonArray();
            }catch (Exception e){
                System.out.println("Not a Json Object");
            }
            return ret;
        }

        return paramStr;
    }


    private Object parseValue(String type, Object o) throws ConfigReadException {
        if(type == null)
            return o.toString();
        try {
            type = casePrimitiveType(type);
            Class clazz = CustomClassLoader.getClassLoader().loadClass(type);
            return JsonUtils.mapToBean(o.toString(), clazz);
        } catch (ClassNotFoundException e) {
            throw new ConfigReadException(String.format("Param type error: (%s)", type) + e.getMessage());
        } catch (Throwable throwable){
            throw new ConfigReadException("Value case error: " + throwable.getMessage());
        }

    }

    private String casePrimitiveType(String type){
        if(type.indexOf(".") >= 0)
            return type;

        type = type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase();
        if("Date".equals(type))
            return "java.util." + type;
        if(type.equals("Int"))
            type = "Integer";
        return "java.lang." + type;
    }

    public void putResponse(String sign, WFResponse response){
        responseMap.put(sign, response);
    }
}
