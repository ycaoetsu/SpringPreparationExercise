public class MyBeanCannotInstantiate {
    public MyBeanCannotInstantiate() throws InstantiationException {
//        throw new IllegalAccessException();
        throw new InstantiationException();
    }
}
