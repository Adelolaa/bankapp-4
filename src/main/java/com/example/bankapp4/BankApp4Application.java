package com.example.bankapp4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@EnableSwagger2
@SpringBootApplication
public class BankApp4Application {

	public static void main(String[] args) {
		SpringApplication.run(BankApp4Application.class, args);
	}



	@Bean
	public Docket SwaggerConfiguration() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.securityContexts(Arrays.asList(securityContext()))
				.select()
//				.apis(RequestHandlerSelectors.any())
//				.paths(PathSelectors.any())
				.build();


	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Hotel Booking API",
				"<h2 style='margin:2px'> Author: Ofunrein Iyaghe.</h2> " +
						"<article> <h2>Description </h2>" +
						"The Api endpoints help with hotel bookings and reservations, management by third party hotel vendors in various cities in Nigeria<br/>" +
						"<h2>Demo Usage</h2> " +
						" Role Admin: Two user with credential (email:yage@gmail.com,password:Yage@123456789)<br/>" +
						"Role Client: The user  can register as new client, and make reservation in any of the hotels of his/her choice<br/>" +
						"<h1>Authorization Header setting: Bearer +token</h1> " +
						"<article>.<br/>",
				"1.0",
				"Free to use",
				new springfox.documentation.service.Contact("Ofunrein Iyaghe", "https://github.com/iyage", "yahg.concept@gmail.com"),
				"API License",
				"https://github.com/iyage",
				Collections.emptyList());
	}
	private SecurityContext securityContext(){
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}

	private List<SecurityReference> defaultAuth(){
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
	}

}
