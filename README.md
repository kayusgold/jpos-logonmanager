# Spring Boot Application with LogonManager Integration

This is a sample Spring Boot application that demonstrates the integration of LogonManager to send ISO 8583 network messages at intervals.

Note that this README file was generated by AI and I did not edit it.

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/kayusgold/jpos-logonmanager.git
   ```

2. Open the project in your preferred IDE.

3. Configure the LogonManager properties in the `application.properties` file:
   ```properties
   # LogonManager configuration
   logon-manager.enabled=true
   logon-manager.interval=5000
   logon-manager.source-address=1234567890
   logon-manager.destination-address=0987654321
   logon-manager.username=myUsername
   logon-manager.password=myPassword
   ```

   Adjust the properties according to your specific needs. Set `logon-manager.enabled` to `true` to enable LogonManager.

4. Implement your custom ISO 8583 message handling logic in the `IsoMessageHandler` class. Customize the `handleIsoMessage` method to process the received ISO messages.

5. Build the application using Maven:
   ```bash
   mvn clean install
   ```

6. Run the application:
   ```bash
   mvn spring-boot:run
   ```

   The LogonManager will start sending ISO 8583 network messages at the specified interval configured in `logon-manager.interval`.

7. Monitor the logs and observe the ISO message sending and receiving.

## Customization

You can customize the application and LogonManager behavior according to your requirements:

- Adjust the LogonManager properties in the `application.properties` file to configure the destination addresses, credentials, and interval.
- Modify the `IsoMessageHandler` class to handle ISO 8583 messages according to your application's needs.
- Extend the application by adding additional features and logic as necessary.

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [JPOS Documentation](http://jpos.org/doc/)
- [ISO 8583 Specification](https://en.wikipedia.org/wiki/ISO_8583)

Feel free to modify the README file based on your specific project structure, conventions, and additional instructions you want to include.