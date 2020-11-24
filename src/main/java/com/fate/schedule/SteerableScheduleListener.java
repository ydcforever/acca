package com.fate.schedule;

import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by ydc on 2020/10/23.
 */
@WebListener
public class SteerableScheduleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        WebApplicationContext context = (WebApplicationContext) servletContextEvent.getServletContext().getAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        SteerableScheduleManager manager = (SteerableScheduleManager) context.getBean("com.fate.schedule.SteerableScheduleManager");
        try {
            manager.destroy();
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
