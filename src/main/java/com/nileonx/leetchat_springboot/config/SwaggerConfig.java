package com.nileonx.leetchat_springboot.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))  //添加ApiOperiation注解的被扫描
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * Compatibility fix for Springfox 2.7.0 on Spring Boot 2.6+ / 2.7+.
     * Filter out handler mappings with non-null PatternParser to avoid NPE in documentation bootstrap.
     */
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private void customizeSpringfoxHandlerMappings(List<Object> mappings) {
                List<Object> copy = new ArrayList<>();
                for (Object mapping : mappings) {
                    try {
                        Field parserField = ReflectionUtils.findField(mapping.getClass(), "patternParser");
                        if (parserField == null) {
                            copy.add(mapping);
                            continue;
                        }
                        parserField.setAccessible(true);
                        Object parser = parserField.get(mapping);
                        if (parser == null) {
                            copy.add(mapping);
                        }
                    } catch (Exception e) {
                        copy.add(mapping);
                    }
                }
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<Object> getHandlerMappings(Object bean) {
                Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                if (field == null) {
                    return new ArrayList<>();
                }
                field.setAccessible(true);
                try {
                    return (List<Object>) field.get(bean);
                } catch (IllegalAccessException e) {
                    return new ArrayList<>();
                }
            }
        };
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("LeetChat的API文档").description("")
                .version("1.0").build();
    }
}
