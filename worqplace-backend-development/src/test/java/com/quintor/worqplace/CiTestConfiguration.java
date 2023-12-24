package com.quintor.worqplace;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

@Profile("ci")
@TestConfiguration
public class CiTestConfiguration {

	//TODO: Replace schema.sql by automatically generated database by hibernate on each run.

}