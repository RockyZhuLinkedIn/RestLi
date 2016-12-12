package com.rockyzhu.client;

import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.rockyzhu.api.AddKey;
import com.rockyzhu.api.Buddy;
import com.rockyzhu.resources.AddRequestBuilders;
import com.rockyzhu.resources.ReverseRequestBuilders;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by hozhu on 12/7/16.
 */
public class TestD2Client {

  private final static AddRequestBuilders ADD_REQUEST_BUILDERS = new AddRequestBuilders();
  private final static ReverseRequestBuilders REVERSE_REQUEST_BUILDERS = new ReverseRequestBuilders();

  private RestClient _restClient;

  private Object[][] _testData = new Object[][] {
      {"d2://add", 3, 5},
      {"d2://add", 32, 57},
      {"d2://add", 652, 7857},
      {"d2://add", 10, 86},
      {"d2://add", 101, 77},
      {"d2://add", 1011, 237},
      {"d2://reverse", 1l},
      {"d2://reverse", 12l},
      {"d2://reverse", 123l},
      {"d2://reverse", 1234l},
      {"d2://reverse", 12345l},
      {"d2://reverse", 12346l},
  };

  @BeforeMethod
  public void before() throws IOException, ParseException, InterruptedException {
    String path = new File(new File(".").getAbsolutePath()).getCanonicalPath() + "/src/main/config/D2Client.json";
    JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(path));
    _restClient = new RestClient(D2ClientFactory.createInstance(json), "d2://");
  }

  @AfterMethod
  public void after() {
    D2ClientFactory.shutDown();
  }

  @Test
  public void test() throws URISyntaxException, RemoteInvocationException, InterruptedException {

    for (Object[] testData : _testData) {
      URI uri = new URI((String)testData[0]);
      if (testData.length == 3) {
        GetRequest<Buddy> getRequestAdd = ADD_REQUEST_BUILDERS.get()
            .id(new ComplexResourceKey<>(new AddKey().setA((Integer) testData[1]).setB((Integer) testData[2]), new EmptyRecord()))
            .build();
        Buddy result = _restClient.sendRequest(getRequestAdd).getResponseEntity();
        System.out.println(testData[1] + " + " + testData[2] + " = " + result.getMessage());
      } else {
        GetRequest<Buddy> getRequestReverse = REVERSE_REQUEST_BUILDERS.get()
            .id((Long) testData[1])
            .build();
        Buddy result = _restClient.sendRequest(getRequestReverse).getResponseEntity();
        System.out.println(testData[1] + " -> " + result.getMessage());
      }
    }
  }
}
