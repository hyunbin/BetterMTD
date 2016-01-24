package me.hyunbin.transit;


import java.util.List;

import me.hyunbin.transit.models.AutoCompleteItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Hyunbin on 1/23/16.
 */
public interface AutoCompleteInterface {

    @GET("search")
    Call<List<AutoCompleteItem>> getSuggestions(
            @Query("query") String query
    );

}
