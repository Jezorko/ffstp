package jezorko.ffstp;

import com.google.gson.Gson;

public class GsonSerializer implements Serializer<Object> {
    private final Gson gson = new Gson();

    @Override
    public String serialize(Object data) {
        return gson.toJson(data);
    }

    @Override
    public <Y> Y deserialize(String data, Class<Y> clazz) {
        return gson.fromJson(data, clazz);
    }
}