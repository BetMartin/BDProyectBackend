plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("javax.persistence:javax.persistence-api:2.2")
    runtimeOnly("mysql:mysql-connector-java:8.0.33")

    //Lombok
    compileOnly ("org.projectlombok:lombok:1.18.30")
    annotationProcessor ("org.projectlombok:lombok:1.18.30")
    testCompileOnly ("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor ("org.projectlombok:lombok:1.18.30")

    //Sawgger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    //PDFs
    implementation("com.itextpdf:itext7-core:7.2.5")

    //Excel
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    //Map struct
    implementation ("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor ("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor ("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    //MercadoPago
    implementation("com.mercadopago:sdk-java:2.1.24")

    // Spring Security
    implementation ("org.springframework.boot:spring-boot-starter-security")

    // OAuth2 para autenticación social
    implementation ("org.springframework.security:spring-security-oauth2-client")
    implementation ("org.springframework.security:spring-security-oauth2-jose")

    // Para envío de correos electrónicos
    implementation ("org.springframework.boot:spring-boot-starter-mail")

    // Thymeleaf para plantillas de email (opcional)
    implementation ("org.springframework.boot:spring-boot-starter-thymeleaf")
}

tasks.test {
    useJUnitPlatform()
}

