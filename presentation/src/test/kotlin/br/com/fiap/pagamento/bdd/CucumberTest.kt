package br.com.fiap.pagamento.bdd

import io.cucumber.junit.Cucumber
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite
import org.junit.runner.RunWith

@Suite
@IncludeEngines("cucumber")
@RunWith(Cucumber::class)
@SelectClasspathResource("features")
class CucumberTest
