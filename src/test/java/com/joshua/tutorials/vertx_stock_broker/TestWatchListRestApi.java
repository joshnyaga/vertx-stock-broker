package com.joshua.tutorials.vertx_stock_broker;

import com.joshua.tutorials.vertx_stock_broker.assets.Asset;
import com.joshua.tutorials.vertx_stock_broker.watchlist.WatchList;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi extends AbstractRestApiTest {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);


  @Test
  void adds_and_returns_watchlist_for_an_account(Vertx vertx, VertxTestContext context) throws Throwable {
    var client =  getWebClient(vertx);
    //generate a random account id
    var accountId = UUID.randomUUID();
    //call the request
    client.put("/account/watchlist/"+ accountId.toString())
      .sendJsonObject(body())
      .onComplete(context.succeeding(response->{
        var json = response.bodyAsJsonObject();
        LOG.info("Response: {}", json);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}" , json.encode());
        assertEquals(200, response.statusCode());

      })).compose(next->{
        client.get("/account/watchlist/"+ accountId.toString())
          .send()
          .onComplete(context.succeeding(res->{
            var json = res.bodyAsJsonObject();
            LOG.info("Response GET: {}", json);
            assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}" , json.encode());
            assertEquals(200, res.statusCode());
            //complete the test
            context.completeNow();
          }));
        return Future.succeededFuture();
      });
  }
  @Test
  void adds_and_delete_watchlist_for_account(Vertx vertx, VertxTestContext context){
    var client = getWebClient(vertx);
    //generate a random account id
    var accountId = UUID.randomUUID();
    //call the request
    client.put("/account/watchlist/"+ accountId.toString())
      .sendJsonObject(body())
      .onComplete(context.succeeding(response->{
        var json = response.bodyAsJsonObject();
        LOG.info("Response: {}", json);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}" , json.encode());
        assertEquals(200, response.statusCode());

      })).compose(next->{
        client.delete("/account/watchlist/"+ accountId.toString())
          .send()
          .onComplete(context.succeeding(res->{
            var json = res.bodyAsJsonObject();
            LOG.info("Response DELEtE: {}", json);
            assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}" , json.encode());
            assertEquals(200, res.statusCode());
            //complete the test
            context.completeNow();
          }));
        return Future.succeededFuture();
      });

  }
  private static JsonObject body() {
    return new WatchList(Arrays.asList(
      new Asset("AMZN"),
      new Asset("TSLA"))).toJsonObject();
  }
  private static WebClient getWebClient(Vertx vertx) {
    return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_PORT_NUMBER));
  }
}
