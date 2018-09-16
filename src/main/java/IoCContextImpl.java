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
            checkNullClass(beanClazz, "beanClazz");
        }
        if (beanCache.contains(beanClazz)) {
            return;
        }
        if (Modifier.isAbstract(beanClazz.getModifiers()) || Modifier.isInterface(beanClazz.getModifiers())) {
            checkModifierAbstract(beanClazz);
        }
        checkConstructor(beanClazz);
        beanCache.add(beanClazz);
        beanMap.put(beanClazz, beanClazz);
    }

    public void checkNullClass(Class<?> nullClazz, String clazzName) {
        String errorMessage = String.format("%s is mandatory", clazzName);
        throw new IllegalArgumentException(errorMessage);
    }

    @Override
    public <T> void registerBean(Class<? super T> resolveClazz, Class<T> beanClazz) {
        if (isGettingBean) {
            throw new IllegalStateException();
        }
        if (beanClazz == null) {
           checkNullClass(beanClazz, "beanClazz");
        }
        if (resolveClazz == null) {
            checkNullClass(resolveClazz, "resolveClazz");
        }
        if (beanCache.contains(beanClazz)) {
            return;
        }
        if (Modifier.isAbstract(beanClazz.getModifiers()) || Modifier.isInterface(beanClazz.getModifiers())) {
            checkModifierAbstract(beanClazz);
        }
        if (Modifier.isAbstract(resolveClazz.getModifiers()) || Modifier.isInterface(resolveClazz.getModifiers())) {
            checkModifierAbstract(resolveClazz);
        }
        checkConstructor(beanClazz);
        checkConstructor(resolveClazz);
        beanMap.put(resolveClazz, beanClazz);
        beanCache.add(resolveClazz);
        beanCache.add(beanClazz);
    }

    public void checkConstructor(Class<?> clazz) {
        try {
            Constructor constructor = clazz.getConstructor();
        } catch (Exception error) {
            String errorMessage = String.format("%s has no default constructor.", clazz.getName());
            throw new IllegalArgumentException(errorMessage);
        }
    }
    public void checkModifierAbstract(Class<?> clazz) {
        String errorMessage = String.format("%s is abstract", clazz.getName());
        throw new IllegalArgumentException(errorMessage);
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
