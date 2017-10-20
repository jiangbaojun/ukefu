package com.ukefu.webim.web.handler;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ukefu.util.Menu;
import com.ukefu.webim.service.acd.ServiceQuene;
import com.ukefu.webim.service.cache.CacheHelper;
import com.ukefu.webim.service.repository.UserRepository;
import com.ukefu.webim.web.model.User;

@Controller
public class ApplicationController extends Handler{
	
	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
    public ModelAndView admin(HttpServletRequest request) {
		ModelAndView view = request(super.createRequestPageTempletResponse("/apps/index"));
		User user = super.getUser(request) ;
        view.addObject("agentStatusReport",ServiceQuene.getAgentReport(user.getOrgi())) ;
		view.addObject("agentStatus",CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId(), user.getOrgi())) ;
        return view;
    }
	
	@Menu(access = true)
	@RequestMapping("/test1")
    public ModelAndView test1(HttpServletRequest request) {
		ModelAndView view = request(super.createRequestPageTempletResponse("/apps/test1"));
		User user = super.getUser(request) ;
		JSONArray arr = new JSONArray();
		JSONObject obj1 = new JSONObject();
		obj1.put("bb", "22");
		arr.add("xx");
		arr.add("yy");
		arr.add("zz");
		view.addObject("obj1", obj1) ;
		view.addObject("par", "test params") ;
        view.addObject("arr", arr) ;
        return view;
    }
}