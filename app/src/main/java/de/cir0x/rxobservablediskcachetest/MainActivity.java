package de.cir0x.rxobservablediskcachetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;
import com.pacoworks.rxobservablediskcache.Cached;
import com.pacoworks.rxobservablediskcache.RxObservableDiskCache;
import com.pacoworks.rxobservablediskcache.policy.TimeAndVersionPolicy;
import com.pacoworks.rxpaper.RxPaperBook;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private Button button;

    private HttpBinService httpBinService;

    private RxObservableDiskCache<HttpBinResponse, TimeAndVersionPolicy> cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        RxPaperBook.init(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://httpbin.org")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        httpBinService = retrofit.create(HttpBinService.class);

        cache = RxObservableDiskCache.create(
            RxPaperBook.with("data_cache"),
            TimeAndVersionPolicy.create(BuildConfig.VERSION_CODE),
            TimeAndVersionPolicy.validate(Long.MAX_VALUE, BuildConfig.VERSION_CODE));

        RxView.clicks(button).subscribe(click -> getDataWithEnabledCache().subscribe(httpBinResponseObserver));
    }

    private Observable<Cached<HttpBinResponse, TimeAndVersionPolicy>> getDataWithEnabledCache() {
        return cache.transform(getData(), "data");
    }

    private Single<HttpBinResponse> getData() {
        return httpBinService.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .onErrorReturn(throwable -> new HttpBinResponse("127.0.0.1", "http://127.0.0.1/post"))
                .toSingle();
    }

    private Observer<Cached<HttpBinResponse, TimeAndVersionPolicy>> httpBinResponseObserver = new Observer<Cached<HttpBinResponse, TimeAndVersionPolicy>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, e.getMessage());
        }

        @Override
        public void onNext(Cached<HttpBinResponse, TimeAndVersionPolicy> response) {
            if (response.isFromDisk) {
                Log.d(TAG, "cached value = " + response.value);
            } else {
                Log.d(TAG, "server value = " + response.value);
            }
        }

    };
}
