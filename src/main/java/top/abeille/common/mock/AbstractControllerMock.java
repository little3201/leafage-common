/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.mock;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

/**
 * Controller Parent
 *
 * @author liwenqiang 2018/12/28 14:40
 **/
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerMock<T> {

    /**
     * 开启日志
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractControllerMock.class);

    private WebTestClient webTestClient;

    /**
     * 设置要测试的controller
     *
     * @return T
     */
    protected abstract T getController();

    @Before
    public void setupMock() {
        /* initialize mock object */
        this.webTestClient = WebTestClient.bindToController(getController()).build();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * ====================  POST ====================
     */
    public WebTestClient.ResponseSpec postTest(String url, Object obj) {
        return webTestClient.post().uri(url).syncBody(obj).exchange();
    }

    /**
     * ====================  GET ====================
     */
    public WebTestClient.ResponseSpec getTest(String url, Consumer<HttpHeaders> params) {
        return webTestClient.get().uri(url).headers(params).exchange();
    }

    /**
     * ====================  PUT  ====================
     */
    public WebTestClient.ResponseSpec putTest(String url, Object obj) {
        return webTestClient.put().uri(url).syncBody(obj).exchange();
    }

    /**
     * ====================  DELETE  ====================
     */
    public WebTestClient.ResponseSpec deleteTest(String url, Consumer<HttpHeaders> params) throws Exception {
        return webTestClient.delete().uri(url).headers(params).exchange();
    }
}
