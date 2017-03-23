package org.radrso.workflow.resolvers;

import org.radrso.workflow.entities.response.WFResponse;

/**
 * Created by rao-mengnan on 2017/3/16.
 */
public interface BaseStepExecuteResolver {

    /**
     * 调用配置中预设的类方法
     * 如果是可变长的参数，
     * @return  WFResponse中的Response是执行结果的消息实体
     */
    WFResponse classRequest();

    /**
     * 调用网络请求
     * @return  WFResponse中的Response是执行结果的消息实体
     */
    WFResponse netRequest();

}
