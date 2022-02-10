package zomato

import io.vertx.groovy.core.Vertx
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

/**
 * Tests for the {@link Main} verticle using SpockFramework and {@link AsyncConditions}
 */
class MainSpec extends Specification {

    def "Test deploy web server"() {
        given: "A Vert.x instance"
            def vertx = Vertx.vertx()
            def cond = new AsyncConditions(1)

        when: "We deploy the verticle"
            vertx.deployVerticle("groovy:zomato.Main", {res ->
                cond.evaluate {
                    assert res.succeeded()
                }
            })

        then: "Expect the condition to succeed"
            noExceptionThrown()
    }
}
