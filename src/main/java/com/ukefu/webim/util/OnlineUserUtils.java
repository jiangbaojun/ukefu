package com.ukefu.webim.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ukefu.core.UKDataContext;
import com.ukefu.util.BrowserClient;
import com.ukefu.util.CheckMobile;
import com.ukefu.util.IP;
import com.ukefu.util.IPTools;
import com.ukefu.util.UKTools;
import com.ukefu.util.extra.DataExchangeInterface;
import com.ukefu.util.webim.WebIMClient;
import com.ukefu.webim.service.acd.ServiceQuene;
import com.ukefu.webim.service.cache.CacheHelper;
import com.ukefu.webim.service.impl.AgentUserService;
import com.ukefu.webim.service.repository.ConsultInviteRepository;
import com.ukefu.webim.service.repository.OnlineUserHisRepository;
import com.ukefu.webim.service.repository.OnlineUserRepository;
import com.ukefu.webim.service.repository.OrganRepository;
import com.ukefu.webim.service.repository.UserRepository;
import com.ukefu.webim.util.router.RouterHelper;
import com.ukefu.webim.util.server.message.NewRequestMessage;
import com.ukefu.webim.web.model.AgentUser;
import com.ukefu.webim.web.model.AreaType;
import com.ukefu.webim.web.model.Contacts;
import com.ukefu.webim.web.model.CousultInvite;
import com.ukefu.webim.web.model.KnowledgeType;
import com.ukefu.webim.web.model.MessageDataBean;
import com.ukefu.webim.web.model.MessageInContent;
import com.ukefu.webim.web.model.OnlineUser;
import com.ukefu.webim.web.model.OnlineUserHis;
import com.ukefu.webim.web.model.Organ;
import com.ukefu.webim.web.model.SceneType;
import com.ukefu.webim.web.model.SessionConfig;
import com.ukefu.webim.web.model.Topic;
import com.ukefu.webim.web.model.User;
import com.ukefu.webim.web.model.UserTraceHistory;

public class OnlineUserUtils {
	public static WebSseEmitterClient webIMClients = new WebSseEmitterClient();
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param id
	 * @param service
	 * @return
	 * @throws Exception
	 */
	public static OnlineUser user(com.ukefu.webim.web.model.User user,
			String orgi, String id, OnlineUserRepository service)
			throws Exception {
		List<OnlineUser> onlineUserList = service.findByUseridAndOrgi(id , orgi);
		return onlineUserList.size() > 0 ? onlineUserList.get(0) : null;
	}
	
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param id
	 * @param service
	 * @return
	 * @throws Exception
	 */
	public static CousultInvite cousult(String id ,String orgi, ConsultInviteRepository consultRes){
		CousultInvite consultInvite = (CousultInvite) CacheHelper.getSystemCacheBean().getCacheObject(id, orgi) ;
		if(consultInvite == null){
			consultInvite = consultRes.findBySnsaccountidAndOrgi(id,orgi) ;
			if(consultInvite!=null){
				CacheHelper.getSystemCacheBean().put(id ,consultInvite , orgi) ;
			}
		}
		return consultInvite;
	}
	
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param id
	 * @param service
	 * @return
	 * @throws Exception
	 */
	public static OnlineUser onlineuser(String userid ,String orgi , CousultInvite invite){
		OnlineUser onlineUser = (OnlineUser) CacheHelper.getOnlineUserCacheBean().getCacheObject(userid, orgi) ;
		if(onlineUser == null && invite.isTraceuser()){
			OnlineUserRepository service = (OnlineUserRepository) UKDataContext.getContext().getBean(OnlineUserRepository.class);

			List<OnlineUser> tempOnlineUserList = service.findByUseridAndOrgi(userid , orgi);
			if(tempOnlineUserList.size() > 0){
				onlineUser = tempOnlineUserList.get(0) ;
			}
		}
		return onlineUser;
	}
	
	
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param id
	 * @param service
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Organ> organ(String orgi , IP ipdata , CousultInvite invite){
		List<Organ> skillList = (List<Organ>) CacheHelper.getSystemCacheBean().getCacheObject(UKDataContext.CACHE_SKILL, orgi) ;
		if(skillList == null){
			OrganRepository service = (OrganRepository) UKDataContext.getContext().getBean(OrganRepository.class);
			skillList = service.findByOrgiAndSkill(orgi, true) ;
			if(skillList.size() > 0){
				CacheHelper.getSystemCacheBean().put(UKDataContext.CACHE_SKILL, skillList, orgi);
			}
		}
		List<Organ> regOrganList = new ArrayList<Organ>()  ;
		for(Organ organ : skillList){
			if(!StringUtils.isBlank(organ.getArea()) && (organ.getArea().indexOf(ipdata.getProvince()) >= 0 || organ.getArea().indexOf(ipdata.getCity()) >= 0 )){
				regOrganList.add(organ) ;
			}else if(StringUtils.isBlank(organ.getArea())){
				regOrganList.add(organ) ;
			}
		}
		return regOrganList;
	}
	
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param id
	 * @param service
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Organ> organ(String orgi){
		List<Organ> skillList = (List<Organ>) CacheHelper.getSystemCacheBean().getCacheObject(UKDataContext.CACHE_SKILL, orgi) ;
		if(skillList == null){
			OrganRepository service = (OrganRepository) UKDataContext.getContext().getBean(OrganRepository.class);
			skillList = service.findByOrgiAndSkill(orgi, true) ;
			if(skillList.size() > 0){
				CacheHelper.getSystemCacheBean().put(UKDataContext.CACHE_SKILL, skillList, orgi);
			}
		}
		return skillList;
	}
	
	private static List<AreaType> getAreaTypeList(String area , List<AreaType> areaTypeList){
		List<AreaType> atList = new ArrayList<AreaType>() ;
		if(areaTypeList!=null && areaTypeList.size() > 0){
			for(AreaType areaType : areaTypeList){
				if(!StringUtils.isBlank(area) && area.indexOf(areaType.getId()) >= 0){
					atList.add(areaType) ;
				}
			}
		}
		return atList;
	}
	/**
	 *	只要有一级 地区命中就就返回
	 * @param orgi
	 * @param ipdata
	 * @param topicTypeList
	 * @return
	 */
	public static List<KnowledgeType> topicType(String orgi , IP ipdata , List<KnowledgeType> topicTypeList){
		List<KnowledgeType> tempTopicTypeList = new ArrayList<KnowledgeType>();
		for(KnowledgeType topicType : topicTypeList){
			if(getParentArea(ipdata, topicType, tempTopicTypeList) != null){
				tempTopicTypeList.add(topicType) ;
			}
		}
		return tempTopicTypeList ;
	}
	/**
	 * 
	 * @param topicType
	 * @param topicTypeList
	 * @return
	 */
	private static KnowledgeType getParentArea(IP ipdata , KnowledgeType topicType , List<KnowledgeType> topicTypeList){
		KnowledgeType area = null ;
		if(!StringUtils.isBlank(topicType.getArea())){
			if((topicType.getArea().indexOf(ipdata.getProvince()) >=0 || topicType.getArea().indexOf(ipdata.getCity()) >= 0)){
				area = topicType;
			}
		}else{
			if(!StringUtils.isBlank(topicType.getParentid()) && !topicType.getParentid().equals("0")){
				for(KnowledgeType temp : topicTypeList){
					if(temp.getId().equals(topicType.getParentid())){
						if(!StringUtils.isBlank(temp.getArea())){
							if((temp.getArea().indexOf(ipdata.getProvince()) >=0 || temp.getArea().indexOf(ipdata.getCity()) >= 0)){
								area = temp ; break ;
							}else{
								break ;
							}
						}else{
							area = getParentArea(ipdata , temp, topicTypeList) ;
						}
					}
				}
			}else{
				area = topicType ;
			}
		}
		return area ;
	}
	
	public static List<Topic> topic(String orgi , List<KnowledgeType> topicTypeList , List<Topic> topicList){
		List<Topic> tempTopicList = new ArrayList<Topic>();
		if(topicList!=null){
			for(Topic topic : topicList){
				if(StringUtils.isBlank(topic.getCate()) || UKDataContext.DEFAULT_TYPE.equals(topic.getCate()) || getTopicType(topic.getCate(), topicTypeList)!=null){
					tempTopicList.add(topic) ;
				}
			}
		}
		return tempTopicList;
	}
	/**
	 * 根据热点知识找到 非空的 分类
	 * @param topicTypeList
	 * @param topicList
	 * @return
	 */
	public static List<KnowledgeType> filterTopicType(List<KnowledgeType> topicTypeList , List<Topic> topicList){
		List<KnowledgeType> tempTopicTypeList = new ArrayList<KnowledgeType>();
		if(topicTypeList!=null){
			boolean hasTopic = false ;
			for(KnowledgeType knowledgeType : topicTypeList){
				for(Topic topic : topicList){
					if(knowledgeType.getId().equals(topic.getCate())){
						hasTopic = true ; break ;
					}
				}
				if(hasTopic){
					tempTopicTypeList.add(knowledgeType) ;
				}
			}
		}
		return tempTopicTypeList ;
	}
	
	/**
	 * 找到知识点对应的 分类
	 * @param cate
	 * @param topicTypeList
	 * @return
	 */
	private static KnowledgeType getTopicType(String cate , List<KnowledgeType> topicTypeList){
		KnowledgeType kt = null ;
		for(KnowledgeType knowledgeType : topicTypeList){
			if(knowledgeType.getId().equals(cate)){
				kt = knowledgeType ; break ;
			}
		}
		return kt ;
	}
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param id
	 * @param service
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<User> agents(String orgi){
		List<User> agentList = (List<User>) CacheHelper.getSystemCacheBean().getCacheObject(UKDataContext.CACHE_AGENT, orgi) ;
		if(agentList == null){
			UserRepository service = (UserRepository) UKDataContext.getContext().getBean(UserRepository.class);
			agentList = service.findByOrgiAndAgent(orgi, true) ;
			if(agentList.size() > 0){
				CacheHelper.getSystemCacheBean().put(UKDataContext.CACHE_AGENT, agentList, orgi);
			}
		}
		return agentList;
	}
	
	
	public static void clean(String orgi){
		CacheHelper.getSystemCacheBean().delete(UKDataContext.CACHE_SKILL, orgi) ;
		CacheHelper.getSystemCacheBean().delete(UKDataContext.CACHE_AGENT, orgi) ;
	}
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param id
	 * @param service
	 * @return
	 * @throws Exception
	 */
	public static void cacheOnlineUser(OnlineUser onlineUser ,String orgi  , CousultInvite invite){
		if(onlineUser!=null && !StringUtils.isBlank(onlineUser.getUserid())){
			CacheHelper.getOnlineUserCacheBean().put(onlineUser.getUserid() , onlineUser , orgi) ;
		}
		if(invite.isTraceuser()){
			UKTools.published(onlineUser);
		}
	}

	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param optype
	 * @param request
	 * @param service
	 * @throws Exception
	 */
	public static OnlineUser online(User user, String orgi, String sessionid,String optype, HttpServletRequest request , String channel , String appid , Contacts contacts , CousultInvite invite) {
		OnlineUser onlineUser = null;
		if (UKDataContext.getContext() != null) {
			onlineUser = onlineuser(user.getId(), orgi , invite) ;
			if (onlineUser == null) {
				onlineUser = new OnlineUser();
				onlineUser.setId(user.getId());
				onlineUser.setCreater(user.getId());
				onlineUser.setUsername(user.getUsername());
				onlineUser.setCreatetime(new Date());
				onlineUser.setUpdatetime(new Date());
				onlineUser.setUpdateuser(user.getUsername());
				onlineUser.setSessionid(sessionid);
				
				if(contacts!=null){
					onlineUser.setContactsid(contacts.getId());
				}

				onlineUser.setOrgi(orgi);
				onlineUser.setChannel(channel);
				
				String cookie = getCookie(request, "R3GUESTUSEKEY");
				if ((StringUtils.isBlank(cookie))
						|| (user.getSessionid().equals(cookie))) {
					onlineUser.setOlduser("0");
				} else {
					onlineUser.setOlduser("1");
				}
				onlineUser.setMobile(CheckMobile.check(request
						.getHeader("User-Agent")) ? "1" : "0");

				// onlineUser.setSource(user.getId());

				String url = request.getHeader("referer");
				onlineUser.setUrl(url);
				if (!StringUtils.isBlank(url)) {
					try {
						URL referer = new URL(url);
						onlineUser.setSource(referer.getHost());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				onlineUser.setAppid(appid);
				onlineUser.setUserid(user.getId());
				onlineUser.setUsername(user.getUsername());
				
				if(!StringUtils.isBlank(request.getParameter("title"))){
					String title = request.getParameter("title") ;
					if(title.length() > 255){
						onlineUser.setTitle(title.substring(0,255));
					}else{
						onlineUser.setTitle(title);
					}
				}

				String ip = UKTools.getIpAddr(request);
				
				onlineUser.setLogintime(new Date());
				onlineUser.setIp(ip);

				IP ipdata = IPTools.getInstance().findGeography(ip);
				onlineUser.setCountry(ipdata.getCountry());
				onlineUser.setProvince(ipdata.getProvince());
				onlineUser.setCity(ipdata.getCity());
				onlineUser.setIsp(ipdata.getIsp());
				onlineUser.setRegion(ipdata.toString() + "（"
						+ ip + "）");

				onlineUser.setDatestr(new SimpleDateFormat("yyyMMdd")
						.format(new Date()));

				onlineUser.setHostname(ip);
				onlineUser.setSessionid(sessionid);
				onlineUser.setOptype(optype);
				onlineUser
						.setStatus(UKDataContext.OnlineUserOperatorStatus.ONLINE
								.toString());
				BrowserClient client = UKTools.parseClient(request);
				onlineUser.setOpersystem(client.getOs());
				onlineUser.setBrowser(client.getBrowser());
				onlineUser.setUseragent(client.getUseragent());
			}else{
				onlineUser.setCreatetime(new Date());
				if((!StringUtils.isBlank(onlineUser.getSessionid()) && !onlineUser.getSessionid().equals(sessionid)) || !UKDataContext.OnlineUserOperatorStatus.ONLINE.toString().equals(onlineUser.getStatus())){
					onlineUser.setStatus(UKDataContext.OnlineUserOperatorStatus.ONLINE.toString());
					onlineUser.setChannel(channel);
					onlineUser.setAppid(appid);
					onlineUser.setUpdatetime(new Date());
					if(!StringUtils.isBlank(onlineUser.getSessionid()) && !onlineUser.getSessionid().equals(sessionid)){
						onlineUser.setInvitestatus(UKDataContext.OnlineUserInviteStatus.DEFAULT.toString());
						onlineUser.setSessionid(sessionid);
						onlineUser.setLogintime(new Date());
						onlineUser.setInvitetimes(0);
					}
				}else if(contacts!=null){
					if(contacts!=null && !StringUtils.isBlank(contacts.getId()) && !StringUtils.isBlank(contacts.getName()) &&(StringUtils.isBlank(onlineUser.getContactsid()) || !contacts.getName().equals(onlineUser.getUsername()))){
						if(StringUtils.isBlank(onlineUser.getContactsid())){
							onlineUser.setContactsid(contacts.getId());
						}
						if(!contacts.getName().equals(onlineUser.getUsername())){
							onlineUser.setUsername(contacts.getName());
						}
						onlineUser.setUpdatetime(new Date());
					}
				}
				if(StringUtils.isBlank(onlineUser.getUsername()) && !StringUtils.isBlank(user.getUsername())){
					onlineUser.setUseragent(user.getUsername());
					onlineUser.setUpdatetime(new Date());
				}
			}
			if(invite.isRecordhis() && !StringUtils.isBlank(request.getParameter("traceid"))){
	    		UserTraceHistory trace = new UserTraceHistory();
	    		trace.setId(request.getParameter("traceid"));
	    		trace.setTitle(request.getParameter("title"));
	    		trace.setUrl(request.getParameter("url"));
	    		trace.setOrgi(invite.getOrgi());
	    		trace.setUpdatetime(new Date());
	    		trace.setUsername(onlineUser.getUsername());
	    		
	    		UKTools.published(trace);
    		}
			cacheOnlineUser(onlineUser, orgi , invite);
		}
		return onlineUser;
	}

	/**
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String key) {
		Cookie data = null;
		if (request != null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(key)) {
					data = cookie;
					break;
				}
			}
		}
		return data != null ? data.getValue() : null;
	}

	/**
	 * 
	 * @param user
	 * @param orgi
	 * @throws Exception
	 */
	public static void offline(String user, String orgi) throws Exception {
		if(UKDataContext.getContext()!=null){
			OnlineUser onlineUser = (OnlineUser) CacheHelper.getOnlineUserCacheBean().getCacheObject(user, orgi) ;
			if(onlineUser!=null){
				CousultInvite invite = OnlineUserUtils.cousult(onlineUser.getAppid(),onlineUser.getOrgi(), UKDataContext.getContext().getBean(ConsultInviteRepository.class));
				if(invite.isTraceuser()){
					onlineUser.setStatus(UKDataContext.OnlineUserOperatorStatus.OFFLINE.toString());
					onlineUser.setInvitestatus(UKDataContext.OnlineUserInviteStatus.DEFAULT.toString());
					onlineUser.setBetweentime((int) (new Date().getTime() - onlineUser.getLogintime().getTime()));
					onlineUser.setUpdatetime(new Date());
					OnlineUserRepository service = UKDataContext.getContext().getBean(
							OnlineUserRepository.class);
					service.save(onlineUser) ;
					
					OnlineUserHisRepository onlineHisUserRes = UKDataContext.getContext().getBean(OnlineUserHisRepository.class) ;
					{
						List<OnlineUserHis> hisList = onlineHisUserRes.findBySessionidAndOrgi(onlineUser.getSessionid() , orgi) ;
						OnlineUserHis his = null ;
						if(hisList.size() > 0){
							his = hisList.get(0) ;
						}else{
							his = new OnlineUserHis();
						}
						
						UKTools.copyProperties(onlineUser, his);
						his.setDataid(onlineUser.getId());
						onlineHisUserRes.save(his);
					}
				}
			}
			CacheHelper.getOnlineUserCacheBean().delete(user, orgi) ;
		}
	}
	
	public static void offline(OnlineUser onlineUser) throws Exception {
		if(UKDataContext.getContext()!=null){
			OnlineUserRepository service = UKDataContext.getContext().getBean(
					OnlineUserRepository.class);
			OnlineUserHisRepository onlineHisUserRes = UKDataContext.getContext().getBean(OnlineUserHisRepository.class) ;
			if (onlineUser != null) {
				onlineUser.setStatus(UKDataContext.OnlineUserOperatorStatus.OFFLINE.toString());
				onlineUser.setInvitestatus(UKDataContext.OnlineUserInviteStatus.DEFAULT.toString());
				onlineUser.setBetweentime((int) (new Date().getTime() - onlineUser.getLogintime().getTime()));
				onlineUser.setUpdatetime(new Date());
				service.save(onlineUser) ;
				CacheHelper.getOnlineUserCacheBean().delete(onlineUser.getUserid(), onlineUser.getOrgi()) ;
				if(onlineUser!=null){
					List<OnlineUserHis> hisList = onlineHisUserRes.findBySessionidAndOrgi(onlineUser.getSessionid() , onlineUser.getOrgi()) ;
					OnlineUserHis his = null ;
					if(hisList.size() > 0){
						his = hisList.get(0) ;
					}else{
						his = new OnlineUserHis();
					}
					
					UKTools.copyProperties(onlineUser, his);
					his.setDataid(onlineUser.getId());
					onlineHisUserRes.save(his);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @throws Exception
	 */
	public static void refuseInvite(String user, String orgi) {
		OnlineUserRepository service = UKDataContext.getContext().getBean(
				OnlineUserRepository.class);

		List<OnlineUser> onlineUserList = service.findByUseridAndOrgi(user , orgi);
		if (onlineUserList.size() > 0) {
			OnlineUser onlineUser = onlineUserList.get(0);
			onlineUser.setInvitestatus(UKDataContext.OnlineUserInviteStatus.REFUSE.toString());
			onlineUser.setRefusetimes(onlineUser.getRefusetimes()+1);
			service.save(onlineUser) ;
		}
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		try {
			tmp.append(java.net.URLDecoder.decode(src, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return tmp.toString();
	}

	public static String getKeyword(String url) {
		Map<String, String[]> values = new HashMap<String, String[]>();
		try {
			parseParameters(values, url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringBuffer strb = new StringBuffer();
		String[] data = values.get("q");
		if (data != null) {
			for (String v : data) {
				strb.append(v);
			}
		}
		return strb.toString();
	}

	public static String getSource(String url) {
		String source = "0";
		try {
			URL addr = new URL(url);
			source = addr.getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return source;
	}

	private static NewRequestMessage newRequestMessage(String user , String nickname, String orgi,
			String session, String appid, String ip, String osname,
			String browser , String headimg , IP ipdata , String channel , String skill , String agent, String title, String url , String traceid) throws Exception {
		// 坐席服务请求，分配 坐席
		NewRequestMessage data = new NewRequestMessage();
		data.setAppid(appid);
		data.setOrgi(orgi);
		data.setUserid(user);
		data.setSession(session);
		data.setType(UKDataContext.MessageTypeEnum.NEW.toString());
		data.setId(UKTools.genID());

		AgentUserService service = UKDataContext.getContext().getBean(
				AgentUserService.class);
		AgentUser agentUser = service.findByUseridAndOrgi(user , orgi);
		if (agentUser == null) {
			agentUser = new AgentUser(data.getUserid(),channel,
					data.getUserid(), null, data.getOrgi(), data.getAppid()); // 创建排队用户的信息，当前用户只能在队列里存在一次，用
																				// UserID作为主键ID存储
			agentUser.setNickname(nickname);
			agentUser.setUsername(nickname);
			
			agentUser.setOsname(osname);
			agentUser.setBrowser(browser);
			agentUser.setAppid(appid);
			agentUser.setSessionid(session);
			
			if (ipdata != null) {
				agentUser.setCountry(ipdata.getCountry());
				agentUser.setProvince(ipdata.getProvince());
				agentUser.setCity(ipdata.getCity());
				if(!StringUtils.isBlank(ip)){
					agentUser.setRegion(ipdata.toString() + "[" + ip + "]");
				}else{
					agentUser.setRegion(ipdata.toString());
				}
			}

			// agentUser.setContextid(session);
			agentUser.setHeadimgurl(headimg);
			// agentUser.setId(data.getUserid());
		}else if(!agentUser.getUsername().equals(nickname)){
			agentUser.setUsername(nickname);
			agentUser.setNickname(nickname);
		}
		agentUser.setStatus(null); // 修改状态
		agentUser.setTitle(title);
		agentUser.setUrl(url);
		agentUser.setTraceid(traceid);
		
		CousultInvite invite = OnlineUserUtils.cousult(appid, orgi, UKDataContext.getContext().getBean(ConsultInviteRepository.class)) ;
		if(invite!=null && !invite.isTraceuser()){
			OnlineUser onlineUser = (OnlineUser) CacheHelper.getOnlineUserCacheBean().getCacheObject(user, orgi) ;
			if(onlineUser!=null){
				OnlineUserRepository onlineUserRes = UKDataContext.getContext().getBean(OnlineUserRepository.class) ;
				if(onlineUserRes.countByUseridAndOrgi(user, orgi) == 0){
					onlineUserRes.save(onlineUser) ;
				}
			}
		}

		MessageInContent inMessage = new MessageInContent();
		inMessage.setChannelMessage(data);
		inMessage.setAgentUser(agentUser);
		inMessage.setMessage(data.getMessage());
		inMessage.setFromUser(data.getUserid());
		inMessage.setToUser(data.getAppid());
		inMessage.setId(data.getId());
		inMessage.setMessageType(data.getType());
		inMessage.setNickName(agentUser.getNickname());
		inMessage.setOrgi(data.getOrgi());
		inMessage.setUser(agentUser);
		
		/**
		 * 技能组 和 坐席
		 */
		 
		agentUser.setSkill(skill);
		agentUser.setAgent(agent);
		
//		if(!StringUtils.isBlank(skill)){
//			agentUser.setSkill(skill);
//		}else{
//			agentUser.setSkill(UKDataContext.SERVICE_QUENE_NULL_STR);
//		}
//		if(!StringUtils.isBlank(agent)){
//			agentUser.setAgent(agent);
//		}else{
//			agentUser.setAgent(UKDataContext.SERVICE_QUENE_NULL_STR);
//		}

		MessageDataBean outMessageDataBean = null ;
		
		SessionConfig sessionConfig = ServiceQuene.initSessionConfig(data.getOrgi()) ;
		
		if(sessionConfig.isHourcheck() && !UKTools.isInWorkingHours(sessionConfig.getWorkinghours())){
			data.setMessage(sessionConfig.getNotinwhmsg());
		}else{
			outMessageDataBean = RouterHelper.getRouteInstance().handler(inMessage);
			if (outMessageDataBean != null) {
				data.setMessage(outMessageDataBean.getMessage());
				
				if(outMessageDataBean.getAgentUser()!=null){
					data.setAgentserviceid(outMessageDataBean.getAgentUser().getAgentserviceid());
				}
			}
		}
		
		return data;
	}
	
	
	public static NewRequestMessage newRequestMessage(String userid, String orgi,
			String session, String appid, String ip, String osname,
			String browser , String channel , String skill , String agent , String nickname, String title, String url , String traceid) throws Exception {
		IP ipdata = null ;
		if(!StringUtils.isBlank(ip)){
			ipdata = IPTools.getInstance().findGeography(ip);
		}
		if(StringUtils.isBlank(nickname)){
			nickname = "Guest_" + userid;
		}
		
		return newRequestMessage(userid , nickname, orgi, session, appid, ip, osname, browser , "" , ipdata , channel , skill , agent , title ,url , traceid) ;
	}
	
	public static NewRequestMessage newRequestMessage(String openid , String nickname, String orgi,
			String session, String appid , String headimg , String country , String province , String city , String channel , String skill , String agent) throws Exception {
		IP ipdata = new IP() ;
		ipdata.setCountry(country);
		ipdata.setProvince(province);
		ipdata.setCity(city);
		return newRequestMessage(openid , nickname , orgi, session, appid, null , null , null , headimg , ipdata , channel , skill , agent , null , null , null) ;
	}

	public static void parseParameters(Map<String, String[]> map, String data,
			String encoding) throws UnsupportedEncodingException {
		if ((data == null) || (data.length() <= 0)) {
			return;
		}

		byte[] bytes = null;
		try {
			if (encoding == null)
				bytes = data.getBytes();
			else
				bytes = data.getBytes(encoding);
		} catch (UnsupportedEncodingException uee) {
		}
		parseParameters(map, bytes, encoding);
	}

	public static void parseParameters(Map<String, String[]> map, byte[] data,
			String encoding) throws UnsupportedEncodingException {
		if ((data != null) && (data.length > 0)) {
			int ix = 0;
			int ox = 0;
			String key = null;
			String value = null;
			while (ix < data.length) {
				byte c = data[(ix++)];
				switch ((char) c) {
				case '&':
					value = new String(data, 0, ox, encoding);
					if (key != null) {
						putMapEntry(map, key, value);
						key = null;
					}
					ox = 0;
					break;
				case '=':
					if (key == null) {
						key = new String(data, 0, ox, encoding);
						ox = 0;
					} else {
						data[(ox++)] = c;
					}
					break;
				case '+':
					data[(ox++)] = 32;
					break;
				case '%':
					data[(ox++)] = (byte) ((convertHexDigit(data[(ix++)]) << 4) + convertHexDigit(data[(ix++)]));

					break;
				default:
					data[(ox++)] = c;
				}
			}

			if (key != null) {
				value = new String(data, 0, ox, encoding);
				putMapEntry(map, key, value);
			}
		}
	}

	private static void putMapEntry(Map<String, String[]> map, String name,
			String value) {
		String[] newValues = null;
		String[] oldValues = (String[]) (String[]) map.get(name);
		if (oldValues == null) {
			newValues = new String[1];
			newValues[0] = value;
		} else {
			newValues = new String[oldValues.length + 1];
			System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
			newValues[oldValues.length] = value;
		}
		map.put(name, newValues);
	}

	private static byte convertHexDigit(byte b) {
		if ((b >= 48) && (b <= 57))
			return (byte) (b - 48);
		if ((b >= 97) && (b <= 102))
			return (byte) (b - 97 + 10);
		if ((b >= 65) && (b <= 70))
			return (byte) (b - 65 + 10);
		return 0;
	}

	// public static void main(String[] args){
	// System.out.println(getKeyword("http://www.so.com/link?url=http%3A%2F%2Fwww.r3yun.com%2F&q=R3+Query%E5%AE%98%E7%BD%91&ts=1484181457&t=e2ad49617cd5de0eb0937f3e2a84669&src=haosou"))
	// ;
	// System.out.println(getSource("https://www.google.com.hk/")) ;
	// }
	
	/**
	 * 发送邀请
	 * @param userid
	 * @throws Exception 
	 */
	public static void sendWebIMClients(String userid , String msg) throws Exception{
		List<WebIMClient> clients = OnlineUserUtils.webIMClients.getClients(userid) ;
		if(clients!=null && clients.size()>0){
			for(WebIMClient client : clients){
				try{
					client.getSse().send(SseEmitter.event().reconnectTime(0).data(msg));
				}catch(Exception ex){
					OnlineUserUtils.webIMClients.removeClient(userid , client.getClient() , true) ;
				}finally{
					client.getSse().complete();
				}
			}
		}
	}
	public static void resetHotTopic(DataExchangeInterface dataExchange,User user , String orgi) {
		if(CacheHelper.getSystemCacheBean().getCacheObject("xiaoeTopic", orgi)!=null){
			CacheHelper.getSystemCacheBean().delete("xiaoeTopic", orgi) ;
		}
		cacheHotTopic(dataExchange,user , orgi) ;
	}

	@SuppressWarnings("unchecked")
	public static List<Topic> cacheHotTopic(DataExchangeInterface dataExchange,User user , String orgi) {
		List<Topic> topicList = null ;
		if((topicList = (List<Topic>) CacheHelper.getSystemCacheBean().getCacheObject("xiaoeTopic", orgi))==null){ 
			topicList = (List<Topic>) dataExchange.getListDataByIdAndOrgi(null, null,  orgi) ;
			CacheHelper.getSystemCacheBean().put("xiaoeTopic" , topicList , orgi) ;
		}
		return topicList;
	}
	
	public static void resetHotTopicType(DataExchangeInterface dataExchange,User user , String orgi) {
		if(CacheHelper.getSystemCacheBean().getCacheObject("xiaoeTopicType", orgi)!=null){
			CacheHelper.getSystemCacheBean().delete("xiaoeTopicType", orgi) ;
		}
		cacheHotTopicType(dataExchange,user , orgi) ;
	}
	@SuppressWarnings("unchecked")
	public static List<KnowledgeType> cacheHotTopicType(DataExchangeInterface dataExchange,User user , String orgi) {
		List<KnowledgeType> topicTypeList = null ;
		if((topicTypeList = (List<KnowledgeType>) CacheHelper.getSystemCacheBean().getCacheObject("xiaoeTopicType", orgi))==null){ 
			topicTypeList = (List<KnowledgeType>) dataExchange.getListDataByIdAndOrgi(null, null,  orgi) ;
			CacheHelper.getSystemCacheBean().put("xiaoeTopicType" , topicTypeList , orgi) ;
		}
		return topicTypeList;
	}
	
	@SuppressWarnings("unchecked")
	public static List<SceneType> cacheSceneType(DataExchangeInterface dataExchange,User user , String orgi) {
		List<SceneType> sceneTypeList = null ;
		if((sceneTypeList = (List<SceneType>) CacheHelper.getSystemCacheBean().getCacheObject("xiaoeSceneType", orgi))==null){ 
			sceneTypeList = (List<SceneType>) dataExchange.getListDataByIdAndOrgi(null, null,  orgi) ;
			CacheHelper.getSystemCacheBean().put("xiaoeSceneType" , sceneTypeList , orgi) ;
		}
		return sceneTypeList;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean filterSceneType(String cate ,String orgi , IP ipdata) {
		boolean result = false ;
		List<SceneType> sceneTypeList = cacheSceneType((DataExchangeInterface) UKDataContext.getContext().getBean("scenetype"), null, orgi) ;
		List<AreaType> areaTypeList = (List<AreaType>) CacheHelper.getSystemCacheBean().getCacheObject(UKDataContext.UKEFU_SYSTEM_AREA, UKDataContext.SYSTEM_ORGI) ;
		if(sceneTypeList!=null && cate != null && !UKDataContext.DEFAULT_TYPE.equals(cate)){
			for(SceneType sceneType : sceneTypeList){
				if(cate.equals(sceneType.getId())){
					if(!StringUtils.isBlank(sceneType.getArea())){
						if(ipdata!=null){
							List<AreaType> atList = getAreaTypeList(sceneType.getArea(), areaTypeList) ;	//找到技能组配置的地区信息
							for(AreaType areaType : atList){
								if(areaType.getArea().indexOf(ipdata.getProvince()) >= 0 || areaType.getArea().indexOf(ipdata.getCity()) >= 0 ){
									result = true ; break ;
								}
							}
						}
					}else{
						result = true ;
					}
				}
				if(result){
					break ;
				}
			}
		}else{
			result = true; 
		}
		return result;
	}
}
