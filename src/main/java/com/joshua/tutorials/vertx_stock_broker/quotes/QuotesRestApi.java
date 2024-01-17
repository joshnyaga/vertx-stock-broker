package com.joshua.tutorials.vertx_stock_broker.quotes;

import com.joshua.tutorials.vertx_stock_broker.MainVerticle;
import com.joshua.tutorials.vertx_stock_broker.assets.Asset;
import com.joshua.tutorials.vertx_stock_broker.assets.AssetsRestApi;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.SqlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static void attach(Router parent, SqlClient db){
    final Map<String, Quote> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol->{
      cachedQuotes.put(symbol, initRandomQuote(symbol)) ;
    });
    parent.get("/quote/:asset").handler(new GetQuoteHandler(cachedQuotes));
    parent.get("/pg/quotes/:asset").handler( new GetQuoteFromDatabaseHandler(db));
  }

  private static Quote initRandomQuote(String assetsParam) {
    return Quote.builder()
      .asset(new Asset(assetsParam))
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
