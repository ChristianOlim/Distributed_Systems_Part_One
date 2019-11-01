package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;

import java.util.logging.Logger;

import io.grpc.stub.StreamObserver;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {
    /* Reference for this server was adjusted using our gRPC Asynchronous
       Inventory Lab notes*/
    private static final Logger logger =
            Logger.getLogger(PasswordServiceImpl.class.getName());

    // Validate method
    @Override
    public void validate(ValidateRequest req,
                         StreamObserver<BoolValue> responseObserver) {
        try {
            byte[] salt = req.getSalt().toByteArray();
            char[] inputPassword = req.getPassword().toCharArray();
            byte[] hashedPassword = req.getHashedPassword().toByteArray();

            if(Passwords.isExpectedPassword(inputPassword, salt, hashedPassword)) {
                responseObserver.onNext(BoolValue.newBuilder().setValue(true).build());
            }
            else{
                responseObserver.onNext(BoolValue.newBuilder().setValue(false).build());
            }
        }
        catch(RuntimeException ex){
            responseObserver.onNext(BoolValue.newBuilder().setValue(false).build());
        }
    }

    // Hash Method
    @Override
    public void hash(HashPassRequest req,
                        StreamObserver<HashPassResult> responseObserver) {
        try {
            // Variables
            // These will be passed through with the gRPC request
            char[] hashString = inputPassword.toCharArray();
            String inputPassword = req.getPassword();
            byte[] hashPassword = Passwords.hash(hashString, salt);
            byte[] addSalt = Passwords.getNextSalt();

            responseObserver.onNext(HashPassResult.newBuilder().setUserId(req.getUserId())
                    .setSalt(ByteString.copyFrom(addSalt))
                    .setHashedPassword(ByteString.copyFrom(hashPassword))
                    .build());
        } catch (RuntimeException ex) {
            responseObserver.onNext(HashPassResult.newBuilder().getDefaultInstanceForType());
        }
        responseObserver.onCompleted();
    }

}
