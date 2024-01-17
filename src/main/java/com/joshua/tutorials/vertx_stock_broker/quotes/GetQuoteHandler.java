package com.joshua.tutorials.vertx_stock_broker.quotes;

import com.joshua.tutorials.vertx_stock_broker.MainVerticle;
import com.joshua.tutorials.vertx_stock_broker.db.DbErrorHelper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class GetQuoteHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);
  private final Map<String, Quote> cachedQuotes;
  public GetQuoteHandler(Map<String, Quote> cachedQuotes) {
    this.cachedQuotes = cachedQuotes;
  }

  @Override
  public void handle(RoutingContext context) {

      final String assetsParam = context.pathParam("asset");
      LOG.debug("Assets parameter: {}", assetsParam);


      var mayBeQuote = Optional.ofNullable(cachedQuotes.get(assetsParam));
      if(mayBeQuote.isEmpty()){
        DbErrorHelper.NotFoundResponse(context, "Quote for asset "+ assetsParam + " not found");
        return;
      }
      final JsonObject res = mayBeQuote.get().toJsonObject();
      LOG.info("Path {} responds with {}", context.normalizedPath(), res);
      context.response().end(res.toBuffer());

  }
}
