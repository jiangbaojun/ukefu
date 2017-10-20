package com.ukefu.webim.util.log;

import java.util.Date;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.ukefu.core.UKDataContext;
import com.ukefu.util.UKTools;
import com.ukefu.webim.web.model.Log;

public class UKeFuAppender extends ch.qos.logback.core.ConsoleAppender<ILoggingEvent> {
	@Override
	public void append(ILoggingEvent event) {
		super.append(event);
		try {
			Log log = new Log(UKDataContext.SYSTEM_ORGI , null , event.getFormattedMessage() , event.getLevel().toString() , event.getThreadName());
			log.setClazz(event.getLoggerName()) ;
			if(event.getFormattedMessage()!=null && event.getFormattedMessage().length() < 255){
				log.setMemo(event.getFormattedMessage());
			}else{
				log.setMemo(event.getFormattedMessage().substring(0 ,255));
			}
			if(event.getThrowableProxy()!=null){
				log.setMsg(event.getThrowableProxy().getMessage());
			}
			
			
			log.setMethod(event.getThreadName());
			log.setLogtype(event.getLevel().toString().equals(Level.ERROR.toString()) ? "1" : "0") ;
			log.setLogtime(String.valueOf(UKTools.dateFormate.format(new Date()))) ;
			/**
			 * 临时缓存
			 */
			UKDataContext.tempLogQueue.add(log) ;
		} catch (Throwable sqle) {
			sqle.printStackTrace();
		}

	}
}
