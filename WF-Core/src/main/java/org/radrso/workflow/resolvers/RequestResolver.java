package org.radrso.workflow.resolvers;

import org.radrso.workflow.entities.model.WorkflowResult;

/**
 * Created by rao-mengnan on 2017/3/16.
 */
public interface RequestResolver {

    /**
     * 调用配置中预设的类方法
     * 如果是可变长的参数，
     * @return  WFResponse中的Response是执行结果的消息实体
     */
    WorkflowResult classRequest();

    /**
     * 调用网络请求
     * @return  WFResponse中的Response是执行结果的消息实体
     */
    WorkflowResult netRequest();

}
