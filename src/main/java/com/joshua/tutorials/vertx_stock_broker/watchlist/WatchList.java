package com.joshua.tutorials.vertx_stock_broker.watchlist;


import com.joshua.tutorials.vertx_stock_broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {
   List<Asset> assets;

   public JsonObject toJsonObject (){
     return JsonObject.mapFrom(this);
   }
}
