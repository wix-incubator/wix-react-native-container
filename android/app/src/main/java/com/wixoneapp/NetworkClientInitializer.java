
 package com.wixoneapp;

import android.os.Build;

import com.facebook.react.modules.network.OkHttpClientProvider;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkClientInitializer {
    public static void init() {
        OkHttpClient originalClient = OkHttpClientProvider.getOkHttpClient();

        OkHttpClient.Builder builder = originalClient.newBuilder();
        builder.addNetworkInterceptor(new UserAgentInterceptor(getWixUserAgent()));
        OkHttpClient newClient = builder.build();

        OkHttpClientProvider.replaceOkHttpClient(newClient);
    }

    private NetworkClientInitializer() {
    }

    private static String getWixUserAgent() {
        return getDefaultUserAgent() + " Wix/0";
    }

    private static String getDefaultUserAgent() {
            return "Android/" + Build.VERSION.SDK_INT;
    }

    private static class UserAgentInterceptor implements Interceptor {

        private final String userAgent;

        public UserAgentInterceptor(String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .header("User-Agent", userAgent)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }

}
