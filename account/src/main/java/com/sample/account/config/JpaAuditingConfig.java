package com.sample.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
//spring applciation 뜰때 autoScan이 되는 것
public class JpaAuditingConfig {

}
