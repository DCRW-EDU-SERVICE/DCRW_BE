package com.example.DCRW.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo(){
        return new Info()
                .title("API TEST") // API 제목
                .description("다채로와 swagger") // API 설명
                .version("1.0.0");
    }

    // get method만 모아 보기
//    @Bean
//    OpenApiCustomizer getEndpointsCustomeizer(){
//        return openApi -> {
//            Paths paths = new Paths();
//
//            // 원래 기본적으로 openApi가 가지고 있는 path(PathItem)들 중 우리가 원하는 path(PathItem)만 paths에 담는다.
//            openApi.getPaths().forEach( (path, pathItem) -> {
//                PathItem newPathItem = new PathItem();
//
//                // get에 해당하는 PathItem 확인
//                if(pathItem.getGet() != null){
//                    newPathItem.setGet(pathItem.getGet());
//                    paths.addPathItem(path, newPathItem);
//                }
//            });
//            openApi.setPaths(paths);
//        };
//    }
}
