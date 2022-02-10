package zomato

import io.vertx.core.Future
import io.vertx.core.logging.Logger
import io.vertx.groovy.core.CompositeFuture
import io.vertx.lang.groovy.GroovyVerticle

/**
 * An example Main verticle
 */
class Main extends GroovyVerticle {

    private Logger LOG

    void start(Future<Void> startFuture) throws Exception {

        // Configure Vert.x to use Slf4j in order to log using BunyanLogger
        System.setProperty('vertx.logger-delegate-factory-class-name', 'io.vertx.core.logging.SLF4JLogDelegateFactory')
        LOG = LoggerFactory.getLogger(Main)

        // Configure the HTTP(S) server
        def httpOptions = [
            maxWebsocketFrameSize: 1000000,
            //ssl: true,                                    // Enable SSL/TLS
            //pemKeyCertOptions:[
            //    keyPath:"/path/to/your/server-key.pem",
            //    certPath:"/path/to/your/server-cert.pem"
            //]
            useAlpn: true,           // Allows HTTP/2 protocol with HTTP/1.1 fallback
            logActivity: true
        ]
        def server = vertx.createHttpServer(httpOptions)

        // Define callbacks to handle completion of asynchronous events
        def httpFuture = Future.future()
        def pingFuture = Future.future()

        vertx.deployVerticle('groovy:zomato.PingVerticle', pingFuture.completer())
        server.listen(8080, '0.0.0.0', httpFuture.completer())

        // Once the attempt to start the web server and PingVerticle have completed, handle the asynchronous results
        CompositeFuture.join(httpFuture, pingFuture).setHandler({ res ->
            if (res.succeeded()) {      // Both async events completed successfully
                LOG.debug('Successfully deployed verticles and started web server')
                startFuture.complete()
            } else {
                // One or more of the async events failed
                startFuture.fail(res.cause())
            }
        })
    }
}
