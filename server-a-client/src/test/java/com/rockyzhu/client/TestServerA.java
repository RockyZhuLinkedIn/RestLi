package com.rockyzhu.client;

import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.rockyzhu.api.AddKey;
import com.rockyzhu.api.Buddy;
import com.rockyzhu.resources.AddRequestBuilders;
import com.rockyzhu.resources.ReverseRequestBuilders;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

/**
 * @author Rocky Zhu
 */
public class TestServerA {

  private final static AddRequestBuilders ADD_REQUEST_BUILDERS = new AddRequestBuilders();
  private final static ReverseRequestBuilders REVERSE_REQUEST_BUILDERS = new ReverseRequestBuilders();

  final HttpClientFactory http = new HttpClientFactory();
  final Client r2Client = new TransportClientAdapter(
      http.getClient(Collections.<String, String>emptyMap()));

  private final RestClient _restClient = new RestClient(r2Client, "http://localhost:7070/server-a/");

  @Test
  public void testAdd() {
    GetRequest<Buddy> getRequestAdd = ADD_REQUEST_BUILDERS.get()
        .id(new ComplexResourceKey<>(new AddKey().setA(5).setB(16), new EmptyRecord()))
        .build();
    try {
      Buddy buddyAdd = _restClient.sendRequest(getRequestAdd).getResponseEntity();
      Assert.assertEquals(buddyAdd.getMessage(), "21");
    } catch (RemoteInvocationException e) {
      System.out.println("exception found in add");
      Assert.fail();
    }
  }

  @Test
  public void testReverse() {
    GetRequest<Buddy> getRequestReverse = REVERSE_REQUEST_BUILDERS.get()
        .id(123l)
        .build();
    try {
      Buddy buddyReverse = _restClient.sendRequest(getRequestReverse).getResponseEntity();
      Assert.assertEquals(buddyReverse.getMessage(), "321");
    } catch (RemoteInvocationException e) {
      System.out.println("exception found in reverse");
      Assert.fail();
    }
  }
}
