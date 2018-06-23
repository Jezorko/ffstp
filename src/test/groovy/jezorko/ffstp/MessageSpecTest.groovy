package jezorko.ffstp

import spock.lang.Specification
import spock.lang.Unroll

class MessageSpecTest extends Specification {

    @Unroll
    "should stringify message with status=#status and data='#data' and size=#dataLength as #expectedString"() {
        given:
          def message = new Message<String>(status, data, dataLength)

        when:
          def actualString = message.toString()

        then:
          expectedString == actualString

        where:
          status | data   | dataLength || expectedString
          "OK"   | "test" | 4          || "Message(4)[OK;test]"
          null   | "test" | 4          || "Message(4)[test]"
          "OK"   | null   | 0          || "Message(0)[OK]"
          "OK"   | "test" | -1         || "Message[OK;test]"
          null   | "test" | -1         || "Message[test]"
          "OK"   | null   | -1         || "Message[OK]"
          null   | null   | 0          || "Message(0)"
          null   | null   | -1         || "Message.empty"
    }

}
