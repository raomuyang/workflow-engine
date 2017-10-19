package org.radrso.workflow.internal.actions;

import com.alibaba.dubbo.rpc.RpcException;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import lombok.extern.log4j.Log4j;
import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.schema.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknownExceptionInRunning;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.info.WorkflowResult;
import org.radrso.workflow.entities.info.StepStatus;
import org.radrso.workflow.entities.info.WorkflowErrorLog;
import org.radrso.workflow.entities.info.WorkflowInstance;
import org.radrso.workflow.executor.WorkflowExecutors;
import org.radrso.workflow.resolvers.Resolvers;
import org.radrso.workflow.resolvers.FlowResolver;

import java.util.Date;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
@Log4j
public class OnStepExecAction extends AbstractAction implements Consumer<FlowResolver> {
    public OnStepExecAction(Commander workflowSynchronize) {
        super(workflowSynchronize);
    }


    @Override
    public void accept(@NonNull FlowResolver workflowResolver) throws Exception {
        boolean loopDo = true;
        boolean isReloadJarFile = false;//判断是否已经重新加载jar文件

        while (loopDo) {
            // 在执行之前判断Instance是否被中断
            if (operations.checkIsInstanceInterrupted(workflowResolver.getWorkflowInstance().getInstanceId())) {
                return;
            }

            loopDo = false;

            operations.verifyDate(workflowResolver);
            try {
                workflowResolver.next();
                Step step = workflowResolver.getCurrentStep();
                if (step != null) {
                    StepStatus stepStatus = workflowResolver.getWorkflowInstance().getStepStatusesMap().get(step.getSign());
                    if (stepStatus.getBegin() == null)
                        stepStatus.setBegin(new Date());
                }

                execBranches(workflowResolver);

                log.info("[STEP-START] " + workflowResolver.getWorkflowInstance().getInstanceId() + " " + step.getName() + String.format(" Thread[%s]", Thread.currentThread().getId()));
                Object[] params = workflowResolver.getCurrentStepParams();
                String[] paramNames = workflowResolver.getCurrentStepParamNames();

                WorkflowResult response = null;
                if (step.getCall() != null)
                    response = commander.runStepAction(step, params, paramNames);

                if (response != null) {
                    int code = response.getCode();

                    if (code == ResponseCode.HTTP_OK.code())
                        workflowResolver.updateResponse(step.getSign(), response);

                    else {
                        loopDo = true;
                        if (code == ExceptionCode.CLASS_NOT_FOUND.code() && !isReloadJarFile) {
                            isReloadJarFile = true;
                            String wfId = workflowResolver.getWorkflowInstance().getWorkflowId();
                            String instanceId = workflowResolver.getWorkflowInstance().getInstanceId();
                            WorkflowSchema workflowConfig = commander.getWorkflowConfig(instanceId);

                            if (workflowConfig == null) {
                                throw new WFRuntimeException("No such instance: " + instanceId, ExceptionCode.ILLEGAL_ARGMENT_EXCEPTION.code());
                            }
                            if (workflowConfig != null && !Commander.isDefinedJarsFiles(workflowConfig))
                                throw new WFRuntimeException("No jars to load class:" + response.getMsg(), ExceptionCode.JAR_FILE_NOT_FOUND.code());

                            //若使用RPC执行，最大重试次数为3
                            int retry = 3;
                            while (retry > 0) {
                                try {
                                    retry--;
                                    boolean isImported = commander.jarFilesSync(wfId);
                                    if (isImported)
                                        break;
                                    if (retry < 0)
                                        throw new WFRuntimeException("Jar files import failed", ExceptionCode.JAR_FILE_NOT_FOUND.code());
                                } catch (RpcException e) {
                                    log.error(String.format("The %s times to try rpc:", 6 - retry) + e.getMessage());
                                    if (retry == 0)
                                        throw new WFRuntimeException("RPC invoke timeout[jarFilesSync]", ExceptionCode.SOCKET_EXCEPTION.code());
                                }
                            }
                        } else if (ExceptionCode.CLASS_NOT_FOUND.code() <= code && code <= ExceptionCode.JAR_FILE_NOT_FOUND.code()) {
                            throw new WFRuntimeException(response.getMsg(), code);
                        }

                        //发生错误时，完成错误鉴别后回滚一步
                        workflowResolver.rollback();
                    }
                }
            } catch (ConfigReadException e) {
                System.out.println(e);
                loopDo = true;
                workflowResolver.rollback();
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e1) {
                    log.error(e1);
                }
            } catch (RuntimeException runtimeException) {
                log.error(runtimeException);
                if (WFRuntimeException.class.isInstance(runtimeException))
                    throw runtimeException;

                runtimeException.printStackTrace();
                throw new WFRuntimeException(runtimeException.getMessage(),
                        runtimeException, ExceptionCode.UNKNOW.code());
            } catch (UnknownExceptionInRunning unknownExceptionInRunning) {
                log.error(unknownExceptionInRunning);
                unknownExceptionInRunning.printStackTrace();
                throw new WFRuntimeException(unknownExceptionInRunning.getMessage(),
                        unknownExceptionInRunning, ExceptionCode.UNKNOW.code());
            } finally {
                if (!loopDo) {
                    String stepSign = workflowResolver.getCurrentStep().getSign();
                    workflowResolver.getWorkflowInstance().getStepStatusesMap().get(stepSign).setEnd(new Date());
                }
            }
        }
    }

    private void execBranches(FlowResolver workflowResolver) {
        Step currentStep = workflowResolver.getCurrentStep();
        // 获取分支的转移函数
        Transfer scatterTransfer = workflowResolver.popBranchTransfer();

        // 标记当前是第几个分支
        int num = 0;
        while (scatterTransfer != null) {
            int branchNum = workflowResolver.getWorkflowInstance().getBranches() - num;
            String branchId = workflowResolver.getWorkflowInstance().getInstanceId() + "-" + branchNum;
            num++;

            // 防止重复执行已经执行过的分支
            WorkflowInstance instance = commander.getInstance(branchId);
            if (instance != null && WorkflowInstance.COMPLETED.equals(instance.getStatus())) {
                scatterTransfer = workflowResolver.popBranchTransfer();
                continue;
            }

            String msg = String.format("Scatter to [%s] from step[%s]/[%s]",
                    scatterTransfer.getTo(), currentStep.getSign(), workflowResolver.getWorkflowInstance().getInstanceId());
            log.info(msg);

            // 克隆workflowConfig对象
            String instanceId = workflowResolver.getWorkflowInstance().getInstanceId();
            WorkflowSchema workflowConfig = commander.getWorkflowConfig(instanceId);

            if (workflowConfig == null) {
                WorkflowErrorLog log = new WorkflowErrorLog();
                log.setMsg("Instance not found:" + msg);
                log.setWorkflowId(workflowResolver.getWorkflowInstance().getWorkflowId());
                log.setInstanceId(workflowResolver.getWorkflowInstance().getInstanceId());
                log.setStepSign(workflowResolver.getCurrentStep().getSign());
                commander.saveErrorLog(log);
                continue;
            }

            WorkflowInstance branchInstance = new WorkflowInstance(workflowConfig.getId(), branchId);
            FlowResolver newWFResolver = Resolvers.getFlowResolver(workflowConfig, branchInstance);

            Step tmpLastStep = new Step();
            tmpLastStep.setTransfer(scatterTransfer);
            tmpLastStep.setSign(workflowResolver.getLastStep().getSign());

            scatterTransfer = workflowResolver.popBranchTransfer();
            newWFResolver.setCurrentStep(tmpLastStep);
            WorkflowExecutors.getFlowAction(commander).start(newWFResolver);
        }
    }

    @Override
    public Action setResolver(FlowResolver resolver) {
        return this;
    }
}
