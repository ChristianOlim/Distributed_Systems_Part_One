package ie.gmit.ds;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class PasswordServiceServer {
    /* Reference for this server was adjusted using our Introduction
        to gRPC Lab notes*/
    private Server gRPCserver;
    private static final Logger logger = Logger.getLogger(PasswordServiceServer.class.getName());

    // Main method
    public static void main(String[] args) throws IOException, InterruptedException {
        final PasswordServiceServer server = new PasswordServiceServer();
        server.start();
        server.blockUntilShutdown();
    }

    /* This start method creates a gRPC server using the PasswordServiceImplementation
         service, a defined port and then starts the server */
    private void start() throws IOException {
        // The port that the server should run
        int port = 50551;

        gRPCserver = ServerBuilder.forPort(port)
                .addService((BindableService) new PasswordServiceImpl())
                .build()
                .start();
        logger.info("Password Server started: listening on " + port);
    }

    // Stop method
    private void stop() {
        if (gRPCserver != null) {
            // Shuts down the server
            gRPCserver.shutdown();
        }
    }

    // Wait for the termination of the main thread as the grpc library uses daemon threads.
    private void blockUntilShutdown() throws InterruptedException {
        if (gRPCserver != null) {
            gRPCserver.awaitTermination();
        }
    }

}
