package no.utgdev.lightdi;

import no.utgdev.lightdi.bean.BeanFactory;
import no.utgdev.lightdi.exceptions.LightDIAlreadyStartedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BeanFactory.class)
public class LightDITest {

    @Before
    public void setUp() throws Exception {
        BeanFactory factory = mock(BeanFactory.class);
        mockStatic(BeanFactory.class);
        doNothing().when(BeanFactory.class, "start", Mockito.anyString());
        doReturn(factory).when(BeanFactory.class, "getInstance");

        Whitebox.setInternalState(LightDI.class, "instance", (LightDI) null);
    }

    @Test
    public void startingLightDIShouldWork() {
        LightDI.start("");
    }

    @Test(expected = NullPointerException.class)
    public void startingLightDIWithNullPackageShouldThrowException() {
        LightDI.start(null);
    }

    @Test(expected = LightDIAlreadyStartedException.class)
    public void startingLightDIMultipleTimesShouldThrowException() {
        LightDI.start("");
        LightDI.start("");
    }
}