package com.ARMsCreation.a_corona;

import com.ARMsCreation.a_corona.Pojo.MultipleResource;

import retrofit2.Call;
import retrofit2.http.GET;

interface APIInterface {
    String baseurl = "https://api.rootnet.in/covid19-in/unofficial/covid19india.org/statewise/";

    @GET("data")
    Call<MultipleResource> doGetListResources();
    String baseurl1="https://api.covid19india.org/v2/state_district_wise.json";

}
