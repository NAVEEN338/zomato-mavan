package zomato

import io.vertx.core.Future
import io.vertx.groovy.core.eventbus.Message
import io.vertx.lang.groovy.GroovyVerticle

/**
 * A Simple {@link GroovyVerticle} which reponds to ping requests with 'pong'
 */
class PingVerticle extends GroovyVerticle {

    void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer('ping.endpoint', this.&handlePingMessage)
        startFuture.complete()
    }

    void handlePingMessage(Message<String> msg) {
        msg.reply('pong')
    }
}
