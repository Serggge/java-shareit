package ru.practicum.shareit;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ShareItApp {

	public static final int PORT = 8080;

	public static void main(String[] args) throws LifecycleException {

		Tomcat tomcat = new Tomcat();
		tomcat.getConnector().setPort(PORT);

		Context tomcatCtx = tomcat.addContext("", null);

		AnnotationConfigWebApplicationContext appCtx = new AnnotationConfigWebApplicationContext();
		appCtx.scan("ru.practicum.shareit");
		appCtx.setServletContext(tomcatCtx.getServletContext());
		appCtx.refresh();

		DispatcherServlet dispatcher = new DispatcherServlet(appCtx);
		Wrapper wrapper = Tomcat.addServlet(tomcatCtx, "dispatcher", dispatcher);
		wrapper.addMapping("/");
		wrapper.setLoadOnStartup(1);

		tomcat.start();
	}

}
