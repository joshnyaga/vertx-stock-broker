package com.joshua.tutorials.vertx_stock_broker.db;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbErrorHelper {
  private static final Logger LOG = LoggerFactory.getLogger(DbErrorHelper.class);

  public static Handler<Throwable> errorHandler(RoutingContext context, String message) {
    return error -> {
      LOG.error("Failure: ", error);
      context.response()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(new JsonObject()
          .put("message", message)
          .put("path", context.normalizedPath())
          .toBuffer()
        );
    };
  }
  public static void NotFoundResponse(RoutingContext context,String message) {
    context.response()
      .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
      .end(new JsonObject()
        .put("message", message)
        .put("path", context.normalizedPath())
        .toBuffer()
      );
  }
}
