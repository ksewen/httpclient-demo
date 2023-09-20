package com.github.ksewen.http.client.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.SocketTimeoutException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.Timeout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ksewen
 * @date 19.09.2023 15:50
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {"server.port=38080"})
class TestControllerTest {

  @Test
  // passed
  void socketWithoutSO() throws Exception {
    PoolingHttpClientConnectionManager connectionManager =
        PoolingHttpClientConnectionManagerBuilder.create()
            .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
            .setConnPoolPolicy(PoolReusePolicy.LIFO)
            .build();

    connectionManager.setDefaultConnectionConfig(
        ConnectionConfig.custom().setSocketTimeout(Timeout.ofSeconds(5)).build());

    CloseableHttpClient httpClient =
        HttpClients.custom().setConnectionManager(connectionManager).build();

    SocketTimeoutException exception =
        Assertions.assertThrows(
            SocketTimeoutException.class,
            () ->
                httpClient.execute(
                    new HttpGet("http://127.0.0.1:38080/ten"),
                    response -> EntityUtils.toString(response.getEntity())));
    assertThat(exception).isInstanceOf(SocketTimeoutException.class);
  }

  @Test
  // failed
  void socketWithoutSOReuseConnection() throws Exception {
    PoolingHttpClientConnectionManager connectionManager =
        PoolingHttpClientConnectionManagerBuilder.create()
            .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
            .setConnPoolPolicy(PoolReusePolicy.LIFO)
            .build();

    connectionManager.setDefaultConnectionConfig(
        ConnectionConfig.custom().setSocketTimeout(Timeout.ofSeconds(5)).build());

    CloseableHttpClient httpClient =
        HttpClients.custom().setConnectionManager(connectionManager).build();

    httpClient.execute(
        new HttpGet("http://127.0.0.1:38080"),
        response -> EntityUtils.toString(response.getEntity()));

    SocketTimeoutException exception =
        Assertions.assertThrows(
            SocketTimeoutException.class,
            () ->
                httpClient.execute(
                    new HttpGet("http://127.0.0.1:38080/ten"),
                    response -> EntityUtils.toString(response.getEntity())));
    assertThat(exception).isInstanceOf(SocketTimeoutException.class);
  }

  @Test
  // passed
  void socketWithSO() throws Exception {
    PoolingHttpClientConnectionManager connectionManager =
        PoolingHttpClientConnectionManagerBuilder.create()
            .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
            .setConnPoolPolicy(PoolReusePolicy.LIFO)
            .build();

    connectionManager.setDefaultSocketConfig(
        SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(5)).build());

    connectionManager.setDefaultConnectionConfig(
        ConnectionConfig.custom().setSocketTimeout(Timeout.ofSeconds(5)).build());

    CloseableHttpClient httpClient =
        HttpClients.custom().setConnectionManager(connectionManager).build();

    httpClient.execute(
        new HttpGet("http://127.0.0.1:38080"),
        response -> EntityUtils.toString(response.getEntity()));

    SocketTimeoutException exception =
        Assertions.assertThrows(
            SocketTimeoutException.class,
            () ->
                httpClient.execute(
                    new HttpGet("http://127.0.0.1:38080/ten"),
                    response -> EntityUtils.toString(response.getEntity())));
    assertThat(exception).isInstanceOf(SocketTimeoutException.class);
  }
}
