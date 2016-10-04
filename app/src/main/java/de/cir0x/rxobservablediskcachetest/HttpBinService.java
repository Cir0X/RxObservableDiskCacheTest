package de.cir0x.rxobservablediskcachetest;

import retrofit2.http.POST;
import rx.Observable;

public interface HttpBinService {

    @POST("/post")
    Observable<HttpBinResponse> getData();

}
