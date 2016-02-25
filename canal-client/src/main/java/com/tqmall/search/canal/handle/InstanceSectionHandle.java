package com.tqmall.search.canal.handle;

import com.tqmall.search.canal.RowChangedData;
import com.tqmall.search.canal.action.InstanceAction;
import com.tqmall.search.commons.lang.Function;
import com.tqmall.search.commons.utils.CommonsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xing on 16/2/23.
 * canalInstance级别的处理, schema, table的区分完全自己实现
 * 因为基于canal instance级别执行更新, 所以每次获取数据{@link #messageBatchSize}不宜过大, 该类默认100
 *
 * @see #setMessageBatchSize(int)
 * @see InstanceAction
 */
public class InstanceSectionHandle extends AbstractCanalInstanceHandle {

    private static final Logger log = LoggerFactory.getLogger(InstanceSectionHandle.class);

    /**
     * 待处理数据集合列表
     * 只能canal获取数据的线程访问, 线程不安全的
     */
    private final List<InstanceRowChangedData> rowChangedData = new LinkedList<>();

    private final InstanceAction instanceAction;

    /**
     * 异常处理方法, 返回结果表示是否忽略, 如果返回null 则为false, 即不忽略, 默认不忽略
     */
    private Function<ExceptionContext, Boolean> exceptionHandleFunction;

    /**
     * @param address        canal服务器地址
     * @param instanceAction canalInstance实例对应的处理Action
     */
    public InstanceSectionHandle(SocketAddress address, InstanceAction instanceAction) {
        super(address, instanceAction.instanceName());
        this.instanceAction = instanceAction;
        setMessageBatchSize(100);
    }

    @Override
    protected void doConnect() {
        canalConnector.connect();
        canalConnector.subscribe();
    }

    @Override
    protected void doRowChangeHandle(List<? extends RowChangedData> changedData) {
        rowChangedData.add(new InstanceRowChangedData(currentHandleSchema,
                currentHandleTable, currentEventType, changedData));
    }

    @Override
    protected void doFinishHandle() {
        instanceAction.onAction(rowChangedData);
        rowChangedData.clear();
    }

    /**
     * @param exceptionHandleFunction 异常处理方法, 返回结果表示是否忽略, 如果返回null 则为false, 即不忽略, 默认不忽略
     */
    public void setExceptionHandleFunction(Function<ExceptionContext, Boolean> exceptionHandleFunction) {
        this.exceptionHandleFunction = exceptionHandleFunction;
    }

    @Override
    protected boolean exceptionHandle(RuntimeException exception, boolean inFinishHandle) {
        try {
            if (exceptionHandleFunction == null) {
                log.error("canal " + instanceName + " handle table data change occurring exception, rowChangedData size: "
                        + rowChangedData.size(), exception);
                return false;
            } else {
                Boolean ignore = exceptionHandleFunction.apply(new ExceptionContext(exception, rowChangedData));
                return ignore == null ? false : ignore;
            }
        } finally {
            if (!rowChangedData.isEmpty()) rowChangedData.clear();
        }
    }

    public static class ExceptionContext {

        private RuntimeException exception;

        private List<InstanceRowChangedData> changedData;

        public ExceptionContext(RuntimeException exception, List<InstanceRowChangedData> changedData) {
            this.exception = exception;
            this.changedData = CommonsUtils.isEmpty(changedData) ? Collections.<InstanceRowChangedData>emptyList()
                    : Collections.unmodifiableList(changedData);
        }

        public List<InstanceRowChangedData> getChangedData() {
            return changedData;
        }

        public RuntimeException getException() {
            return exception;
        }
    }
}