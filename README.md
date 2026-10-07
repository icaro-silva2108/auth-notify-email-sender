# Email Sender

Microservice responsible for asynchronously processing user-related events and sending emails.

The `Email Sender` is part of the [`Auth Notify API`](https://github.com/icaro-silva2108/auth-notify-api) architecture. 
It consumes events published to RabbitMQ, processes the received data, and sends emails through Gmail SMTP.

This separation allows the main API to publish an event and continue its execution without waiting for the email to be sent.

---

## 🛠️Technologies

- Java 21
- Spring Boot
- Spring AMQP
- RabbitMQ
- Thymeleaf
- JavaMailSender
- Gmail SMTP
- Maven

---

## 🏗️Architecture

```text
 Auth Notify API
       │
       │ Publishes event
       ▼
    RabbitMQ
       │
       │ Exchange + Routing Key
       ▼
  email-queue
       │
       │ Consumer
       ▼
  Email Sender
       │
       │ JavaMailSender
       ▼
  Gmail SMTP
       │
       ▼
     Email
```

RabbitMQ acts as an intermediary between the main API and the email service, enabling asynchronous and decoupled processing.

---

## 💡How It Works

When a user-related operation occurs in the **Auth Notify API**, an event can be published to RabbitMQ.

Example event:

```text
user.created
```

The API publishes the event using a routing key:

```java
rabbitTemplate.convertAndSend(
    "user-events-exchange",
    "user.created",
    payload
);
```

The `Email Sender` consumes messages from the queue:

```java
@RabbitListener(queues = "email-queue")
public void listener(@Payload UserEventMessageDTO message) throws MessagingException {

     switch(message.type()) {
         case USER_CREATED ->  {
             UserEventDTO payload = objectMapper.convertValue(message.payload(), UserEventDTO.class);
             emailService.sendWelcomeEmail(payload);
         }
    }
}
```

After successfully processing a message, the consumer acknowledges it with an `ACK`.

---

## 🐇RabbitMQ

The service uses a `Direct Exchange` with the following structure:

```text
Exchange:
user-events-exchange

Queue:
email-queue

Routing Key:
user.created
```

The architecture can support additional user-related events, such as:  

- `user.created`  
- `user.updated`  
- `user.deleted`  
- `user.role.changed`  


A single queue can receive multiple event types through different bindings:

```text
                 user-events-exchange
                         │
          ┌──────────────┼──────────────┐
          │              │              │
     user.created   user.updated   user.deleted
          │              │              │
          └──────────────┼──────────────┘
                         ▼
                    email-queue
                         │
                         ▼
                    Email Sender
```

Additional queues can be added in the future if different services need to consume the same events.

---

## 📧Email Sending

Emails are sent using:

- `JavaMailSender`
- `Gmail SMTP`
- `Thymeleaf`

Example:

```java
MimeMessage mimeMessage = mailSender.createMimeMessage();

MimeMessageHelper mimeHelper =
        new MimeMessageHelper(mimeMessage, true, "UTF-8");

mimeHelper.setTo(event.userEmail());
mimeHelper.setSubject("Welcome to Auth Notify!");
mimeHelper.setText(htmlContent, true);

mailSender.send(mimeMessage);
```

Email content is generated from `HTML` templates using `Thymeleaf`:

```java
Context context = new Context();
context.setVariable("username", event.name());

String htmlContent =
        templateEngine.process("welcomeMessage", context);
```

---

## 📋Service Responsibilities

| Service           | Responsibility                            |
|-------------------| ----------------------------------------- |
| `Auth Notify API` | Users, authentication, and business rules |
| `RabbitMQ`        | Message transport and temporary storage   |
| `Email Sender`    | Event consumption and email delivery      |
| `Gmail SMTP`      | Email delivery infrastructure             |

The `Email Sender` is not responsible for authentication or user management. Its responsibility is limited to consuming events and sending emails.

---

## ⛓️‍💥Resilience

If the `Email Sender` is unavailable, messages remain in the `RabbitMQ` queue until a consumer becomes available again.

The project also explores RabbitMQ concepts such as:

- `ACK` and `NACK`
- Message requeueing( *Still in development* )
- Retry strategies( *Still in development* )
- TTL( *Still int development* )
- Dead Letter Exchange( *Still in development* )
- Dead Letter Queue( *Still in development* )

---

## 📑Project

The `Email Sender` is part of the [`Auth Notify API`](https://github.com/icaro-silva2108/auth-notify-api) study project, focused on:

**Java, Spring Boot, Spring Security, OAuth2/OIDC, JWT, RabbitMQ, event-driven architecture, and microservices.**
