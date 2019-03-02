package io.github.buniaowanfeng.event;

import io.github.buniaowanfeng.data.VersionInfo;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by caofeng on 16-7-31.
 */
public interface UpdateService {
    @GET("http://45.78.39.95/buniaowanfeng.json")
    Observable<VersionInfo> getNewVersion();
}
