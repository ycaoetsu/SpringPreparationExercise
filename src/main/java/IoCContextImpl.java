import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IoCContextImpl implements IoCContext{
    private Set<Class<?>> beanCache;
    private boolean isGettingBean = false;
    private Map<Class<?>, Class<?>> beanMap;

    public IoCContextImpl() {
        this.beanCache = new HashSet<>();
        this.beanMap = new HashMap<>();
    }

    @Override
    public void registerBean(Class<?> beanClazz) {
        if (isGettingBean) {
            throw new IllegalStateException();
        }
        if (beanClazz == null) {
            String errorMessage = "beanClazz is mandatory";
            throw new IllegalArgumentException(errorMessage);
        }
        if (beanCache.contains(beanClazz)) {
            return;
        }
        if (Modifier.isAbstract(beanClazz.getModifiers())) {
            String errorMessage = String.format("%s is abstract", beanClazz.getName());
            throw new IllegalArgumentException(errorMessage);
        }
        try {
            Constructor constructor = beanClazz.getConstructor();
        } catch (Exception error) {
            String errorMessage = String.format("%s has no default constructor.", beanClazz.getName());
            throw new IllegalArgumentException(errorMessage);
        }
        beanCache.add(beanClazz);
        beanMap.put(beanClazz, beanClazz);
    }

    @Override
    public <T> void registerBean(Class<? super T> resolveClazz, Class<T> beanClazz) {
//        if (isGettingBean) {
//            throw new IllegalStateException();
//        }
//        if (beanClazz == null) {
//            String errorMessage = "beanClazz is mandatory";
//            throw new IllegalArgumentException(errorMessage);
//        }
//        if (resolveClazz == null) {
//            String errorMessage = "resolveClazz is mandatory";
//        }
//        if (beanCache.contains(beanClazz) || beanCache.contains(resolveClazz)) {
//            return;
//        }
//        if (Modifier.isAbstract(beanClazz.getModifiers())) {
//            String errorMessage = String.format("%s is abstract", beanClazz.getName());
//            throw new IllegalArgumentException(errorMessage);
//        }
//        if (Modifier.isAbstract(resolveClazz.getModifiers())) {
//            String errorMessage = String.format("%s is abstract", resolveClazz.getName());
//            throw new IllegalArgumentException(errorMessage);
//        }
//        try {
//            Constructor constructor = beanClazz.getConstructor();
//        } catch (Exception error) {
//            String errorMessage = String.format("%s has no default constructor.", beanClazz.getName());
//            throw new IllegalArgumentException(errorMessage);
//        }
//        try {
//            Constructor constructor = resolveClazz.getConstructor();
//        } catch (Exception error) {
//            String errorMessage = String.format("%s has no default constructor.", resolveClazz.getName());
//            throw new IllegalArgumentException(errorMessage);
//        }
        beanMap.put(resolveClazz, beanClazz);
        beanCache.add(resolveClazz);
        beanCache.add(beanClazz);
    }

    @Override
    public <T> T getBean(Class<T> resolveClazz) throws IllegalAccessException, InstantiationException {
        this.isGettingBean = true;
        if (beanMap.containsKey(resolveClazz)) {
            resolveClazz = (Class<T>) beanMap.get(resolveClazz);
        }
        try {
            if (resolveClazz == null) {
                throw new IllegalArgumentException();
            }
            if (!this.beanCache.contains(resolveClazz)) {
                throw new IllegalStateException();
            }

            return resolveClazz.newInstance();
        } finally {
            this.isGettingBean = false;
        }

    }

}
