package com.joshua.tutorials.vertx_stock_broker.assets;

import com.joshua.tutorials.vertx_stock_broker.MainVerticle;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static com.joshua.tutorials.vertx_stock_broker.assets.AssetsRestApi.ASSETS;

public class GetAssetsHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void handle(RoutingContext context) {

      final JsonArray res = new JsonArray();
      ASSETS.stream().map(Asset::new).forEach(res::add);

      LOG.info("Path {} responds with {}", context.normalizedPath(), res);
      context.response().putHeader(
          HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .putHeader("my-header", "my-value")
        .end(res.toBuffer());
    }
  }

