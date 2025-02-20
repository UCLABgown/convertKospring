plugins {
	kotlin("jvm") version "1.9.25" // 추가
	kotlin("plugin.spring") version "1.9.25" // 추가
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "team16"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Data JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// Validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Spring Web
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Spring DevTools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// H2 Database
	runtimeOnly("com.h2database:h2")

	// Spring Boot Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// JUnit Platform Launcher
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Springdoc OpenAPI for Swagger UI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.1")

	// Spring security
	implementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.security:spring-security-test")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
	implementation(kotlin("stdlib-jdk8"))
	// 코프링
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// 테스트
	// 생략
	implementation("org.hibernate.validator:hibernate-validator")
	implementation("jakarta.validation:jakarta.validation-api")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
