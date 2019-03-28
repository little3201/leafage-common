package top.abeille.common.mock;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * ServiceTest Parent
 *
 * @author liwenqiang 2018/12/28 14:40
 **/
@RunWith(MockitoJUnitRunner.class)
public abstract class BasicServiceMock {

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }
}

