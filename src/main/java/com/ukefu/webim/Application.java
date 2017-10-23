package com.ukefu.webim;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import com.ukefu.core.UKDataContext;
import com.ukefu.webim.config.web.StartedEventListener;

@EnableAutoConfiguration
@SpringBootApplication
@EnableJpaRepositories("com.ukefu.webim.service.repository")
@EnableAsync
public class Application {
	
	static{
    	UKDataContext.model.put("contacts", true) ;
//    	此处判断是否开启企业版内部沟通（注意，开源官方明确表示是收费的功能。可破解）
    	UKDataContext.model.put("im", true);
//    	下面的功能不可用
//    	UKDataContext.model.put("workorders", true);
//    	UKDataContext.model.put("xiaoe", true);
//    	UKDataContext.model.put("sales", true);
    	
    }
	
    @Bean   
    public MultipartConfigElement multipartConfigElement() {   
            MultipartConfigFactory factory = new MultipartConfigFactory();  
            factory.setMaxFileSize("50MB"); //KB,MB  
            factory.setMaxRequestSize("100MB");   
            return factory.createMultipartConfig();   
    }   
      
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
            	ErrorPage error = new ErrorPage("/error.html");
            	container.addErrorPages(error);
            }
        };
    }
    
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Application.class) ;
		springApplication.addListeners(new StartedEventListener());
		UKDataContext.setApplicationContext(springApplication.run(args));
	}
	
}
