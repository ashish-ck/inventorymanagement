package com.drivojoy.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

@Configuration
@ComponentScan(basePackages = { "com.drivojoy.inventory.web.controllers" })
@Profile({"production", "dev", "debug"})
public class InventoryWebAppConfig extends WebMvcConfigurerAdapter{
	
	
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/app/**").addResourceLocations("/WEB-INF/views/");
	    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}

    @SuppressWarnings("deprecation")
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
    	WebContentInterceptor interceptor = new WebContentInterceptor();
    	interceptor.setCacheSeconds(0);
    	interceptor.setUseExpiresHeader(true);
    	interceptor.setUseCacheControlHeader(true);
    	interceptor.setUseCacheControlNoStore(true);
        registry.addInterceptor(new WebContentInterceptor());
    }
		
	 /**
	 * @return TilesViewResolver
	 */
	@Bean
	 public TilesViewResolver getTilesViewResolver() {
	  TilesViewResolver tilesViewResolver = new TilesViewResolver();
	  tilesViewResolver.setViewClass(TilesView.class);
	  return tilesViewResolver;
	 }


	 /**Configures Apache tiles definitions bean used by Apache TilesViewResolver to resolve views selected for rendering by @Controllers
	 * @return TilesConfigurer
	 */
	@Bean
	 public TilesConfigurer getTilesConfigurer() {
	  TilesConfigurer tilesConfigurer = new TilesConfigurer();
	  tilesConfigurer.setCheckRefresh(true);
	  tilesConfigurer.setDefinitions("/WEB-INF/defs/tiles.xml");
	  return tilesConfigurer;
	 }
}
