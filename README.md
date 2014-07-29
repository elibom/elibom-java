Elibom Java API Client
===========

A java client of the Elibom REST API. [The full API reference is here](http://www.elibom.com/developers/reference).


## Getting Started

1\. Include the library

Add the dependency to your projects pom.xml file:

```xml
<dependency>
  <groupId>com.elibom</groupId>
  <artifactId>elibom-java</artifactId>
  <version>0.2.0</version>
</dependency>
```

Or [download the JAR](http://central.maven.org/maven2/com/elibom/elibom-java/0.2.0/elibom-java-0.2.0.jar), [its dependencies](http://central.maven.org/maven2/org/json/json/20140107/json-20140107.jar) and include it in your project.

2\. Create an `ElibomRestClient` object passing your credentials:

```java
System.setProperty("jsse.enableSNIExtension", "false"); // if you are using Java 7
ElibomRestClient elibom = new ElibomRestClient("your_email", "your_api_password");
```

*Note*: You can find your api password at http://www.elibom.com/api-password (make sure you are logged in).

You are now ready to start calling the API methods!

## API methods

* [Send SMS](#send-sms)
* [Schedule SMS](#schedule-sms)
* [Show Delivery](#show-delivery)
* [List Scheduled SMS Messages](#list-scheduled-sms-messages)
* [Show Scheduled SMS Message](#show-scheduled-sms-message)
* [Cancel Scheduled SMS Message](#cancel-scheduled-sms-message)
* [List Users](#list-users)
* [Show User](#show-user)
* [Show Account](#show-account)

### Send SMS
```java
String deliveryId = elibom.sendMessage("51965876567, 573002111111", "This is a test");
```

### Schedule SMS 
```java
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
long scheduleId = elibom.scheduleMessage("51965876567, 573002111111", "This is a test", sdf.parse("2014-08-24 10:00"));
```

### Show Delivery
```java
Delivery delivery = elibom.getDelivery("<delivery_token>")
System.out.println(delivery);
```

### List Scheduled SMS Messages
```java
List<Schedule> schedules = elibom.getScheduledMessages();
for (Schedule schedule : schedules) {
  System.out.println(schedule);
}
```

### Show Scheduled SMS Message
```java
Schedule schedule = elibom.getScheduledMessage(<schedule_id>)
System.out.println(schedule);
```

### Cancel Scheduled SMS Message
```java
elibom.unschedule(<schedule_id>)
```

### List Users
```java
List<User> users = elibom.getUsers();
for (User user : users) {
  System.out.println(user);
}
```

### Show User
```java
User user = elibom.getUser(<user_id>)
System.out.println(user);
```

### Show Account
```java
Account account = elibom.getAccount();
System.out.println(account);
