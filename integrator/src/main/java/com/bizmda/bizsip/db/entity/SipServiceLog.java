package com.bizmda.bizsip.db.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * (sip_service_log)实体类
 *
 * @author 史正烨
 * @since 2021-01-07 11:10:43
 * @description 由 Mybatisplus Code Generator 创建
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sip_service_log")
public class SipServiceLog extends Model<SipServiceLog> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 服务跟踪ID
     */
    @TableId
	private String traceId;
    /**
     * 父服务跟踪ID
     */
    private String parentTraceId;
    /**
     * 服务开始时间
     */
    private Date beginTime;
    /**
     * 服务结束时间
     */
    private Date endTime;
    /**
     * 返回码
     */
    private Integer code;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回扩展消息
     */
    private String extMessage;
    /**
     * 请求数据
     */
    private Object requestData;
    /**
     * 响应数据
     */
    private Object responseData;
    /**
     * 服务状态
     */
    private String serviceStatus;

}