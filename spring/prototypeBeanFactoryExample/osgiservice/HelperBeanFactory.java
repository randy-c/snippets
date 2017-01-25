package randyc;

public interface HelperBeanFactory<Object> {
    
    Object getBeanInstance();
   
    Class<?> getClazz();
}
