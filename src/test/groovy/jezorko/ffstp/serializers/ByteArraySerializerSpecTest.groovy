package jezorko.ffstp.serializers

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class ByteArraySerializerSpecTest extends Specification {

    @Subject
    def serializer = new ByteArraySerializer()

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
                  new ByteArraySerializer.ByteArray(null),
                  new ByteArraySerializer.ByteArray([] as byte[]),
                  new ByteArraySerializer.ByteArray([1, 2, 3] as byte[])
          ]
    }

    def "should throw if provided array is null"() {
        when:
          serializer.serialize null

        then:
          thrown NullPointerException
    }

}
