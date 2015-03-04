package no.utgdev.lightdi.bean;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

public class BeanFactoryTest {

    @Before
    public void setUp() throws Exception {
        BeanFactory.start("");
    }

    @Test
    public void getInstanceShouldReturnAnInstanceIfCalledAfterStart() throws Exception {
        assertThat(BeanFactory.getInstance(), is(not(nullValue())));
    }
}

