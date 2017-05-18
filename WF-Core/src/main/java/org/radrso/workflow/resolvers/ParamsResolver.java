package org.radrso.workflow.resolvers;

import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknowExceptionInRunning;

/**
 * Created by rao-mengnan on 2017/3/16.
 */
public interface ParamsResolver {
    /**
     * 从状态转移函数中解析参数，参数会自动put到StepStatusMap的StepStatus中
     * @param transfer 状态转移函数
     * @return 返回的是参数集合
     */
    Object[] resolverTransferParams(Transfer transfer) throws ConfigReadException, UnknowExceptionInRunning;

    /**
     * 将配置文件中的表达式语句转为有效值，但不是最终的类型
     * @param paramStr 如{output}[x-step-sign][xxx][xxx]
     * @return  返回转义之后的值
     */
    Object resolverStringToParams(String paramStr) throws UnknowExceptionInRunning, ConfigReadException;

    /**
     * 将对象转换为目标类型
     * @param type
     * @param o
     * @return
     * @throws ConfigReadException
     */
    Object parseValue(String type, Object o) throws ConfigReadException;
}
