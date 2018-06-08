package jezorko.ffstp.serializers

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class StringSerializerSpecTest extends Specification {

    @Subject
    def serializer = new StringSerializer()

    @Unroll
    "should serialize and deserialize #data as equal"() {
       when:
         def serializedResult = serializer.serialize data

        and:
          def deserializedResult = serializer.deserialize serializedResult

        then:
          deserializedResult == data

        where:
          data << [
                  null,
                  "",
                  "test",
                  "ńóń-ąśći-test"
          ]
    }

}
