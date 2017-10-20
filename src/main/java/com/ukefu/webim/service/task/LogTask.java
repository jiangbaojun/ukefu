package com.ukefu.webim.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ukefu.core.UKDataContext;
import com.ukefu.webim.service.repository.LogRepository;
import com.ukefu.webim.web.model.Log;

@Configuration
@EnableScheduling
public class LogTask {
	
	@Autowired
	private LogRepository logRes;
	
	@Scheduled(fixedDelay= 1000) // 每5秒执行一次
	public void log(){
		/**
    	 * 日志处理
    	 */
    	Log log = null ;
    	while((log = UKDataContext.tempLogQueue.poll()) != null){
			logRes.save(log) ;
		}
	}
}
