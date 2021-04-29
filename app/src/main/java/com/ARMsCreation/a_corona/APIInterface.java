package com.ARMsCreation.a_corona;

import com.ARMsCreation.a_corona.Pojo.Data;
import com.ARMsCreation.a_corona.Pojo.Regional;
import com.ARMsCreation.a_corona.Pojo.Statewise;
import com.ARMsCreation.a_corona.Pojo.Summary;

import retrofit2.Call;
import retrofit2.http.GET;

interface APIInterface {
    //String baseurl = "https://api.rootnet.in/covid19-in/unofficial/covid19india.org/statewise/";
    String baseurl = "https://api.rootnet.in/covid19-in/stats/latest/";

    @GET("data")
   Call<Data> doGetListResources();
    @GET("data")
    Call<Statewise> datatwise();

    String baseurl1="https://api.covid19india.org/v2/state_district_wise.json";

}
