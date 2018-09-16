public class MyBeanIllegalAccess {

    public MyBeanIllegalAccess() throws InstantiationException, IllegalAccessException {
        throw new IllegalAccessException();
//        throw new InstantiationException();
    }


}
