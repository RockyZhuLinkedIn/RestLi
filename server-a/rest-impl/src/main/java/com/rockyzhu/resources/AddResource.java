package com.rockyzhu.resources;

import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.ComplexKeyResourceTemplate;
import com.rockyzhu.api.AddKey;
import com.rockyzhu.api.Buddy;
import com.rockyzhu.services.AddService;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Rocky Zhu
 */

@RestLiCollection(name = "add", namespace = "com.rockyzhu.resources")
public class AddResource extends ComplexKeyResourceTemplate<AddKey, EmptyRecord, Buddy> {

  @Inject @Named("addService")
  private AddService _addService;

  // http://localhost:7070/server-a/add/a=9&b=22
  @Override
  public Buddy get(ComplexResourceKey<AddKey, EmptyRecord> complexResourceKey) {
    AddKey addKey = complexResourceKey.getKey();
    return _addService.get(addKey);
  }
}
