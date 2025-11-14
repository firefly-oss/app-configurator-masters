package com.firefly.app.configurator.web.controllers;

import com.firefly.common.application.config.DomainPassthrough;
import org.springframework.stereotype.Component;

@Component
@DomainPassthrough(path = "/api/v1/configuration/init", target = "${endpoints.domain.configuration}")
public class ConfiguratorController {

}
