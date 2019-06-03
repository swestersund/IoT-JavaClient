package com.iotticket.api.v1;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.iotticket.api.v1.exception.IoTServerCommunicationException;
import com.iotticket.api.v1.exception.ValidAPIParamException;
import com.iotticket.api.v1.model.Device;
import com.iotticket.api.v1.model.Device.DeviceDetails;
import com.iotticket.api.v1.model.DeviceAttribute;
import com.iotticket.api.v1.util.ResourceFileUtils;


public class IOTAPIClientDeviceTest{
	
	
    private static final int WIREMOCK_PORT = 9999; 
    private static final String TEST_BASE_URL = "http://localhost:" + String.valueOf(WIREMOCK_PORT) + "/";
	
    private static final String TEST_DEVICES_RESOURCE = "/devices/";
    
    private static final String TEST_USERNAME = "user1";
	private static final String TEST_PASSWORD = "pw1";
	
	private static final String TEST_DEVICE_NAME = "Test device";
	private static final String TEST_DEVICE_MANUFACTURER = "Test Oy";
	private static final String TEST_DEVICE_TYPE = "PC";
	private static final String TEST_DEVICE_DESCRIPTION = "The main server";
	
	private static final String TEST_DEVICE_ID = "153ffceb982745e8b1e8abacf9c217f3";
	    	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);
	
	@Test
	public void testRegisterDevice() throws ValidAPIParamException, IOException, URISyntaxException{
		
		IOTAPIClient iotApiClient = new IOTAPIClient(TEST_BASE_URL, TEST_USERNAME, TEST_PASSWORD);
		
		DeviceAttribute attribute1 = new DeviceAttribute("Application Version", "0.2.3");
		DeviceAttribute attribute2 = new DeviceAttribute("Chip", "TestCore");
		
		Collection<DeviceAttribute> deviceAttributes = new ArrayList<DeviceAttribute>();
		deviceAttributes.add(attribute1);
		deviceAttributes.add(attribute2);
		
		Device device = new Device();
		device.setName(TEST_DEVICE_NAME);
		device.setManufacturer(TEST_DEVICE_MANUFACTURER);
		device.setType(TEST_DEVICE_TYPE);
		device.setDescription(TEST_DEVICE_DESCRIPTION);
		device.setAttributes(deviceAttributes);
		
		stubFor(post(
				urlEqualTo(TEST_DEVICES_RESOURCE))
				.withHeader("Accept", equalTo("application/json"))
				.withRequestBody(equalToJson(
						ResourceFileUtils.resourceFileToString("testRegisterDeviceRequestBody.json", getClass())))
				.withBasicAuth(TEST_USERNAME, TEST_PASSWORD)
				.willReturn(
						aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json").
						withBody(ResourceFileUtils.resourceFileToString("testRegisterDeviceResponseBody.json", getClass())))
				);
		
		DeviceDetails result = iotApiClient.registerDevice(device);
		
		verify(postRequestedFor(
				urlEqualTo(TEST_DEVICES_RESOURCE))
				.withHeader("Accept", equalTo("application/json"))
				.withRequestBody(equalToJson(
						ResourceFileUtils.resourceFileToString("testRegisterDeviceRequestBody.json", getClass())))
				.withBasicAuth(new BasicCredentials(TEST_USERNAME, TEST_PASSWORD)));
		
		assertEquals(result.getName(), TEST_DEVICE_NAME);
		assertEquals(result.getManufacturer(), TEST_DEVICE_MANUFACTURER);
		assertEquals(result.getDeviceId(), TEST_DEVICE_ID);
	}
	
	@Test(expected = IoTServerCommunicationException.class)
	public void testRegisterDevice_invalidCredentials() throws ValidAPIParamException, IOException, URISyntaxException{
		
		String wrongPassword = "pw2";
		
		IOTAPIClient iotApiClient = new IOTAPIClient(TEST_BASE_URL, TEST_USERNAME, wrongPassword);
		
		DeviceAttribute attribute1 = new DeviceAttribute("Application Version", "0.2.3");
		DeviceAttribute attribute2 = new DeviceAttribute("Chip", "TestCore");
		
		Collection<DeviceAttribute> deviceAttributes = new ArrayList<DeviceAttribute>();
		deviceAttributes.add(attribute1);
		deviceAttributes.add(attribute2);
		
		Device device = new Device();
		device.setName(TEST_DEVICE_NAME);
		device.setManufacturer(TEST_DEVICE_MANUFACTURER);
		device.setType(TEST_DEVICE_TYPE);
		device.setDescription(TEST_DEVICE_DESCRIPTION);
		device.setAttributes(deviceAttributes);
		
		stubFor(post(
				urlEqualTo(TEST_DEVICES_RESOURCE))
				.withHeader("Accept", equalTo("application/json"))
				.withRequestBody(equalToJson(
						ResourceFileUtils.resourceFileToString("testRegisterDeviceRequestBody.json", getClass())))
				.withBasicAuth(TEST_USERNAME, wrongPassword)
				.willReturn(
						aResponse()
						.withStatus(401)
						.withBody(ResourceFileUtils.resourceFileToString("testRegisterDevice_invalidCredentialsResponseBody.json", getClass())))
				);
		
		iotApiClient.registerDevice(device);
	}
	
}
