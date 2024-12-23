package com.steven.solomon.entity;

import com.steven.solomon.annotation.JobTask;
import com.steven.solomon.enums.*;
import com.steven.solomon.spring.SpringUtil;
import com.steven.solomon.verification.ValidateUtils;
import com.xxl.job.core.handler.annotation.XxlJob;

import java.util.Date;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
public class XxlJobInfo {
	
	private int id;				// 主键ID
	
	private int jobGroup;		// 执行器主键ID
	private String jobDesc;
	
	private String author;		// 负责人
	private String alarmEmail;	// 报警邮件

	private ScheduleTypeEnum scheduleType;			// 调度类型
	private String scheduleConf;			// 调度配置，值含义取决于调度类型
	private MisfireStrategyEnum misfireStrategy;			// 调度过期策略

	private ExecutorRouteStrategyEnum executorRouteStrategy;	// 执行器路由策略
	private String executorHandler;		    // 执行器，任务Handler名称
	private String executorParam;		    // 执行器，任务参数
	private ExecutorBlockStrategyEnum executorBlockStrategy;	// 阻塞处理策略
	private int executorTimeout;     		// 任务执行超时时间，单位秒
	private int executorFailRetryCount;		// 失败重试次数
	
	private GlueTypeEnum glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	private String glueSource;		// GLUE源代码
	private String glueRemark;		// GLUE备注

	private String childJobId;		// 子任务ID，多个逗号分隔

	private int triggerStatus;		// 调度状态：0-停止，1-运行

	public XxlJobInfo() {
		super();
	}

	public XxlJobInfo(JobTask jobTask,String className) {
		super();
		this.jobGroup = jobTask.jobGroup();
		this.jobDesc = ValidateUtils.getOrDefault(jobTask.taskName(),className);
		String author = null;
		if(ValidateUtils.isEmpty(jobTask.author())){
			author = ValidateUtils.getOrDefault(SpringUtil.getElValue("${spring.application.name}"),className);
		} else {
			author = jobTask.author();
		}
		this.author = author;
		this.alarmEmail = jobTask.alarmEmail();
		this.scheduleType = jobTask.scheduleType();
		this.scheduleConf = jobTask.scheduleConf();
		this.misfireStrategy = jobTask.misfireStrategy();
		this.executorRouteStrategy = jobTask.executorRouteStrategy();
		this.executorParam = jobTask.executorParam();
		this.executorBlockStrategy = jobTask.executorBlockStrategy();
		this.executorTimeout = jobTask.executorTimeout();
		this.executorFailRetryCount = jobTask.executorFailRetryCount();
		this.glueType = jobTask.glueType();
		this.childJobId = jobTask.childJobId();
		this.triggerStatus = jobTask.start() ? 1 : 0;
	}

	public XxlJobInfo update(JobTask jobTask,String className){
		this.jobGroup = jobTask.jobGroup();
		this.jobDesc = ValidateUtils.getOrDefault(jobTask.taskName(),className);
		String author = null;
		if(ValidateUtils.isEmpty(jobTask.author())){
			author = ValidateUtils.getOrDefault(SpringUtil.getElValue("${spring.application.name}"),className);
		} else {
			author = jobTask.author();
		}
		this.author = author;
		this.alarmEmail = jobTask.alarmEmail();
		this.scheduleType = jobTask.scheduleType();
		this.scheduleConf = jobTask.scheduleConf();
		this.misfireStrategy = jobTask.misfireStrategy();
		this.executorRouteStrategy = jobTask.executorRouteStrategy();
		this.executorParam = jobTask.executorParam();
		this.executorBlockStrategy = jobTask.executorBlockStrategy();
		this.executorTimeout = jobTask.executorTimeout();
		this.executorFailRetryCount = jobTask.executorFailRetryCount();
		this.glueType = jobTask.glueType();
		this.childJobId = jobTask.childJobId();
		this.triggerStatus = jobTask.start() ? 1 : 0;
		return this;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(int jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAlarmEmail() {
		return alarmEmail;
	}

	public void setAlarmEmail(String alarmEmail) {
		this.alarmEmail = alarmEmail;
	}

	public ScheduleTypeEnum getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleTypeEnum scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getScheduleConf() {
		return scheduleConf;
	}

	public void setScheduleConf(String scheduleConf) {
		this.scheduleConf = scheduleConf;
	}

	public MisfireStrategyEnum getMisfireStrategy() {
		return misfireStrategy;
	}

	public void setMisfireStrategy(MisfireStrategyEnum misfireStrategy) {
		this.misfireStrategy = misfireStrategy;
	}

	public ExecutorRouteStrategyEnum getExecutorRouteStrategy() {
		return executorRouteStrategy;
	}

	public void setExecutorRouteStrategy(ExecutorRouteStrategyEnum executorRouteStrategy) {
		this.executorRouteStrategy = executorRouteStrategy;
	}

	public String getExecutorHandler() {
		return executorHandler;
	}

	public void setExecutorHandler(String executorHandler) {
		this.executorHandler = executorHandler;
	}

	public String getExecutorParam() {
		return executorParam;
	}

	public void setExecutorParam(String executorParam) {
		this.executorParam = executorParam;
	}

	public ExecutorBlockStrategyEnum getExecutorBlockStrategy() {
		return executorBlockStrategy;
	}

	public void setExecutorBlockStrategy(ExecutorBlockStrategyEnum executorBlockStrategy) {
		this.executorBlockStrategy = executorBlockStrategy;
	}

	public int getExecutorTimeout() {
		return executorTimeout;
	}

	public void setExecutorTimeout(int executorTimeout) {
		this.executorTimeout = executorTimeout;
	}

	public int getExecutorFailRetryCount() {
		return executorFailRetryCount;
	}

	public void setExecutorFailRetryCount(int executorFailRetryCount) {
		this.executorFailRetryCount = executorFailRetryCount;
	}

	public GlueTypeEnum getGlueType() {
		return glueType;
	}

	public void setGlueType(GlueTypeEnum glueType) {
		this.glueType = glueType;
	}

	public String getGlueSource() {
		return glueSource;
	}

	public void setGlueSource(String glueSource) {
		this.glueSource = glueSource;
	}

	public String getGlueRemark() {
		return glueRemark;
	}

	public void setGlueRemark(String glueRemark) {
		this.glueRemark = glueRemark;
	}

	public String getChildJobId() {
		return childJobId;
	}

	public void setChildJobId(String childJobId) {
		this.childJobId = childJobId;
	}

	public int getTriggerStatus() {
		return triggerStatus;
	}

	public void setTriggerStatus(int triggerStatus) {
		this.triggerStatus = triggerStatus;
	}
}
