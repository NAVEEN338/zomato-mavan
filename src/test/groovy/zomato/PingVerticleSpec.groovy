package zomato

import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.eventbus.Message
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

/**
 * Tests for the {@link PingVerticle} using SpockFramework and {@link AsyncConditions}
 */
class PingVerticleSpec extends Specification {

    def "Test deploy verticle"() {
        given: "A Vert.x instance"
            def vertx = Vertx.vertx()
            def cond = new AsyncConditions(1)

        when: "We deploy the verticle"
            vertx.deployVerticle("groovy:zomato.PingVerticle", {res ->
                cond.evaluate {
                    assert res.succeeded()
                }
            })

        then: "Expect the condition to succeed"
            noExceptionThrown()
    }

    def "Test pong response"() {
        given: "An instance of the PingVerticle and appropriate mocks"
            def underTest = new PingVerticle()
            def msg = Mock(Message)

        when: "The method is called with a Message"
            underTest.handlePingMessage(msg)

        then:
            1 * msg.reply('pong')
    }

    def "Test complete integration with Vert.x EventBus"() {
        given: "A deployed instance of the PingVerticle"
            def vertx = Vertx.vertx()
            def cond = new AsyncConditions(1)
            vertx.deployVerticle('groovy:zomato.PingVerticle')
            def result = ''

        when: "A message is sent to the correct eventbus address"
            vertx.eventBus().send('ping.endpoint', 'Test Message', { res ->
                cond.evaluate {
                    assert res.succeeded()
                    assert res.result() == 'pong'
                }
            })

        then:
            noExceptionThrown()
    }
}
