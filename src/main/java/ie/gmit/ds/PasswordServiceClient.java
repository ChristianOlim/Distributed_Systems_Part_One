package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordServiceClient {
    /* Reference for this server was adjusted using our gRPC Asynchronous
       Inventory Lab notes*/
    private static final Logger logger = Logger.getLogger(PasswordServiceClient.class.getName());
    private final ManagedChannel channel;
    Scanner userLoginInfo = new Scanner(System.in);
    // These stubs are used to call methods on
    private final PasswordServiceGrpc.PasswordServiceStub asyncPasswordClient;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub syncPasswordClient;

    public PasswordServiceClient(String host, int port) {
        // Here we create a Channel to connect to the gRPC server
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        syncPasswordService = PasswordServiceGrpc.newBlockingStub(channel);
        asyncPasswordService = PasswordServiceGrpc.newStub(channel);
    }

    // Shutdown method
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // Hash Password is added synchronously by calling the blocking stub
    public void hashPasswordRequest() {
        userLoginInfo();
        HashPassRequest hashPassRequest = HashPassRequest.newBuilder()
                                                            .setUserId(userId)
                                                            .setPassword(password)
                                                            .build();
        HashPassResult result;

        try {
            logger.info("Adding Hash Request Now ");
            result = syncPasswordService.hash(hashPassRequest);
            salt = result.getSalt();
            hashedPassword = result.getHashPassRequest();
        }
        catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }
    } // End

    // Validate Password Method
    private void validatePasswordRequest() {
        //Here we pass in a StreamObserver to handle the asynchronous result from the server
        StreamObserver<BoolValue> responseObserver = new StreamObserver<BoolValue>() {
            @Override
            public void onNext(BoolValue boolValue) {
                if (boolValue.getValue()){
                    System.out.println("Validation has been a success.");
                }
                else{
                    System.out.println("Validation has failed.");
                }
            }
            @Override
            public void onError(Throwable throwable) {

                System.out.println("There has been an error.");
            }
            @Override
            public void onCompleted() {
                logger.info("Finished receiving items");
                System.exit(0);
            }
        };

        try {
            logger.info("Validating the request.");
            asyncPasswordService.validate(ValidateRequest.newBuilder()
                                                            .build(),
                                                            responseObserver);
            logger.info("Returned from validating the request.");
        } catch (
                StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }
    }

    // Input Variables
    int userId = userLoginInfo.nextInt();
    String password = userLoginInfo.nextLine();
    private ByteString hashedPassword;
    private ByteString salt;

    // User Input before process begins
    public void userLoginInfo() {
        System.out.println("=======================================================================");
        System.out.println("  Distributed Systems Assignment Part 1 - Christian Olim - G00334621   ");
        System.out.println("=======================================================================");
        System.out.println("\nPlease enter your User ID: " + userId);
        System.out.println("Please enter your Password: " + password);
    }

    // Main Method and Output
    public static void main(String[] args) throws Exception {
        PasswordServiceClient client = new PasswordServiceClient("localhost", 50551);
        try {
            client.hashPasswordRequest();
            client.validatePasswordRequest();
            System.out.println("=======================================================================");
            System.out.println("  Distributed Systems Assignment Part 1 - Christian Olim - G00334621   ");
            System.out.println("=======================================================================");
            System.out.println("\nYour User ID: " + client.userId);
            System.out.println("Your Password: " + client.password);
            System.out.println("Hashed Password: " + client.hashedPassword.toByteArray().toString());
            System.out.println("Salt: " + client.salt.toByteArray().toString());
        }
        finally {
            // Keeps alive to receive async response
            Thread.currentThread().join();
        }
    }
}