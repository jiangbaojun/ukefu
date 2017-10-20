package com.ukefu.webim.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.AiConfig;

public abstract interface AiConfigRepository  extends JpaRepository<AiConfig, String>{
	public abstract List<AiConfig> findByOrgi(String orgi);
}

