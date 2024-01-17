package com.joshua.tutorials.vertx_stock_broker;

import io.vertx.core.Vertx;
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
public class TestQuoteRestApi extends  AbstractRestApiTest{
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Test
  void return_quote_for_asset(Vertx vertx, VertxTestContext context) throws Throwable {
    var client = getWebClient(vertx);
      client.get("/quote/AMZN")
      .send()
      .onComplete(context.succeeding(response->{
       var json = response.bodyAsJsonObject();
       LOG.info("Response: {}", json);
       assertEquals("{\"name\":\"AMZN\"}" , json.getJsonObject("asset").encode());
        assertEquals(200, response.statusCode());
        context.completeNow();
      }));
  }
  @Test
  void return_not_found_for_unknown_asset(Vertx vertx, VertxTestContext context) throws Throwable {
    var client = getWebClient(vertx);
    client.get("/quote/UNKNOWN")
      .send()
      .onComplete(context.succeeding(response->{
        var json = response.bodyAsJsonObject();
        LOG.info("Response: {}", json);
        assertEquals("{\"message\":\"quotes for asset UNKNOWN not available\",\"path\":\"/quote/UNKNOWN\"}" , json.encode());
        assertEquals(404, response.statusCode());
        context.completeNow();
      }));
  }

  private static WebClient getWebClient(Vertx vertx) {
    return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_PORT_NUMBER));
  }
}
