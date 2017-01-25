package randyc;

import org.springframework.beans.factory.FactoryBean;

public class BeanFactory implements FactoryBean<Object>{
    
    private HelperBeanFactory<Object> helperBeanFactory;

    @Override
    public Object getObject() throws Exception {
            return helperBeanFactory.getBeanInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return helperBeanFactory.getClazz();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
    
    public HelperBeanFactory<Object> getHelperBeanFactory() {
        return helperBeanFactory;
    }

    public void setHelperBeanFactory(HelperBeanFactory<Object> helperBeanFactory) {
        this.helperBeanFactory = helperBeanFactory;
    }
}
