package jezorko.ffstp

import jezorko.ffstp.exception.InvalidStatusException
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class FriendlyForkedSocketTransferProtocolWriterSpecTest extends Specification {

    def buffer = Mock PrintWriter

    @Subject
    def writer = new FriendlyForkedSocketTransferProtocolWriter(buffer)

    @Unroll
    "should send #givenMessage as '#expectedParsedMessage'"() {
        when:
          writer.writeMessage givenMessage

        then:
          1 * buffer.print(expectedParsedMessage)
          1 * buffer.flush()
          0 * buffer._

        where:
          givenMessage                          | expectedParsedMessage
          Message.EMPTY                         | "FFS;UNKNOWN;0;;"
          Message.empty()                       | "FFS;UNKNOWN;0;;"
          new Message<>(null as String, "test") | "FFS;UNKNOWN;4;test;"
          Message.ok(null)                      | "FFS;OK;0;;"
          Message.ok("test")                    | "FFS;OK;4;test;"
          Message.error("):")                   | "FFS;ERROR;2;):;"
          Message.errorInvalidStatus("):")      | "FFS;ERROR_INVALID_STATUS;2;):;"
          Message.errorInvalidPayload("):")     | "FFS;ERROR_INVALID_PAYLOAD;2;):;"
          Message.die("x_X")                    | "FFS;DIE;3;x_X;"
    }

    def "should throw if given status contains semicolons"() {
        when:
          writer.writeMessage(new Message<String>("invalid;status", "test"))

        then:
          thrown InvalidStatusException
    }

    def "should rethrow any exceptions"() {
        when:
          writer.writeMessage Message.empty()

        then:
          1 * buffer.print(_ as String) >> { throw new Exception("oh no") }
          0 * buffer._

        and:
          thrown Exception
    }

}
