package com.bhavsar.airBnb.service;

import com.bhavsar.airBnb.model.Session;
import com.bhavsar.airBnb.model.User;

public interface SessionService {

    Session generateNewSession(User user , String refreshToken);
    void validateSession(String refreshToken);

}
