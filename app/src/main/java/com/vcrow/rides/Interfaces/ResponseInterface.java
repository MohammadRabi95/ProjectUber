package com.vcrow.rides.Interfaces;

public interface ResponseInterface {
    void onResponse(Object... params);

    void onError(String error);

}
