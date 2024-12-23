plugins {
	java
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.ring-cam-recorder"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter") {
		exclude(module = "spring-boot-starter-logging")
	}

// Lombok for reducing boilerplate code
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Process execution library
	implementation("org.zeroturnaround:zt-exec:1.12")

	// ActiveMQ Support
	implementation("org.springframework.boot:spring-boot-starter-activemq"){
		exclude(module = "spring-boot-starter-logging")
	}
	// https://mvnrepository.com/artifact/org.apache.activemq/activemq-client
	//implementation("org.apache.activemq:activemq-client:6.1.4")
	//implementation("org.springframework.boot:spring-boot-starter-logging")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")

	testImplementation("org.springframework.boot:spring-boot-starter-test"){
		exclude(module = "spring-boot-starter-logging")
	}
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
