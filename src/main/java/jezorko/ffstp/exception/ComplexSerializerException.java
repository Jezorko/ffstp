package jezorko.ffstp.exception;

import jezorko.ffstp.Serializer;

public class ComplexSerializerException extends RuntimeException {
    public ComplexSerializerException(Class<? extends Serializer> serializerClass) {
        super("serializer " + serializerClass.getCanonicalName() + " is too complex not to require class information, " +
              "please provide the class you wish your data to be deserialized to");
    }
}
