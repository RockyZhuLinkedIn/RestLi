package com.rockyzhu.services;

import com.rockyzhu.api.Buddy;
import org.springframework.stereotype.Component;

/**
 * Created by hozhu on 10/19/16.
 */
@Component("reverseService")
public class ReverseService {

  public Buddy get(Long key) {
    long origKey = key;
    long result = 0;
    while (key != 0) {
      result = result * 10 + key % 10;
      key /= 10;
    }
    Buddy buddy = new Buddy().setMessage(String.valueOf(result));
    System.out.println(origKey + " -> "+ buddy.getMessage());
    return buddy;
  }
}
