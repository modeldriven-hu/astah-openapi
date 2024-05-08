package hu.modeldriven.openapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MockTest {

    @Test
    public void testMocking(){
        var myClass = Mockito.mock(MyClass.class);

        Mockito.doNothing().when(myClass).myMethod();

        myClass.myMethod();
    }

    class MyClass{


        void myMethod(){
            System.out.println("Shall not see this");
        }

    }

}
