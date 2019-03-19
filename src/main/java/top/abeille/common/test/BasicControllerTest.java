package top.abeille.common.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;

/**
 * Controller Parent
 *
 * @author liwenqiang 2018/12/28 14:40
 **/
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public abstract class BasicControllerTest<T> {

    private MockMvc mockMvc;

    protected abstract T getController();

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    @Before
    public void setupMock() {
        /* initialize mock object */
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.getController()).build();
    }

    /* ====================  POST 重载 添加参数到request body ====================*/
    public MockHttpServletResponse postTest(String url, Object obj) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url).accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj)));
        return resultActions.andReturn().getResponse();
    }

    /* ====================  POST 重载 添加Map类型参数到请求中 ====================*/
    public MockHttpServletResponse postTest(String url, MultiValueMap<String, String> params) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url).params(params).accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        return resultActions.andReturn().getResponse();
    }

    /* ====================  GET 重载 添加参数到request body ====================*/
    public MockHttpServletResponse getTest(String url, Object obj) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj)));
        return resultActions.andReturn().getResponse();
    }

    /* ====================  GET 重载 添加Map类型参数到请求中 ====================*/
    public MockHttpServletResponse getTest(String url, MultiValueMap<String, String> params) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url).params(params).accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        return resultActions.andReturn().getResponse();
    }

    /* ====================  PUT  ====================*/
    public MockHttpServletResponse putTest(String url, Object obj) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put(url).accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj)));
        return resultActions.andReturn().getResponse();
    }

    /* ====================  DELETE  ====================*/
    public MockHttpServletResponse deleteTest(String url, Object obj) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url).accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(new ObjectMapper().writeValueAsString(obj)));
        return resultActions.andReturn().getResponse();
    }
}
