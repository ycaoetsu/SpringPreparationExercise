import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.font.TrueTypeFont;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    IoCContext context = null;
    @BeforeEach
    public void setUp() {
        context = new IoCContextImpl();
    }

    @Test
    void should_throw_exception_when_beanClazz_null_when_register_with_one_parameter() {
        try {
            context.registerBean(null);
        } catch (IllegalArgumentException error) {
            assertEquals("beanClazz is mandatory", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_beanClazz_abstract_when_register_with_one_parameter() {
        try {
            context.registerBean(MyAbstractBean.class);
        } catch (IllegalArgumentException error) {
            assertEquals("MyAbstractBean is abstract", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_beanClazz_interface_when_register_with_one_parameter() {
        try {
            context.registerBean(MyInterfaceBean.class);
        } catch (IllegalArgumentException error) {
            assertEquals("MyInterfaceBean is abstract", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_no_default_constructor_when_register_with_one_parameter() {
        try {
            context.registerBean(MyNoDefaultConstructorClass.class);
            fail("not caught expected IllegalArgumentException when no default constructor");
        } catch (IllegalArgumentException error) {
            assertEquals("MyNoDefaultConstructorClass has no default constructor.", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_private_constructor_when_register_with_one_parameter() {
        try {
            context.registerBean(MyPrivateConstructorBean.class);
            fail("not caught expected IllegalArgumentException when private constructor");
        } catch (IllegalArgumentException error) {
            assertEquals("MyPrivateConstructorBean has no default constructor.", error.getMessage());
        }
    }

    @Test
    void should_not_throw_exception_when_having_default_constructor() {
        try {
            context.registerBean(MyBean.class);
        } catch (IllegalArgumentException error) {
            assertEquals(null, error.getMessage());
        }
    }

    @Test
    void should_no_error_message_when_pass_an_already_registered_beanClazz_when_register_with_one_parameter() {
        context.registerBean(MyBean.class);
        try {
            context.registerBean(MyBean.class);
        } catch (Exception error) {
            assertEquals(null, error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_resolveClazz_null() {
        context.registerBean(MyBean.class);
        Exception exception = null;
        try {
            context.getBean(null);
        } catch (Exception error) {
            exception = error;
        }
        assertEquals(IllegalArgumentException.class, exception.getClass());
    }

    @Test
    void should_throw_exception_when_no_register_before_getBean() {
        try {
            context.registerBean(null);
            MyBean myBeanInstance = context.getBean(MyBean.class);
        } catch (Exception error) {
            assertEquals(IllegalArgumentException.class, error.getClass());
        }
    }

    @Test
    void should_throw_instantiation_exception_when_class_cannot_be_instantiated() {
        context.registerBean(MyBeanCannotInstantiate.class);
        try {
            context.getBean(MyBeanCannotInstantiate.class);
            fail("not throw instantiation exception");
        } catch (InstantiationException error) {
            assertTrue(true, "Instantiation Exception Caught");
        } catch (IllegalAccessException e) {
            fail("Expected: instantiation exception, but caught IllegalAccessException");
        }
    }

    @Test
    void should_throw_illegal_access_exception_when_class_cannot_be_accessed_legally() {
        context.registerBean(MyBeanIllegalAccess.class);
        try {
            context.getBean(MyBeanIllegalAccess.class);
            fail("not throw IllegalAccessException exception");
        } catch (InstantiationException error) {
            fail("Expected: IllegalAccessException exception, but caught Instantiation exception");
        } catch (IllegalAccessException e) {
            assertTrue(true, "IllegalAccessException Exception Caught");
        }
    }

    @Test
    void should_throw_illegal_when_register_bean_while_getting_bean() {
        try {
            new Thread(() -> {
                context.registerBean(MySleepBean.class);
                try {
                    context.getBean(MySleepBean.class);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }).start();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.registerBean(MyBeanBase.class, MyBean.class);
            fail("cannot catch expected IllegalStateException when registering bean while gettingBean.");
        } catch (IllegalStateException e) {
            assertTrue(true, "IllegalStateException caught");
        }
    }

    @Test
    void should_override_resolveClazz_when_registering_same_base() {
        context.registerBean(MyBeanBase.class, MyBean.class);
        context.registerBean(MyBeanBase.class, MyBeanCooler.class);
        try {
            assertEquals("MyBeanCooler", context.getBean(MyBeanBase.class).getClass().getName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Test
    void should_throw_exception_when_resolveClazz_null_when_register_with_two_parameters() {
        try {
            context.registerBean(null, MyBean.class);
        } catch (IllegalArgumentException error) {
            assertEquals("resolveClazz is mandatory", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_beanClazz_null_when_register_with_two_parameters() {
        try {
            context.registerBean(MyBean.class, null);
        } catch (IllegalArgumentException error) {
            assertEquals("beanClazz is mandatory", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_beanBaseClazz_abstract_when_register_with_two_parameter() {
        try {
            context.registerBean(MyAbstractBaseBean.class, MyAbstractBaseBeanChild.class);
        } catch (IllegalArgumentException error) {
            assertEquals("MyAbstractBaseBean is abstract", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_beanClazz_abstract_when_register_with_two_parameter() {
        try {
            context.registerBean(MyAbstractBeanParent.class, MyAbstractBaseBean.class);
        } catch (IllegalArgumentException error) {
            assertEquals("MyAbstractBaseBean is abstract", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_beanClazz_interface_when_register_with_two_parameter() {
        try {
            context.registerBean(MyInterfaceBean.class, MyInterfaceBeanImplement.class);
        } catch (IllegalArgumentException error) {
            assertEquals("MyInterfaceBean is abstract", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_no_resolve_default_constructor_when_register_with_two_parameter() {
        try {
            context.registerBean(MyNoDefaultConstructorClass.class, MyNoDefaultConstructorClass.class);
            fail("not caught expected IllegalArgumentException when no default constructor");
        } catch (IllegalArgumentException error) {
            assertEquals("MyNoDefaultConstructorClass has no default constructor.", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_bean_no_default_constructor_when_register_with_two_parameter() {
        try {
            context.registerBean(MyBean.class, MyNoDefaultConstructorClassChild.class);
            fail("not caught expected IllegalArgumentException when no default constructor");
        } catch (IllegalArgumentException error) {
            assertEquals("MyNoDefaultConstructorClassChild has no default constructor.", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_resolve_private_constructor_when_register_with_two_parameter() {
        try {
            context.registerBean(MyPrivateConstructorBean.class, MyPrivateConstructorBean.class);
            fail("not caught expected IllegalArgumentException when private constructor");
        } catch (IllegalArgumentException error) {
            assertEquals("MyPrivateConstructorBean has no default constructor.", error.getMessage());
        }
    }

    @Test
    void should_throw_exception_when_bean_private_constructor_when_register_with_two_parameter() {
        try {
            context.registerBean(MyBean.class, MyPrivateConstructorBeanChild.class);
            fail("not caught expected IllegalArgumentException when private constructor");
        } catch (IllegalArgumentException error) {
            assertEquals("MyPrivateConstructorBeanChild has no default constructor.", error.getMessage());
        }
    }
}
