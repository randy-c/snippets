package randyc;

import org.springframework.beans.BeansException;

public class HelperBeanFactoryImpl implements ApplicationContextAware, HelperBeanFactory<Object> {
    
    private ApplicationContext context;
    private String beanId;
    private Class<?> clazz;
    
    @Override
    public Object getBeanInstance() {
        return context.getBean(this.beanId);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }


    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }
}
