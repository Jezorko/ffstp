package jezorko.ffstp

import jezorko.ffstp.exception.InvalidHeaderException
import jezorko.ffstp.exception.InvalidMessageLengthException
import jezorko.ffstp.exception.MessageTooLongException
import jezorko.ffstp.exception.MissingDataException
import spock.lang.Specification
import spock.lang.Unroll

import static java.io.File.createTempFile
import static java.lang.System.err
import static jezorko.ffstp.Status.UNKNOWN

class FriendlyForkedSocketTransferProtocolReaderSpecTest extends Specification {

    List<File> temporaryFiles = []

    void cleanup() {
        temporaryFiles.each {
            if (!it.delete()) {
                err.println("Temporary test file ${it.getAbsolutePath()} could not be deleted")
            }
        }
    }

    @Unroll
    "should parse '#message' into #expectedResult"() {
        given:
          def buffer = mockBuffer message
          def reader = new FriendlyForkedSocketTransferProtocolReader(buffer)

        when:
          def actualResult = reader.readMessageRethrowErrors()

        then:
          expectedResult == actualResult

        where:
          message                           | expectedResult
          "FFS;;0;;"                        | new Message<>("", "")
          "FFS;UNKNOWN;0;;"                 | new Message<>(UNKNOWN, "")
          "FFS;UNKNOWN;4;test;"             | new Message<>(UNKNOWN, "test")
          "FFS;OK;0;;"                      | Message.ok("")
          "FFS;OK;4;test;"                  | Message.ok("test")
          "FFS;ERROR;2;):;"                 | Message.error("):")
          "FFS;ERROR_INVALID_STATUS;2;):;"  | Message.errorInvalidStatus("):")
          "FFS;ERROR_INVALID_PAYLOAD;2;):;" | Message.errorInvalidPayload("):")
          "FFS;DIE;3;x_X;"                  | Message.die("x_X")
    }

    @Unroll
    "should throw #expectedException.simpleName after trying to parse #message"() {
        given:
          def buffer = mockBuffer message
          def reader = new FriendlyForkedSocketTransferProtocolReader(buffer)

        when:
          reader.readMessageRethrowErrors()

        then:
          def actualException = thrown expectedException
          assert exceptionCheck(actualException)

        where:
          message                                           | expectedException             | exceptionCheck
          "ABC;OK;4;test;"                                  | InvalidHeaderException        | { true }
          "FFS;OK;-1;test;"                                 | InvalidMessageLengthException | { true }
          "FFS;OK;This is not a non-negative integer;test;" | InvalidMessageLengthException | { true }
          "oh"                                              | MissingDataException          | { it.receivedData == null }
          "FFS;NOOOoo-"                                     | MissingDataException          | { it.receivedData == "NOOOoo-" }
          "FFS;OK;NOOOoo-"                                  | MissingDataException          | { it.receivedData == "NOOOoo-" }
          "FFS;OK;6;test-"                                  | MissingDataException          | { it.receivedData == null }
          "FFS;OK;1;test;"                                  | MessageTooLongException       | { true }
    }

    def mockBuffer(String data) {
        def temporaryFile = createTempFile(this.class.getName(), "-test-buffer")
        temporaryFile.write(data)
        temporaryFiles.add(temporaryFile)
        return new BufferedReader(new FileReader(temporaryFile))
    }

}
