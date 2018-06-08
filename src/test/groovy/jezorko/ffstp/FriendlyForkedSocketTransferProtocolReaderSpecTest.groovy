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

    static longMessage = "w3qJi6kGe/lcHPKUgkGkZpb7JaC03VVSphdZDZKLLkwsYXfKlYeRmFn9OrbNwYwgBN2v5EYwO+Hg569cxumd7plDB+ha5f1" +
            "bGv589SwzlNSRyawSM6+coLTlP354awetJUIiLvJjp/I6zBME4rE2lvGqdlHSDKhDy8ZEvQXbkVybMdBaWNfnqNvNfEavE480Ra" +
            "nNcPWLRASWw8TQEFk5Hycy85mfVJ5Lj644ZWGDCuR6mJOi5/8ksPy1Ag6quzW80RFNukq38gpaqYKisfqXhaWaMMG0fMNzILGEK" +
            "qMpRm02AUTqqSabuLqdytZHYxFEIHk1AmjfYhCllOltNp4zGY/9sAlNhMl7KJlKEmLQDPexa9czFU/HwNeEdp5eC4PMnuHWSmYH" +
            "AhjQfV42CUK/oJlcU2X2r9Vj22Zm2q541H+S0nh5+XW1yTdtb2bHsptUTUkDkoMvfCBDZaH+5OtMLMbDxq+z4mdzuk+4hcQgOgc" +
            "hOsHBPJxw0xtXki+LWyfchLHj2otXayhmTZrGWJMctO0pSPPyDHKFWkMYU7PJso+vonIJE8T625Q+XvX9AY9lDBkOIvZ/HTAoNY" +
            "iOAzvYPCGR3DomNwKJIS6qJAJzeRRECWNS1u+d4LglJ77qqwHsbIEraj6npThgHW9W0uOJxJT584KmX62QCBW37robEZy8cbuaD" +
            "he16LAN/4aiU5l05OmDsnbPvUBYBUceioshTAxhS8reh8WYAP2PtnLDqSQ02RPiRj/N8Kj1105a+zH8XFHb+q731UhQ7R/mQjO5" +
            "1ITJoxIWr92g+OVnfiprQaysQWsqfmfl+KDdrxYSo8mE1LkzQuYf7VBI1feu+ttRXPwx+1pO1/Wo8M0/RuIT2TQkqcNyto/9AJj" +
            "Fh97KS2EMTCGLih5HBVhAvc92soPp5HSZU6KG/w2w6LUXDpq7cbycERu67rcVCJCtX6aC8/mUxInj0lZvHWA4pac+aiuBbOwBq+" +
            "q+JyW44J3v1lJjCUQUeXMCJt8tEIiVN246m5EcPPE7Go6XNWgwjX8GItMZ9GRpAzL2Tz3A2IXHngoLocyM4ctKXgdFGIB9DzbyQ" +
            "ymktFmTYsa/TXAodVfO2nuxfNzRW3oud1XI0G2fZsKzOVQESSOSli9dIHhl5/6q68EsLMHrqv7nZXggXS+WkiNJBFQ5s8Jmn23Q" +
            "yFV3Lnpb0dx8sXvazld1KHBNv8Zik1m0pClD8jYPfYAYRQdeSsvhjMyhCwqex4XYwD1P9jIDaWT0GdMiBn+NMGg1l44aO/E8HJG" +
            "bOm43lYgQLd8mAnN5FEQJY1LW753guCUnvuqrAexsgStqPqelOGAdb1bS44nElPnzgqZfrZAIFbfuuhsRnA=="

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
          "FFS;OK;4;ąćół;"                  | Message.ok("ąćół")
          "FFS;ÓK;4;ąćół;"                  | new Message<>("ÓK", "ąćół")
          "FFS;ERROR;2;):;"                 | Message.error("):")
          "FFS;ERROR_INVALID_STATUS;2;):;"  | Message.errorInvalidStatus("):")
          "FFS;ERROR_INVALID_PAYLOAD;2;):;" | Message.errorInvalidPayload("):")
          "FFS;DIE;3;x_X;"                  | Message.die("x_X")
          "FFS;OK;1368;${longMessage};"     | Message.ok(longMessage)
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
          "oh"                                              | InvalidHeaderException        | { true }
          "FFS;NOOOoo-"                                     | MissingDataException          | { it.receivedData == "NOOOoo-" }
          "FFS;OK;NOOOoo-"                                  | MissingDataException          | { it.receivedData == "NOOOoo-" }
          "FFS;OK;6;test-"                                  | MissingDataException          | { it.receivedData == "" }
          "FFS;OK;1;test;"                                  | MessageTooLongException       | { true }
    }

    def mockBuffer(String data) {
        def temporaryFile = createTempFile(this.class.getName(), "-test-buffer")
        temporaryFile.write(data)
        temporaryFiles.add(temporaryFile)
        return new BufferedReader(new FileReader(temporaryFile))
    }

}
