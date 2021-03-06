package com.veridu.idos.test.unit.endpoints;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.veridu.idos.IdOSAPIFactory;
import com.veridu.idos.endpoints.AbstractEndpoint;
import com.veridu.idos.endpoints.ProfileCandidates;
import com.veridu.idos.endpoints.ProfileTasks;
import com.veridu.idos.settings.Config;
import com.veridu.idos.test.unit.AbstractUnit;
import com.veridu.idos.utils.Filter;
import com.veridu.idos.utils.IdOSAuthType;
import com.veridu.idos.utils.IdOSUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractEndpoint.class, Request.class, Response.class, IdOSUtils.class, Content.class })
public class AbstractEndpointTest extends AbstractUnit {

    HashMap<String, String> credentials;

    @Before
    public void setUp() {
        this.credentials = this.getCredentials();
    }

    /**
     * Tests if the Constructor has the expected behavior
     *
     * @
     */
    @Test
    public void testConstructor() {
        ProfileTasks tasks = new ProfileTasks(credentials, Config.BASE_URL, false);
        assertSame("HANDLER", tasks.getAuthType().toString());
    }

    /**
     * Tests the fetch() method with the data parameter empty
     *
     * @throws Exception
     */
    @Test
    public void testFetchEmptyData() throws Exception {
        AbstractEndpoint abstractMock = Mockito.mock(AbstractEndpoint.class);
        JsonObject json = new JsonObject();
        json.addProperty("response", "response");
        abstractMock.setAuthType(IdOSAuthType.IDENTITY);
        Mockito.when(abstractMock.request("GET", Config.BASE_URL + "/profiles", null, null)).thenReturn(json);
        Mockito.when(WhiteboxImpl.invokeMethod(abstractMock, "fetch", "GET", "/profiles", null, null))
                .thenCallRealMethod();
        assertSame(json, WhiteboxImpl.invokeMethod(abstractMock, "fetch", "GET", "/profiles", null, null));
    }

    /**
     * Tests the fetch() method with data parameter and no filter
     *
     * @throws Exception
     */
    @Test
    public void testFetch() throws Exception {
        AbstractEndpoint abstractMock = Mockito.mock(AbstractEndpoint.class);
        JsonObject json = new JsonObject();
        json.addProperty("data", "data");
        JsonObject response = new JsonObject();
        response.addProperty("response", "response");
        Mockito.when(abstractMock.request("POST", Config.BASE_URL + "/profiles", json, null)).thenReturn(response);
        Mockito.when(WhiteboxImpl.invokeMethod(abstractMock, "fetch", "POST", "/profiles", json, null))
                .thenCallRealMethod();
        assertSame(response, WhiteboxImpl.invokeMethod(abstractMock, "fetch", "POST", "/profiles", json, null));
    }

    /**
     * Tests fetch() method with data and filter parameters
     *
     * @throws Exception
     */
    @Test
    public void testFetchWithFilter() throws Exception {
        Filter filter = Filter.createFilter();
        filter.addFilterByKeyName("filter", "filter");
        AbstractEndpoint abstractMock = Mockito.mock(AbstractEndpoint.class);
        JsonObject json = new JsonObject();
        json.addProperty("data", "data");
        JsonObject response = new JsonObject();
        response.addProperty("response", "response");
        Mockito.when(abstractMock.request("POST", Config.BASE_URL + "/profiles?filter=filter", json, filter))
                .thenReturn(response);
        Mockito.when(WhiteboxImpl.invokeMethod(abstractMock, "fetch", "POST", "/profiles", json, filter))
                .thenCallRealMethod();
        assertSame(response, WhiteboxImpl.invokeMethod(abstractMock, "fetch", "POST", "/profiles", json, filter));
    }

    /**
     * Tests the transformURL() with the GET method and check if the queries are
     * builded as expected
     *
     * @throws Exception
     */
    @Test
    public void testTransformURLGETMethod() throws Exception {
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("credentialPublicKey", "credentialPublicKey");
        credentials.put("creentialPrivateKey", "credentialPrivateKey");

        IdOSAPIFactory idOSAPIFactory = new IdOSAPIFactory(credentials);
        ProfileCandidates attribute = idOSAPIFactory.getCandidate();
        Object[] params = { "GET", "profiles/attributes", null };
        assertEquals(Config.BASE_URL + "/profiles/attributes",
                Whitebox.invokeMethod(attribute, "transformURL", params));
    }

    /**
     * Tests the transformURL() with the POST method and check if the queries
     * are builded as expected
     *
     * @throws Exception
     */
    @Test
    public void testTransformURLPOSTMethod() throws Exception {
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("credentialPublicKey", "credentialPublicKey");
        credentials.put("creentialPrivateKey", "credentialPrivateKey");
        IdOSAPIFactory idOSAPIFactory = new IdOSAPIFactory(credentials);
        ProfileCandidates attribute = idOSAPIFactory.getCandidate();
        JsonObject data = new JsonObject();
        data.addProperty("response", "response");
        Filter filter = Filter.createFilter();
        filter.addFilterByKeyName("filter", "filter");
        Object[] params = { "POST", "profiles/attributes", filter };
        assertEquals(Config.BASE_URL + "/profiles/attributes?filter=filter",
                Whitebox.invokeMethod(attribute, "transformURL", params));
    }

    @Test
    public void testConvertToJson() throws Exception {
        AbstractEndpoint endpoint = Mockito.mock(AbstractEndpoint.class, Mockito.CALLS_REAL_METHODS);
        JsonObject json = new JsonObject();
        json.addProperty("status", true);
        assertEquals(json, Whitebox.invokeMethod(endpoint, "convertToJson", "{\"status\":true}"));
    }

    @Test
    public void testSendRequestGetMethod() throws Exception {
        AbstractEndpoint endpointMock = Mockito.mock(AbstractEndpoint.class, Mockito.CALLS_REAL_METHODS);
        endpointMock.setCredentials(this.credentials);
        PowerMockito.mockStatic(IdOSUtils.class);
        JsonObject json = new JsonObject();
        json.addProperty("status", true);
        endpointMock.setAuthType(IdOSAuthType.HANDLER);
        PowerMockito.mockStatic(Request.class);
        PowerMockito.mockStatic(Response.class);
        PowerMockito.mockStatic(Content.class);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Content content = Mockito.mock(Content.class);
        when(Request.Get(anyString())).thenReturn(request);
        when(request.setHeader(anyString(), anyString())).thenReturn(request);
        PowerMockito.doReturn(response).when(request).execute();
        PowerMockito.doReturn(content).when(response).returnContent();
        when(content.toString()).thenReturn("{\"status\":true}");
        when(IdOSUtils.generateHandlerToken(this.credentials.get("servicePrivateKey"),
                this.credentials.get("servicePublicKey"), this.credentials.get("credentialPublicKey")))
                .thenReturn("token");
        assertEquals(json,
                WhiteboxImpl.invokeMethod(endpointMock, "sendRequest", "GET", "https://idos.api.io/1.0", null));
    }

    @Test
    public void testSendRequestPostMethod() throws Exception {
        AbstractEndpoint endpointMock = Mockito.mock(AbstractEndpoint.class, Mockito.CALLS_REAL_METHODS);
        endpointMock.setCredentials(this.credentials);
        PowerMockito.mockStatic(IdOSUtils.class);
        JsonObject json = new JsonObject();
        json.addProperty("status", true);
        endpointMock.setAuthType(IdOSAuthType.HANDLER);
        PowerMockito.mockStatic(Request.class);
        PowerMockito.mockStatic(Response.class);
        PowerMockito.mockStatic(Content.class);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Content content = PowerMockito.mock(Content.class);
        when(Request.Post(anyString())).thenReturn(request);
        when(request.setHeader(anyString(), anyString())).thenReturn(request);
        when(request.bodyByteArray(Matchers.anyObject(), Matchers.anyObject())).thenReturn(request);
        PowerMockito.doReturn(response).when(request).execute();
        PowerMockito.doReturn(content).when(response).returnContent();
        when(content.toString()).thenReturn("{\"status\":true}");
        when(IdOSUtils.generateHandlerToken(this.credentials.get("servicePrivateKey"),
                this.credentials.get("servicePublicKey"), this.credentials.get("credentialPublicKey")))
                .thenReturn("token");
        JsonObject data = new JsonObject();
        data.addProperty("key", "value");
        assertEquals(json,
                WhiteboxImpl.invokeMethod(endpointMock, "sendRequest", "POST", "https://idos.api.io/1.0", data));
    }

    @Test
    public void testSendRequestPutMethod() throws Exception {
        AbstractEndpoint endpointMock = Mockito.mock(AbstractEndpoint.class, Mockito.CALLS_REAL_METHODS);
        endpointMock.setCredentials(this.credentials);
        PowerMockito.mockStatic(IdOSUtils.class);
        JsonObject json = new JsonObject();
        json.addProperty("status", true);
        endpointMock.setAuthType(IdOSAuthType.HANDLER);
        PowerMockito.mockStatic(Request.class);
        PowerMockito.mockStatic(Response.class);
        PowerMockito.mockStatic(Content.class);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Content content = PowerMockito.mock(Content.class);
        when(Request.Put(anyString())).thenReturn(request);
        when(request.setHeader(anyString(), anyString())).thenReturn(request);
        when(request.bodyByteArray(Matchers.anyObject(), Matchers.anyObject())).thenReturn(request);
        PowerMockito.doReturn(response).when(request).execute();
        PowerMockito.doReturn(content).when(response).returnContent();
        when(content.toString()).thenReturn("{\"status\":true}");
        when(IdOSUtils.generateHandlerToken(this.credentials.get("servicePrivateKey"),
                this.credentials.get("servicePublicKey"), this.credentials.get("credentialPublicKey")))
                .thenReturn("token");
        JsonObject data = new JsonObject();
        data.addProperty("key", "value");
        assertEquals(json,
                WhiteboxImpl.invokeMethod(endpointMock, "sendRequest", "PUT", "https://idos.api.io/1.0", data));
    }

    @Test
    public void testSendRequestPatchMethod() throws Exception {
        AbstractEndpoint endpointMock = Mockito.mock(AbstractEndpoint.class, Mockito.CALLS_REAL_METHODS);
        endpointMock.setCredentials(this.credentials);
        PowerMockito.mockStatic(IdOSUtils.class);
        JsonObject json = new JsonObject();
        json.addProperty("status", true);
        endpointMock.setAuthType(IdOSAuthType.HANDLER);
        PowerMockito.mockStatic(Request.class);
        PowerMockito.mockStatic(Response.class);
        PowerMockito.mockStatic(Content.class);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Content content = PowerMockito.mock(Content.class);
        when(Request.Patch(anyString())).thenReturn(request);
        when(request.setHeader(anyString(), anyString())).thenReturn(request);
        when(request.bodyByteArray(Matchers.anyObject(), Matchers.anyObject())).thenReturn(request);
        PowerMockito.doReturn(response).when(request).execute();
        PowerMockito.doReturn(content).when(response).returnContent();
        when(content.toString()).thenReturn("{\"status\":true}");
        when(IdOSUtils.generateHandlerToken(this.credentials.get("servicePrivateKey"),
                this.credentials.get("servicePublicKey"), this.credentials.get("credentialPublicKey")))
                .thenReturn("token");
        JsonObject data = new JsonObject();
        data.addProperty("key", "value");
        assertEquals(json,
                WhiteboxImpl.invokeMethod(endpointMock, "sendRequest", "PATCH", "https://idos.api.io/1.0", data));
    }

    @Test
    public void testSendRequestDeleteMethod() throws Exception {
        AbstractEndpoint endpointMock = Mockito.mock(AbstractEndpoint.class, Mockito.CALLS_REAL_METHODS);
        endpointMock.setCredentials(this.credentials);
        PowerMockito.mockStatic(IdOSUtils.class);
        JsonObject json = new JsonObject();
        json.addProperty("status", true);
        endpointMock.setAuthType(IdOSAuthType.HANDLER);
        PowerMockito.mockStatic(Request.class);
        PowerMockito.mockStatic(Response.class);
        PowerMockito.mockStatic(Content.class);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Content content = Mockito.mock(Content.class);
        when(Request.Delete(anyString())).thenReturn(request);
        when(request.setHeader(anyString(), anyString())).thenReturn(request);
        PowerMockito.doReturn(response).when(request).execute();
        PowerMockito.doReturn(content).when(response).returnContent();
        when(content.toString()).thenReturn("{\"status\":true}");
        when(IdOSUtils.generateHandlerToken(this.credentials.get("servicePrivateKey"),
                this.credentials.get("servicePublicKey"), this.credentials.get("credentialPublicKey")))
                .thenReturn("token");
        JsonObject data = new JsonObject();
        data.addProperty("key", "value");
        assertEquals(json,
                WhiteboxImpl.invokeMethod(endpointMock, "sendRequest", "DELETE", "https://idos.api.io/1.0", null));
    }

    @Test
    public void testRequestFilters() throws Exception {
        AbstractEndpoint endpointMock = Mockito.mock(AbstractEndpoint.class);
        endpointMock.setCredentials(this.credentials);
        PowerMockito.mockStatic(IdOSUtils.class);
        endpointMock.setAuthType(IdOSAuthType.HANDLER);
        Filter filter = Filter.createFilter();
        filter.addFilterByKeyName("key", "value");
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject pagination = new JsonObject();
        pagination.addProperty("current_page", 0);
        pagination.addProperty("total", 1);
        json.addProperty("status", true);
        json.add("data", array);
        json.add("pagination", pagination);
        when(WhiteboxImpl.invokeMethod(endpointMock, "sendRequest", "GET", "https://idos.api.io/1.0", null))
                .thenReturn(json);
        JsonObject json2 = new JsonObject();
        pagination = new JsonObject();
        pagination.addProperty("current_page", 1);
        pagination.addProperty("total", 1);
        json.addProperty("status", true);
        json.add("data", array);
        json.add("pagination", pagination);
        when(WhiteboxImpl.invokeMethod(endpointMock, "sendRequest", "GET", "https://idos.api.io/1.0?key=value", null))
                .thenReturn(json2);
        endpointMock.request("GET", "https://idos.api.io/1.0", null, filter);
    }
}
