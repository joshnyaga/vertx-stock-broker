package com.joshua.tutorials.vertx_stock_broker;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestAssetsRestApi extends AbstractRestApiTest{
  private static final Logger LOG = LoggerFactory.getLogger(TestAssetsRestApi.class);


  @Test
  void return_all_assets(Vertx vertx, VertxTestContext context) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_PORT_NUMBER));
    client.get("/assets")
      .send()
      .onComplete(context.succeeding(response->{
       var json = response.bodyAsJsonArray();
       LOG.info("Response: {}", json);
       assertEquals("[{\"name\":\"ASSL\"},{\"name\":\"AMZN\"},{\"name\":\"FB\"},{\"name\":\"GOOG\"},{\"name\":\"MSFT\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"}]", json.encode());
        assertEquals(200, response.statusCode());
        //create a header of content-type
        //use the http headers from vertx core
        assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
        //custom headers
        assertEquals("my-value", response.getHeader("my-header"));
        context.completeNow();
      }));
  }
}
