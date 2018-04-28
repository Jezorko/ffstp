package jezorko.ffstp

import jezorko.ffstp.exception.ProtocolReaderInitializationException
import jezorko.ffstp.exception.ProtocolWriterInitializationException
import spock.lang.Specification

class FriendlyTemplateSpecTest extends Specification {

    def "should throw if writer initialization fails"() {
        given:
          def socket = Mock Socket

        when:
          new FriendlyTemplate<>(socket, _ as Serializer)

        then:
          1 * socket.getOutputStream() >> { throw new RuntimeException("wopsie!") }
          0 * _._

        and:
          thrown ProtocolWriterInitializationException
    }

    def "should throw if reader initialization fails"() {
        given:
          def socket = Mock Socket

        when:
          new FriendlyTemplate<>(socket, _ as Serializer)

        then:
          1 * socket.getOutputStream() >> Mock(OutputStream)
          1 * socket.getInputStream() >> { throw new RuntimeException("wopsie!") }
          0 * _._

        and:
          thrown ProtocolReaderInitializationException
    }

}
