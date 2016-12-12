package com.rockyzhu.resources;

import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;
import com.rockyzhu.api.Buddy;
import com.rockyzhu.services.ReverseService;

import javax.inject.Inject;
import javax.inject.Named;

@RestLiCollection(name = "reverse", namespace = "com.rockyzhu.resources")
public class ReverseResource extends CollectionResourceTemplate<Long, Buddy> {

  @Inject
  @Named("reverseService")
  private ReverseService _reverseService;

  @Override
  public Buddy get(Long key) {
    return _reverseService.get(key);
  }
}
