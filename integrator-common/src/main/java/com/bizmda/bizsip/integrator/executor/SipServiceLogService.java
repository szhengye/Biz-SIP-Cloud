package com.bizmda.bizsip.integrator.executor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizConstant;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.db.dao.SipServiceLogDao;
import com.bizmda.bizsip.db.entity.SipServiceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SipServiceLogService {
    @Autowired
    private SipServiceLogDao sipServiceLogDao;

    public void saveSuccessServiceLog(BizMessage<JSONObject> inBizMessage, BizMessage<JSONObject> outBizMessage) {
        SipServiceLog sipServiceLog = new SipServiceLog();
        sipServiceLog.setTraceId(inBizMessage.getTraceId());
        sipServiceLog.setBeginTime(DateUtil.date(inBizMessage.getTimestamp()));
        sipServiceLog.setEndTime(DateUtil.date());
        sipServiceLog.setCode(outBizMessage.getCode());
        sipServiceLog.setMessage(outBizMessage.getMessage());
        sipServiceLog.setExtMessage(outBizMessage.getExtMessage());
        sipServiceLog.setParentTraceId(inBizMessage.getParentTraceId());
        sipServiceLog.setRequestData(JSONUtil.toJsonStr(inBizMessage.getData()));
        sipServiceLog.setResponseData(JSONUtil.toJsonStr(outBizMessage.getData()));
        sipServiceLog.setServiceStatus(BizConstant.SERVICE_STATUS_SUCCESS);
        this.sipServiceLogDao.saveOrUpdate(sipServiceLog);
    }

    public void saveProcessingServiceLog(BizMessage<JSONObject> inBizMessage) {
        SipServiceLog sipServiceLog = new SipServiceLog();
        sipServiceLog.setTraceId(inBizMessage.getTraceId());
        sipServiceLog.setBeginTime(DateUtil.date(inBizMessage.getTimestamp()));
        sipServiceLog.setEndTime(DateUtil.date());
        sipServiceLog.setParentTraceId(inBizMessage.getParentTraceId());
        sipServiceLog.setRequestData(JSONUtil.toJsonStr(inBizMessage.getData()));
        sipServiceLog.setServiceStatus(BizConstant.SERVICE_STATUS_PROCESSING);
        this.sipServiceLogDao.saveOrUpdate(sipServiceLog);
    }

    public void saveErrorServiceLog(BizMessage<JSONObject> inBizMessage,BizMessage<JSONObject> outBizMessage) {
        SipServiceLog sipServiceLog = new SipServiceLog();
        sipServiceLog.setTraceId(inBizMessage.getTraceId());
        sipServiceLog.setBeginTime(DateUtil.date(inBizMessage.getTimestamp()));
        sipServiceLog.setEndTime(DateUtil.date());
        sipServiceLog.setCode(outBizMessage.getCode());
        sipServiceLog.setMessage(outBizMessage.getMessage());
        sipServiceLog.setExtMessage(outBizMessage.getExtMessage());
        sipServiceLog.setParentTraceId(inBizMessage.getParentTraceId());
        sipServiceLog.setRequestData(JSONUtil.toJsonStr(inBizMessage.getData()));
        sipServiceLog.setResponseData(JSONUtil.toJsonStr(outBizMessage.getData()));
        sipServiceLog.setServiceStatus(BizConstant.SERVICE_STATUS_ERROR);
        this.sipServiceLogDao.saveOrUpdate(sipServiceLog);

        if (inBizMessage.getParentTraceId() == null) {
            return;
        }
        // 更新父服务状态
        if (outBizMessage.getCode() == 0) {
            return;
        }
        this.updateErrorStatusOfParentService(inBizMessage.getParentTraceId());
    }

    private void updateErrorStatusOfParentService(String parentTraceId) {
        if (parentTraceId == null) {
            return;
        }
        SipServiceLog parentSipServiceLog = this.sipServiceLogDao.getById(parentTraceId);
        if (parentSipServiceLog == null) {
            return;
        }
        if (parentSipServiceLog.getServiceStatus().equals(BizConstant.SERVICE_STATUS_SUCCESS)) {
            parentSipServiceLog.setServiceStatus(BizConstant.SERVICE_STATUS_ERROR);
            this.sipServiceLogDao.updateById(parentSipServiceLog);
        }
        if (parentSipServiceLog.getParentTraceId() == null) {
            return;
        }
        this.updateErrorStatusOfParentService(parentSipServiceLog.getParentTraceId());
    }
}
