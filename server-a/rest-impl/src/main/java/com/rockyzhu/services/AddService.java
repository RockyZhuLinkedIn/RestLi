package com.rockyzhu.services;

import com.rockyzhu.api.AddKey;
import com.rockyzhu.api.Buddy;
import org.springframework.stereotype.Component;

/**
 * Created by hozhu on 10/19/16.
 */
@Component("addService") // single ton
public class AddService {
  public Buddy get(AddKey addKey) {
    Buddy buddy = new Buddy().setMessage(String.valueOf(addKey.getA() + addKey.getB()));
    System.out.println(addKey.getA() + " + " + addKey.getB() + " = " +buddy.getMessage());
    return buddy;
  }
}
