package com.ukefu.webim.web.handler.apps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ukefu.core.UKDataContext;
import com.ukefu.util.Menu;
import com.ukefu.util.UKTools;
import com.ukefu.util.bi.ReportData;
import com.ukefu.util.bi.UKExcelUtil;
import com.ukefu.util.bi.model.Level;
import com.ukefu.webim.service.repository.AgentServiceRepository;
import com.ukefu.webim.service.repository.CubeService;
import com.ukefu.webim.service.repository.DataSourceService;
import com.ukefu.webim.service.repository.UserRepository;
import com.ukefu.webim.service.repository.WeiXinUserRepository;
import com.ukefu.webim.util.OnlineUserUtils;
import com.ukefu.webim.web.handler.Handler;
import com.ukefu.webim.web.model.SysDic;
import com.ukefu.webim.web.model.UKeFuDic;

@Controller
@RequestMapping("/service")
public class StatsController extends Handler{
	@Autowired
	private AgentServiceRepository agentServiceRes ;
	
	@Autowired
	private UserRepository userRes ;
	
	@Autowired
	private WeiXinUserRepository weiXinUserRes;
	
	@Value("${web.upload-path}")
    private String path;
	
	@Autowired
	private DataSourceService dataSource ;
	
	@RequestMapping("/stats/coment")
    @Menu(type = "service" , subtype = "statcoment" , admin= true)
    public ModelAndView statcoment(ModelMap map , HttpServletRequest request , @Valid String agent , @Valid String skill , @Valid String begin ,@Valid String end) throws Exception {
		ReportData reportData = new CubeService("coment.xml", path, dataSource , UKTools.getRequestParam(request)).execute("SELECT [comment].[满意度].members on columns , NonEmptyCrossJoin([time].[日期].members , NonEmptyCrossJoin([skill].[技能组].members,[agent].[坐席].members)) on rows  FROM [满意度]") ;
		
		List<SysDic> dicList = UKeFuDic.getInstance().getDic(UKDataContext.UKEFU_SYSTEM_COMMENT_DIC) ;
		for(Level title : reportData.getCol().getChilderen()){
			for(SysDic dic : dicList){
				if(dic.getCode().equals(title.getName())){
					title.setName(dic.getName());
				}
			}
		}
		
		map.addAttribute("reportData", reportData);
		if(!StringUtils.isBlank(agent)){
			map.addAttribute("agent", agent);
		}
		if(!StringUtils.isBlank(skill)){
			map.addAttribute("skill", skill);
		}
		if(!StringUtils.isBlank(begin)){
			map.addAttribute("begin", begin);
		}
		if(!StringUtils.isBlank(end)){
			map.addAttribute("end", end);
		}
		
		 /***
	     * 查询 技能组 ， 缓存？ 
	     */
		map.addAttribute("skillList", OnlineUserUtils.organ(super.getOrgi(request)))  ;
	    /**
	     * 查询坐席 ， 缓存？
	     */
		map.addAttribute("agentList", OnlineUserUtils.agents(super.getOrgi(request)))  ;
		
		return request(super.createAppsTempletResponse("/apps/service/stats/coment"));
    }
	
	@RequestMapping("/stats/coment/exp")
    @Menu(type = "service" , subtype = "statcoment" , admin= true)
    public void statcomentexp(ModelMap map , HttpServletRequest request , HttpServletResponse response , @Valid String agent , @Valid String skill , @Valid String begin ,@Valid String end) throws Exception {
		ReportData reportData = new CubeService("coment.xml", path, dataSource , UKTools.getRequestParam(request)).execute("SELECT [comment].[满意度].members on columns , NonEmptyCrossJoin([time].[日期].members , NonEmptyCrossJoin([skill].[技能组].members,[agent].[坐席].members)) on rows  FROM [满意度]") ;
		
		List<SysDic> dicList = UKeFuDic.getInstance().getDic(UKDataContext.UKEFU_SYSTEM_COMMENT_DIC) ;
		for(Level title : reportData.getCol().getChilderen()){
			for(SysDic dic : dicList){
				if(dic.getCode().equals(title.getName())){
					title.setName(dic.getName());
				}
			}
		}
		
		response.setHeader("content-disposition", "attachment;filename=UCKeFu-Report-"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".xlsx");
		new UKExcelUtil(reportData , response.getOutputStream() , "满意度统计").createFile() ;
		
		return ;
    }
	
	@RequestMapping("/stats/agent")
    @Menu(type = "service" , subtype = "statagent" , admin= true)
    public ModelAndView statagent(ModelMap map , HttpServletRequest request , @Valid String agent , @Valid String skill , @Valid String begin ,@Valid String end) throws Exception {
		ReportData reportData = new CubeService("consult.xml", path, dataSource , UKTools.getRequestParam(request)).execute("SELECT {[Measures].[咨询数量],[Measures].[平均等待时长（秒）],[Measures].[平均咨询时长（秒）]} on columns , NonEmptyCrossJoin([time].[日期].members , NonEmptyCrossJoin([skill].[技能组].members,[agent].[坐席].members)) on rows  FROM [咨询]") ;
		map.addAttribute("reportData", reportData);
		
		if(!StringUtils.isBlank(agent)){
			map.addAttribute("agent", agent);
		}
		if(!StringUtils.isBlank(skill)){
			map.addAttribute("skill", skill);
		}
		if(!StringUtils.isBlank(begin)){
			map.addAttribute("begin", begin);
		}
		if(!StringUtils.isBlank(end)){
			map.addAttribute("end", end);
		}
		/***
	     * 查询 技能组 ， 缓存？ 
	     */
		map.addAttribute("skillList", OnlineUserUtils.organ(super.getOrgi(request)))  ;
	    /**
	     * 查询坐席 ， 缓存？
	     */
		map.addAttribute("agentList", OnlineUserUtils.agents(super.getOrgi(request)))  ;
		
		return request(super.createAppsTempletResponse("/apps/service/stats/consult"));
    }
	
	@RequestMapping("/stats/agent/exp")
    @Menu(type = "service" , subtype = "statagent" , admin= true)
    public void statagentexp(ModelMap map , HttpServletRequest request , HttpServletResponse response ,@Valid String agent , @Valid String skill , @Valid String begin ,@Valid String end) throws Exception {
		ReportData reportData = new CubeService("consult.xml", path, dataSource , UKTools.getRequestParam(request)).execute("SELECT {[Measures].[咨询数量],[Measures].[平均等待时长（秒）],[Measures].[平均咨询时长（秒）]} on columns , NonEmptyCrossJoin([time].[日期].members , NonEmptyCrossJoin([skill].[技能组].members,[agent].[坐席].members)) on rows  FROM [咨询]") ;
		response.setHeader("content-disposition", "attachment;filename=UCKeFu-Report-"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".xlsx");
		new UKExcelUtil(reportData , response.getOutputStream() , "客服坐席统计").createFile() ;
		
		return ;
    }
}
