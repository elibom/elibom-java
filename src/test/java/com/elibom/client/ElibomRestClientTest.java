package com.elibom.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public class ElibomRestClientTest {

    private WireMockServer wireMockServer;

    @BeforeMethod
    public void setUp() throws Exception {
        wireMockServer = new WireMockServer(4005); //No-args constructor will start on port 8080, no HTTPS
        wireMockServer.start();

        WireMock.configureFor("localhost", 4005);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        wireMockServer.stop();
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldFailWithNullUsername() throws Exception {
        new ElibomRestClient(null, "test");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldFailWithNullPassword() throws Exception {
        new ElibomRestClient("t@u.com", null);
    }

    @Test
    public void shouldSendMessage() throws Exception {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody("{ \"deliveryToken\": \"12345\" }")));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        String deliveryToken = elibom.sendMessage("573002111111,583242111111", "this is a test");
        Assert.assertEquals(deliveryToken, "12345");

        verify(postRequestedFor(urlEqualTo("/messages"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalTo("{\"to\":\"573002111111,583242111111\",\"text\":\"this is a test\"}")));
    }


    @Test
    public void shouldSendMessageWithCampingId() throws Exception {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody("{ \"deliveryToken\": \"12345\" }")));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        String deliveryToken = elibom.sendMessage("573002111111,583242111111", "this is a test","Campaing_1");
        Assert.assertEquals(deliveryToken, "12345");

        verify(postRequestedFor(urlEqualTo("/messages"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalTo("{\"campaign\":\"Campaing_1\",\"to\":\"573002111111,583242111111\",\"text\":\"this is a test\"}")));
    }

    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldFailSendMessageWithLongText() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.sendMessage("573002111111,583242111111", longText());
    }

    private String longText() {
        String ret = "";
        for (int i=0; i < 161; i++) {
            ret += "a";
        }

        return ret;
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldFailSendMessageWithMissingDestinations() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.sendMessage(null, "this is a test");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldFailSendMessageWithMissingText() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.sendMessage("573002111111", null);
    }

    @Test
    public void shouldScheduleMessageWithCampingId() throws Exception {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody("{ \"scheduleId\": \"32\" }")));

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2014-02-18 10:00");

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        long scheduleId = elibom.scheduleMessage("573002111111,583242111111", "this is a test", date, "Campaing_1");
        Assert.assertEquals(scheduleId, 32);

        verify(postRequestedFor(urlEqualTo("/messages"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalTo("{\"scheduleDate\":\"2014-02-18 10:00\",\"campaign\":\"Campaing_1\",\"to\":\"573002111111,583242111111\",\"text\":\"this is a test\"}")));
    }

    @Test
    public void shouldScheduleMessage() throws Exception {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody("{ \"scheduleId\": \"32\" }")));

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2014-02-18 10:00");

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        long scheduleId = elibom.scheduleMessage("573002111111,583242111111", "this is a test", date);
        Assert.assertEquals(scheduleId, 32);

        verify(postRequestedFor(urlEqualTo("/messages"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalTo("{\"scheduleDate\":\"2014-02-18 10:00\",\"to\":\"573002111111,583242111111\",\"text\":\"this is a test\"}")));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shoudFailScheduleMessageLongText() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.scheduleMessage("573002111111,583242111111", longText(), new Date());
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldFailScheduleMessageMissingDestinations() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.scheduleMessage(null, "this is a test", new Date());
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldFailScheduleMessageMissingText() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.scheduleMessage("573002111111", null, new Date());
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldFailScheduleMessageMissingDate() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.scheduleMessage("573002111111", "this is a test", null);
    }

    @Test
    public void shouldShowDelivery() throws Exception {
        JSONObject jsonDelivery = createFakeDelivery();
        stubFor(get(urlEqualTo("/messages/12345"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody(jsonDelivery.toString())));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        Delivery delivery = elibom.getDelivery("12345");

        Assert.assertNotNull(delivery);
        Assert.assertEquals(delivery.getId(), "12345");
        Assert.assertEquals(delivery.getStatus(), "finished");
        Assert.assertEquals(delivery.getNumSent(), 1);
        Assert.assertEquals(delivery.getNumFailed(), 0);
        Assert.assertNotNull(delivery.getMessages());

        List<Message> messages = delivery.getMessages();
        Assert.assertNotNull(messages);

        Message message = messages.get(0);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getId(), 171851);
        Assert.assertEquals(message.getUserId(), 2);
        Assert.assertEquals(message.getTo(), "573002175604");
        Assert.assertEquals(message.getOperator(), "Tigo (Colombia)");
        Assert.assertEquals(message.getText(), "this is a test");
        Assert.assertEquals(message.getStatus(), "sent");
        Assert.assertEquals(message.getStatusDetail(), "sent");
        Assert.assertEquals(message.getCredits(), new BigDecimal("1"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals(message.getCreatedAt(), sdf.parse("2013-07-24 15:05:34"));
        Assert.assertEquals(message.getSentAt(), sdf.parse("2013-07-24 15:05:34"));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shoudlFailShowDeliveryWithNullDeliveryId() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.getDelivery(null);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shoudlFailShowDeliveryWithEmptyDeliveryId() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.getDelivery("");
    }

    @Test
    public void shouldListScheduledMessages() throws Exception {
        JSONObject jsonSchedule = createFakeScheduler();
        JSONArray jsonSchedules = new JSONArray().put(jsonSchedule);

        stubFor(get(urlEqualTo("/schedules/scheduled"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody(jsonSchedules.toString())));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        List<Schedule> schedules = elibom.getScheduledMessages();
        Assert.assertNotNull(schedules);

        Schedule schedule = schedules.get(0);
        Assert.assertNotNull(schedule);
        Assert.assertEquals(schedule.getId(), 32);
        Assert.assertEquals(schedule.getUserId(), 45);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals(schedule.getScheduledAt(), sdf.parse("2014-05-23 10:23:00"));
        Assert.assertEquals(schedule.getCreatedAt(), sdf.parse("2012-09-23 22:00:00"));

        Assert.assertTrue(schedule.isFile());
        Assert.assertEquals(schedule.getFileName(), "test.xls");
        Assert.assertFalse(schedule.isFileHasText());
        Assert.assertEquals(schedule.getText(), "this is a test");
    }

    @Test
    public void shouldShowScheduledMessage() throws Exception {
        JSONObject jsonSchedule = createFakeScheduler();

        stubFor(get(urlEqualTo("/schedules/32"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody(jsonSchedule.toString())));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        Schedule schedule = elibom.getScheduledMessage(32);

        Assert.assertNotNull(schedule);
        Assert.assertEquals(schedule.getId(), 32);
        Assert.assertEquals(schedule.getUserId(), 45);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals(schedule.getScheduledAt(), sdf.parse("2014-05-23 10:23:00"));
        Assert.assertEquals(schedule.getCreatedAt(), sdf.parse("2012-09-23 22:00:00"));

        Assert.assertEquals(schedule.getStatus(), "executed");
        Assert.assertTrue(schedule.isFile());
        Assert.assertEquals(schedule.getFileName(), "test.xls");
        Assert.assertFalse(schedule.isFileHasText());
        Assert.assertEquals(schedule.getText(), "this is a test");
    }

    @Test
    public void shouldCancelSchedule() throws Exception {
        stubFor(delete(urlEqualTo("/schedules/32"))
                .willReturn(aResponse()
                    .withStatus(200)));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.unschedule(32);

        verify(deleteRequestedFor(urlEqualTo("/schedules/32")));
    }

    @Test(expectedExceptions=HttpServerException.class)
    public void shouldFailToDeleteScheduleIfNotFound() throws Exception {
        stubFor(delete(urlEqualTo("/schedules/32"))
                .willReturn(aResponse()
                    .withStatus(404)));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.unschedule(32);
    }

    @Test
    public void shouldListUsers() throws Exception {
        JSONObject jsonUser = createFakeUser();
        JSONArray jsonUsers = new JSONArray().put(jsonUser);

        stubFor(get(urlEqualTo("/users"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody(jsonUsers.toString())));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        List<User> users = elibom.getUsers();
        Assert.assertNotNull(users);
        Assert.assertEquals(users.size(), 1);

        User user = users.get(0);
        Assert.assertEquals(user.getId(), 1);
        Assert.assertEquals(user.getName(), "Usuario 1");
        Assert.assertEquals(user.getEmail(), "usuario1@tudominio.com");
        Assert.assertEquals(user.getStatus(), "active");
    }

    @Test
    public void shouldShowUser() throws Exception {
        JSONObject jsonUser = createFakeUser();

        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody(jsonUser.toString())));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        User user = elibom.getUser(1);
        Assert.assertEquals(user.getId(), 1);
        Assert.assertEquals(user.getName(), "Usuario 1");
        Assert.assertEquals(user.getEmail(), "usuario1@tudominio.com");
        Assert.assertEquals(user.getStatus(), "active");
    }

    @Test
    public void shouldShowAccount() throws Exception {
        JSONObject jsonAccount = new JSONObject()
                .put("name", "Nombre Empresa")
                .put("credits", 10)
                .put("owner", new JSONObject().put("id", 1).put("url", "https://www.elibom.com/users/1"));

        stubFor(get(urlEqualTo("/account"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody(jsonAccount.toString())));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        Account account = elibom.getAccount();
        Assert.assertNotNull(account);
        Assert.assertEquals(account.getName(), "Nombre Empresa");
        Assert.assertEquals(account.getCredits(), new BigDecimal("10"));
        Assert.assertEquals(account.getOwnerId(), 1);
    }
    
    @Test
    public void shouldShowLastMessages() throws Exception {
        JSONArray jsonMessages = createFakeMessagesList();

        JSONObject jsonLastMessages = new JSONObject()
        .put("messages", jsonMessages);
        
        stubFor(get(urlEqualTo("/messages?status=sent&perPage=1&user=t@u.com"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody(jsonLastMessages.toString())));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        List<Message>messages = elibom.getLastMessages(1);
        Assert.assertNotNull(messages);

        Message message = messages.get(0);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getId(), 171851);
        Assert.assertEquals(message.getUserId(), 2);
        Assert.assertEquals(message.getTo(), "573002175604");
        Assert.assertEquals(message.getOperator(), "Tigo (Colombia)");
        Assert.assertEquals(message.getText(), "this is a test");
        Assert.assertEquals(message.getStatus(), "sent");
        Assert.assertEquals(message.getStatusDetail(), "sent");
        Assert.assertEquals(message.getCredits(), new BigDecimal("1"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals(message.getCreatedAt(), sdf.parse("2013-07-24 15:05:34"));
        Assert.assertEquals(message.getSentAt(), sdf.parse("2013-07-24 15:05:34"));
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shoudlFailShowLastMessagesWithNegativeNumMessages() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.getLastMessages(-1);
    }

    @Test
    public void shouldShowLastMessagesBetweenDates() throws Exception {
        JSONArray jsonMessages = createFakeMessagesList();

        JSONObject jsonLastMessages = new JSONObject()
        .put("messages", jsonMessages);
        
        stubFor(get(urlEqualTo("/messages?status=sent&perPage=1&user=t@u.com&startDate=23-07-2013&endDate=24-07-2013"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json; charset=UTF-8")
                    .withBody(jsonLastMessages.toString())));

        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        
        List<Message>messages = elibom.getLastMessages(1,sdf1.parse("23-07-2013"),sdf1.parse("24-07-2013"));
        Assert.assertNotNull(messages);

        Message message = messages.get(0);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getId(), 171851);
        Assert.assertEquals(message.getUserId(), 2);
        Assert.assertEquals(message.getTo(), "573002175604");
        Assert.assertEquals(message.getOperator(), "Tigo (Colombia)");
        Assert.assertEquals(message.getText(), "this is a test");
        Assert.assertEquals(message.getStatus(), "sent");
        Assert.assertEquals(message.getStatusDetail(), "sent");
        Assert.assertEquals(message.getCredits(), new BigDecimal("1"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals(message.getCreatedAt(), sdf.parse("2013-07-24 15:05:34"));
        Assert.assertEquals(message.getSentAt(), sdf.parse("2013-07-24 15:05:34"));
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shoudlFailShowLastMessagesBetweenDatesWithNegativeNumMessages() throws Exception {
        ElibomRestClient elibom = new ElibomRestClient("t@u.com", "test", "http://localhost:4005");
        elibom.getLastMessages(-1);
    }

    
    
    private JSONObject createFakeMessage() throws Exception
    {
        JSONObject jsonMessage = new JSONObject()
        .put("id", 171851)
        .put("user", new JSONObject()
                .put("id", 2)
                .put("url", "https://www.elibom.com:9090/users/2"))
        .put("to", "573002175604")
        .put("operator", "Tigo (Colombia)")
        .put("text", "this is a test")
        .put("status", "sent")
        .put("statusDetail", "sent")
        .put("credits", 1)
        .put("from", "3542")
        .put("createdAt", "2013-07-24 15:05:34")
        .put("sentAt", "2013-07-24 15:05:34");
        
        return jsonMessage;
    }
    
    private JSONArray createFakeMessagesList() throws Exception {
        JSONObject jsonMessage = createFakeMessage();
        JSONArray jsonMessages = new JSONArray().put(jsonMessage);
        return jsonMessages;
    }

    private JSONObject createFakeDelivery() throws Exception {
        JSONArray jsonMessages = createFakeMessagesList();
        JSONObject jsonDelivery = new JSONObject()
        .put("deliveryId", "12345")
        .put("status", "finished")
        .put("numSent", 1)
        .put("numFailed", 0)
        .put("messages", jsonMessages);

        return jsonDelivery;
    }
    
    private JSONObject createFakeScheduler() throws Exception{
        JSONObject jsonSchedule = new JSONObject()
        .put("id", 32)
        .put("user", new JSONObject().put("id", 45).put("url", "https://www.elibom.com/users/45"))
        .put("scheduledTime", "2014-05-23 10:23:00")
        .put("creationTime", "2012-09-23 22:00:00")
        .put("status", "executed")
        .put("isFile", true)
        .put("fileName", "test.xls")
        .put("fileHasText", false)
        .put("text", "this is a test");
        
        return jsonSchedule;
    }
    
    private JSONObject createFakeUser() throws Exception{
        JSONObject jsonUser = new JSONObject()
        .put("id", 1)
        .put("name", "Usuario 1")
        .put("email", "usuario1@tudominio.com")
        .put("status", "active");

        return jsonUser;
    }

}
