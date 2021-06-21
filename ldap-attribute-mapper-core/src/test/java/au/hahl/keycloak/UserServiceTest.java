package au.hahl.keycloak;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;


/**
 * @author Mark Hahl <mark@hahl.id.au>
 */
 public class UserServiceTest {
    
    private String VALID_RESPONSE;
    private String INVALID_RESPONSE;

    @Before
    public void setUp() throws Exception {
        this.VALID_RESPONSE = new String(Files.readAllBytes(Paths.get("src/test/resources/test_valid_response.json")));
        this.INVALID_RESPONSE = new String(Files.readAllBytes(Paths.get("src/test/resources/test_invalid_response.json")));
    }

    /**
     * Test that if hte api returns the expected information
     * the userDetails class is poulated.
     * @throws Exception
     */
    @Test
    public void should_return_true_if_api_works() throws Exception {

        try (var mockApi = new MockWebServer();) {

            mockApi.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(VALID_RESPONSE)
                .setResponseCode(200));
            mockApi.start();
            String url = mockApi.url("/").toString();
            UserService service = new UserService(url);
            UserDetails details = service.getUserDetails("test");
            Assert.assertTrue(details.getAttrbutes().size() > 0);
            Assert.assertTrue(details.username.equals("test"));
        }
    }

    @Test
    public void should_fail_if_api_return_not_200() throws IOException  {

        try (var mockApi = new MockWebServer();) {

            mockApi.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(INVALID_RESPONSE)
                .setResponseCode(500));
            mockApi.start();

            String url = mockApi.url("/").toString();
            
            Exception invalidUserException = Assert.assertThrows(InvalidUserException.class, () -> {
                UserService service = new UserService(url);
                var u = service.getUserDetails("test");
                System.out.println(u.toString());
            });


            String expectedError = "http request failed";
            Assert.assertTrue(invalidUserException.getMessage().contains(expectedError));
        }
    }

    @Test
    public void should_fail_if_user_info_incomplete() throws IOException  {

        try (var mockApi = new MockWebServer();) {

            mockApi.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(INVALID_RESPONSE)
                .setResponseCode(200));
            mockApi.start();

            String url = mockApi.url("/").toString();
            
            Exception invalidUserException = Assert.assertThrows(InvalidUserException.class, () -> {
                UserService service = new UserService(url);
                var u = service.getUserDetails("test");
            });


            String expectedError = "invalid json";
            Assert.assertTrue(invalidUserException.getMessage().contains(expectedError));
        }
    }

}
