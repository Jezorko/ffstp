package jezorko.ffstp

import static java.nio.charset.StandardCharsets.US_ASCII
import static org.apache.commons.io.IOUtils.toInputStream

class TestUtils {
    static asciiBytesOf(String data) {
        return data.getBytes(US_ASCII)
    }

    static mockAsciiStream(String data) {
        new DataInputStream(toInputStream(data, US_ASCII))
    }
}
