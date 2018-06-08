package jezorko.ffstp

import jezorko.ffstp.exception.InvalidStatusException
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static java.nio.charset.StandardCharsets.US_ASCII
import static jezorko.ffstp.TestUtils.asciiBytesOf

class FriendlyForkedSocketTransferProtocolWriterSpecTest extends Specification {

    def outputStream = new ByteArrayOutputStream(50)
    def buffer = Spy(DataOutputStream, constructorArgs: [outputStream]) as DataOutputStream

    @Subject
    def writer = new FriendlyForkedSocketTransferProtocolWriter(buffer)

    @Unroll
    "should send #givenMessage as '#expectedParsedMessage'"() {
        when:
          writer.writeMessage givenMessage

        then: "header is written"
          outputStream.toString(US_ASCII.name()) == expectedParsedMessage

        where:
          givenMessage                                        | expectedParsedMessage
          Message.EMPTY                                       | "FFS;UNKNOWN;0;;"
          Message.empty()                                     | "FFS;UNKNOWN;0;;"
          new Message<>(null as String, asciiBytesOf("test")) | "FFS;UNKNOWN;4;test;"
          Message.ok(null)                                    | "FFS;OK;0;;"
          Message.ok(asciiBytesOf("test"))                    | "FFS;OK;4;test;"
          Message.error(asciiBytesOf("):"))                   | "FFS;ERROR;2;):;"
          Message.errorInvalidStatus(asciiBytesOf("):"))      | "FFS;ERROR_INVALID_STATUS;2;):;"
          Message.errorInvalidPayload(asciiBytesOf("):"))     | "FFS;ERROR_INVALID_PAYLOAD;2;):;"
          Message.die(asciiBytesOf("x_X"))                    | "FFS;DIE;3;x_X;"
    }

    def "should throw if given status contains semicolons"() {
        when:
          writer.writeMessage(new Message<byte[]>("invalid;status", asciiBytesOf("test")))

        then:
          thrown InvalidStatusException
    }

    def "should rethrow any exceptions"() {
        when:
          writer.writeMessage Message.empty()

        then:
          1 * buffer._ >> { throw new Exception("oh no") }
          0 * buffer._

        and:
          thrown Exception
    }

}
