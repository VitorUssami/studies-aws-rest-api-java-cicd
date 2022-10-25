//package helloworld;
//
//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
//import com.studies.aws.sam.PostHandler;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import org.junit.Test;
//
//public class AppTest {
//  @Test
//  public void successfulResponse() {
//    PostHandler app = new PostHandler();
//    APIGatewayProxyResponseEvent result = app.handleRequest(null, null);
//    assertEquals(200, result.getStatusCode().intValue());
//    assertEquals("application/json", result.getHeaders().get("Content-Type"));
//    String content = result.getBody();
//    assertNotNull(content);
//    assertTrue(content.contains("\"message\""));
//    assertTrue(content.contains("\"hello world\""));
//    assertTrue(content.contains("\"location\""));
//  }
//}
