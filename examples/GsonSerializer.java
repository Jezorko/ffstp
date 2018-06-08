package jezorko.ffstp.serializers;

import com.google.gson.Gson;
import jezorko.ffstp.Serializer;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_16;

public class GsonSerializer implements Serializer<Object> {

    private final static Charset MESSAGE_ENCODING = UTF_16;

    private final Gson gson = new Gson();

    @Override
    public byte[] serialize(Object data) {
        return gson.toJson(data).getBytes(MESSAGE_ENCODING);
    }

    @Override
    public <Y> Y deserialize(byte[] data, Class<Y> clazz) {
        return gson.fromJson(new String(data, MESSAGE_ENCODING), clazz);
    }
}
